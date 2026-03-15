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

/**
 * Servicio de negocio encargado de gestionar las operaciones relacionadas con
 * los servidores monitorizados por el sistema.
 *
 * <p>
 * Esta clase actúa como capa intermedia entre los recursos REST y la capa de
 * persistencia. Además coordina múltiples subsistemas:
 * </p>
 *
 * <ul>
 * <li>{@link DaoApi} para el acceso a datos relacionales (MariaDB).</li>
 * <li>{@link MongoDao} para el acceso a métricas históricas almacenadas en
 * MongoDB.</li>
 * <li>{@link MinioService} para la gestión de imágenes almacenadas en
 * MinIO.</li>
 * <li>{@link ServidorProbeService} para obtener información del servidor
 * mediante llamadas de red.</li>
 * </ul>
 *
 * <p>
 * Este servicio también implementa procesamiento concurrente para operaciones
 * masivas de inserción utilizando {@link CompletableFuture} y un pool de hilos.
 * </p>
 */
public class ServidorService {

	private final DaoApi dao = DaoApi.getInstance();
	private final MongoDao daoMongo = new MongoDao();
	private final ServidorProbeService probe = new ServidorProbeService();
	private final MinioService minioService = new MinioService();

	/**
	 * Patrón de validación para nombres DNS válidos.
	 */
	private static final Pattern DNS_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+$");

	/**
	 * Pool de hilos reutilizable utilizado para operaciones paralelas en
	 * inserciones masivas de servidores.
	 */
	private static final ExecutorService pool = Executors.newFixedThreadPool(10);

	/**
	 * Recupera un servidor por su identificador.
	 *
	 * <p>
	 * Si el servidor tiene una imagen asociada, se genera la URL de acceso
	 * correspondiente mediante {@link MinioService}.
	 * </p>
	 *
	 * @param id identificador del servidor
	 * @return objeto {@link Servidor} o {@code null} si no existe
	 */
	public Servidor findServidorById(int id) {

		Servidor servidor = dao.findServidorById(id);

		if (servidor != null) {
			servidor.setImagenUrl(minioService.getUrlImagen(MinioService.BUCKET_SERVIDORES, servidor.getImagen()));
		}

		return servidor;
	}

	/**
	 * Recupera todos los servidores registrados en el sistema.
	 *
	 * <p>
	 * A cada servidor se le asigna la URL pública de su imagen almacenada en MinIO.
	 * </p>
	 *
	 * @return lista de servidores
	 */
	public List<Servidor> findServidores() {

		List<Servidor> servidores = dao.findAllServidor();

		servidores
				.forEach(s -> s.setImagenUrl(minioService.getUrlImagen(MinioService.BUCKET_SERVIDORES, s.getImagen())));

		return servidores;
	}

