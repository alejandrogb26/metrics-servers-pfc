package local.alejandrogb.metricsservers.api.services.servicio;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;
import local.alejandrogb.metricsservers.api.services.minio.MinioService;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Servicio;

public class ServicioService {

	private final DaoApi dao = DaoApi.getInstance();
	private final MinioService minioService = new MinioService();

	public Servicio findById(int id) {
		Servicio servicio = dao.findServicioById(id);

		if (servicio != null)
			servicio.setUrlLogo(minioService.getUrlImagen(MinioService.BUCKET_SERVICIOS, servicio.getLogo()));

		return servicio;
	}

	public List<Servicio> findAll() {
		List<Servicio> lista = dao.findAllServicio();

		lista.forEach(s -> s.setUrlLogo(minioService.getUrlImagen(MinioService.BUCKET_SERVICIOS, s.getLogo())));

		return lista;
	}

	public int insert(Servicio servicio) {
		if (servicio == null || servicio.getNombre() == null) {
			throw new ValidationException("Datos de servicio inválidos");
		}
		return dao.insertServicio(servicio);
	}

	public void update(int id, Map<String, Object> fields) {
		if (fields == null || fields.isEmpty())
			throw new ValidationException("No hay campos para actualizar");

		Map<String, Object> validFields = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			switch (entry.getKey()) {
			case "nombre" -> validFields.put("nombre", entry.getValue());
			case "puerto" -> validFields.put("puerto", entry.getValue());
			case "tipo" -> validFields.put("tipo", entry.getValue());
			default -> throw new ValidationException("Campo no permitido: " + entry.getKey());
			}
		}

		if (!dao.updateServicio(id, validFields)) {
			throw new NotFoundException("Servicio no encontrado para actualizar");
		}
	}

	public boolean delete(int id) {
		return dao.deleteServicio(id);
	}
}