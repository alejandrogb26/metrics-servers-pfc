package local.alejandrogb.metricsservers.api.services.servidor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;
import local.alejandrogb.metricsservers.api.services.minio.MinioService;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.dao.MongoDao;
import local.alejandrogb.metricsservers.models.metrics.MetricPoint;
import local.alejandrogb.metricsservers.models.servidor.Servidor;
import local.alejandrogb.metricsservers.models.servidor.ServidorDTO;
import local.alejandrogb.metricsservers.models.servidor.ServidorInfo;
import local.alejandrogb.metricsservers.utils.BulkResult;

public class ServidorService {

	private final DaoApi dao = DaoApi.getInstance();
	private final MongoDao daoMongo = new MongoDao();
	private final ServidorProbeService probe = new ServidorProbeService();
	private final MinioService minioService = new MinioService();

	private static final Pattern DNS_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+$");
	// Pool reutilizable para no sobrecargar el sistema
	private static final ExecutorService pool = Executors.newFixedThreadPool(10);

	public Servidor findServidorById(int id) {
		Servidor servidor = dao.findServidorById(id);

		if (servidor != null)
			servidor.setImagenUrl(minioService.getUrlImagen(MinioService.BUCKET_SERVIDORES, servidor.getImagen()));

		return servidor;
	}

	public List<Servidor> findServidores() {
		List<Servidor> servidores = dao.findAllServidor();

		servidores
				.forEach(s -> s.setImagenUrl(minioService.getUrlImagen(MinioService.BUCKET_SERVIDORES, s.getImagen())));

		return servidores;
	}

	public BulkResult insertServidores(List<ServidorDTO> list) {
		BulkResult result = new BulkResult();
		result.setTotal(list.size());

		// Procesamiento en paralelo
		List<CompletableFuture<Void>> futures = list.stream()
				.map(dto -> CompletableFuture.runAsync(() -> processOne(dto, result), pool)).toList();

		// Esperamos a que todos terminen para devolver la respuesta
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		return result;
	}

	private void processOne(ServidorDTO dto, BulkResult result) {
		try {
			validateDto(dto);
			Servidor s = ServidorDTO.mapDtoToServidor(dto);

			// Llamada de red (Enriquecimiento)
			ServidorInfo info = probe.askServer(s.getDns());

			s.setHostname(info.hostname());
			s.setPrettyOs(info.os());
			s.setArch(info.arch());
			s.setKernel(info.kernel());

			dao.insertServidor(s);

			// Ya no hace falta bloque synchronized
			result.incrementOk();

		} catch (Exception e) {
			result.incrementFailed();
			result.addError(dto.dns + " --> " + e.getMessage());
		}
	}

	public void updateServidor(int id, Map<String, Object> fields) {
		if (fields == null || fields.isEmpty())
			throw new ValidationException("No hay campos para actualizar");

		// Validamos que el DNS sea correcto si viene en el PATCH
		if (fields.containsKey("dns")) {
			String dns = (String) fields.get("dns");
			if (dns == null || !DNS_PATTERN.matcher(dns).matches()) {
				throw new ValidationException("DNS inválido en la actualización");
			}
		}

		boolean updated = dao.updateServidor(id, fields);
		if (!updated)
			throw new NotFoundException("Servidor no encontrado");
	}

	public boolean deleteServidor(int id) {
		return dao.deleteServidor(id);
	}

	public BulkResult deleteServidores(List<Integer> ids) {
		BulkResult result = new BulkResult();
		if (ids == null || ids.isEmpty())
			return result;

		result.setTotal(ids.size());
		int deleted = dao.deleteServidoresById(ids);

		// Actualizamos contadores atómicos
		for (int i = 0; i < deleted; i++)
			result.incrementOk();

		int fallidos = ids.size() - deleted;
		for (int i = 0; i < fallidos; i++) {
			result.incrementFailed();
		}

		if (fallidos > 0)
			result.addError("Algunos IDs no existían en la base de datos");

		return result;
	}

	public int addServicios(int servidorId, List<Integer> servicioIds) {
		return dao.addServiciosToServidor(servidorId, servicioIds);
	}

	public int removeServicios(int servidorId, List<Integer> servicioIds) {
		return dao.removeServiciosFromServidor(servidorId, servicioIds);
	}

	public void actualizarFotoPerfil(int serverId, InputStream stream, String nuevoNombre) throws Exception {
		// A. Subir el chorro de bytes a MinIO
		// Usamos el bucket 'servidores' y el nombre que generamos
		minioService.uploadArchivo(MinioService.BUCKET_SERVIDORES, nuevoNombre, stream);

		// B. Guardar el nombre en la BD
		Map<String, Object> campos = new HashMap<>();
		campos.put(Servidor.COL_IMAGEN, nuevoNombre);

		boolean ok = dao.updateServidor(serverId, campos);

		if (!ok)
			throw new Exception("No se pudo actualizar la ruta en la base de datos");
	}

	public List<MetricPoint> getServidorHistory(String serverId, Long minutes) {
		return daoMongo.getMetrics(serverId, minutes);
	}

	private void validateDto(ServidorDTO dto) {
		if (dto == null)
			throw new ValidationException("DTO null");
		if (dto.dns == null || !DNS_PATTERN.matcher(dto.dns).matches())
			throw new ValidationException("DNS inválido");
	}
}