	/**
	 * Inserta múltiples servidores en el sistema utilizando procesamiento
	 * concurrente.
	 *
	 * <p>
	 * Este método está diseñado para gestionar la creación masiva de servidores de
	 * forma eficiente, especialmente cuando el número de elementos es elevado o
	 * cuando la operación de inserción implica tareas de red potencialmente lentas.
	 * </p>
	 *
	 * <p>
	 * Cada servidor recibido en la lista se procesa de forma independiente mediante
	 * ejecución asíncrona usando {@link CompletableFuture} y un
	 * {@link ExecutorService} compartido. Esto permite paralelizar operaciones
	 * costosas como la consulta de información remota del servidor.
	 * </p>
	 *
	 * <p>
	 * El flujo completo para cada servidor es el siguiente:
	 * </p>
	 *
	 * <ol>
	 * <li>Validación de los datos recibidos en el {@link ServidorDTO}.</li>
	 * <li>Conversión del DTO al modelo de dominio {@link Servidor}.</li>
	 * <li>Consulta remota al servidor mediante {@link ServidorProbeService} para
	 * obtener información adicional del sistema.</li>
	 * <li>Enriquecimiento del objeto {@link Servidor} con los datos obtenidos
	 * (hostname, sistema operativo, arquitectura y kernel).</li>
	 * <li>Inserción final del servidor en la base de datos mediante
	 * {@link DaoApi}.</li>
	 * </ol>
	 *
	 * <p>
	 * El método espera a que todas las tareas asíncronas finalicen antes de
	 * devolver el resultado final. Para ello se utiliza
	 * {@link CompletableFuture#allOf(CompletableFuture...)}.
	 * </p>
	 *
	 * <p>
	 * El resultado global de la operación se acumula en un objeto
	 * {@link BulkResult}, que contiene:
	 * </p>
	 *
	 * <ul>
	 * <li>Número total de servidores procesados.</li>
	 * <li>Número de inserciones correctas.</li>
	 * <li>Número de errores ocurridos durante el procesamiento.</li>
	 * <li>Lista de mensajes de error asociados a cada servidor fallido.</li>
	 * </ul>
	 *
	 * @param list lista de servidores representados mediante objetos
	 *             {@link ServidorDTO}
	 * @return resultado agregado de la operación masiva
	 */
	public BulkResult insertServidores(List<ServidorDTO> list) {

		BulkResult result = new BulkResult();
		result.setTotal(list.size());

		// Cada servidor se procesa en paralelo usando el pool de hilos
		List<CompletableFuture<Void>> futures = list.stream()
				.map(dto -> CompletableFuture.runAsync(() -> processOne(dto, result), pool)).toList();

		// Esperamos a que todas las tareas finalicen antes de devolver el resultado
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		return result;
	}

	/**
	 * Procesa la inserción de un único servidor dentro de una operación masiva.
	 *
	 * <p>
	 * Este método encapsula la lógica necesaria para registrar un servidor en el
	 * sistema. Se ejecuta normalmente dentro de una tarea asíncrona lanzada por
	 * {@link #insertServidores(List)}.
	 * </p>
	 *
	 * <p>
	 * El proceso consta de las siguientes etapas:
	 * </p>
	 *
	 * <ol>
	 * <li>Validación del DTO recibido para asegurar que los datos mínimos son
	 * correctos.</li>
	 * <li>Conversión del {@link ServidorDTO} a la entidad de dominio
	 * {@link Servidor}.</li>
	 * <li>Consulta al servidor remoto mediante {@link ServidorProbeService} para
	 * obtener información del sistema operativo.</li>
	 * <li>Actualización del objeto {@link Servidor} con la información
	 * obtenida.</li>
	 * <li>Persistencia final del servidor en la base de datos mediante
	 * {@link DaoApi}.</li>
	 * </ol>
	 *
	 * <p>
	 * Si ocurre cualquier error durante el proceso (validación, conexión remota o
	 * inserción en base de datos), el error se captura y se registra en el objeto
	 * {@link BulkResult} sin interrumpir el procesamiento del resto de servidores.
	 * </p>
	 *
	 * @param dto    objeto de transferencia de datos que representa el servidor a
	 *               registrar
	 * @param result acumulador compartido del resultado de la operación masiva
	 */
	private void processOne(ServidorDTO dto, BulkResult result) {

		try {

			validateDto(dto);

			Servidor s = ServidorDTO.mapDtoToServidor(dto);

			// Consulta remota al servidor para obtener información del sistema
			ServidorInfo info = probe.askServer(s.getDns());

			s.setHostname(info.hostname());
			s.setPrettyOs(info.os());
			s.setArch(info.arch());
			s.setKernel(info.kernel());

			// Persistencia final
			dao.insertServidor(s);

			result.incrementOk();

		} catch (Exception e) {

			result.incrementFailed();
			result.addError(dto.dns + " --> " + e.getMessage());
		}
	}

	/**
	 * Actualiza parcialmente un servidor existente.
	 *
	 * <p>
	 * Si se actualiza el DNS del servidor se valida que tenga un formato válido.
	 * </p>
	 *
	 * @param id     identificador del servidor
	 * @param fields campos a actualizar
	 * @throws ValidationException si los datos proporcionados no son válidos
	 * @throws NotFoundException   si el servidor no existe
	 */
	public void updateServidor(int id, Map<String, Object> fields) {

		if (fields == null || fields.isEmpty())
			throw new ValidationException("No hay campos para actualizar");

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

	/**
	 * Elimina un servidor del sistema.
	 *
	 * @param id identificador del servidor
	 * @return {@code true} si el servidor fue eliminado
	 */
	public boolean deleteServidor(int id) {
		return dao.deleteServidor(id);
	}

	/**
	 * Elimina múltiples servidores del sistema.
	 *
	 * @param ids lista de identificadores de servidores
	 * @return resultado de la operación masiva
	 */
	public BulkResult deleteServidores(List<Integer> ids) {

		BulkResult result = new BulkResult();

		if (ids == null || ids.isEmpty())
			return result;

		result.setTotal(ids.size());

		int deleted = dao.deleteServidoresById(ids);

		for (int i = 0; i < deleted; i++)
			result.incrementOk();

		int fallidos = ids.size() - deleted;

		for (int i = 0; i < fallidos; i++)
			result.incrementFailed();

		if (fallidos > 0)
			result.addError("Algunos IDs no existían en la base de datos");

		return result;
	}

	/**
	 * Asocia servicios a un servidor existente.
	 *
	 * @param servidorId  identificador del servidor
	 * @param servicioIds lista de identificadores de servicios
	 * @return número de relaciones creadas
	 */
	public int addServicios(int servidorId, List<Integer> servicioIds) {
		return dao.addServiciosToServidor(servidorId, servicioIds);
	}

	/**
	 * Elimina asociaciones entre servicios y un servidor.
	 *
	 * @param servidorId  identificador del servidor
	 * @param servicioIds lista de servicios a desvincular
	 * @return número de relaciones eliminadas
	 */
	public int removeServicios(int servidorId, List<Integer> servicioIds) {
		return dao.removeServiciosFromServidor(servidorId, servicioIds);
	}

	/**
	 * Actualiza la imagen asociada a un servidor.
	 *
	 * <p>
	 * El proceso consiste en subir el archivo a MinIO y posteriormente actualizar
	 * la referencia en la base de datos.
	 * </p>
	 *
	 * @param serverId    identificador del servidor
	 * @param stream      flujo de datos del archivo
	 * @param nuevoNombre nombre del archivo almacenado
	 * @throws Exception si ocurre un error durante el proceso
	 */
	public void actualizarFotoPerfil(int serverId, InputStream stream, String nuevoNombre) throws Exception {

		minioService.uploadArchivo(MinioService.BUCKET_SERVIDORES, nuevoNombre, stream);

		Map<String, Object> campos = new HashMap<>();
		campos.put(Servidor.COL_IMAGEN, nuevoNombre);

		boolean ok = dao.updateServidor(serverId, campos);

		if (!ok)
			throw new Exception("No se pudo actualizar la ruta en la base de datos");
	}

	/**
	 * Recupera el historial de métricas de un servidor desde MongoDB.
	 *
	 * @param serverId identificador del servidor
	 * @param minutes  ventana temporal de consulta
	 * @return lista de métricas registradas
	 */
	public List<MetricPoint> getServidorHistory(String serverId, Long minutes) {
		return daoMongo.getMetrics(serverId, minutes);
	}

	/**
	 * Valida los datos mínimos necesarios para crear un servidor.
	 *
	 * @param dto DTO del servidor
	 * @throws ValidationException si los datos no son válidos
	 */
	private void validateDto(ServidorDTO dto) {

		if (dto == null)
			throw new ValidationException("DTO null");

		if (dto.dns == null || !DNS_PATTERN.matcher(dto.dns).matches())
			throw new ValidationException("DNS inválido");
	}
}