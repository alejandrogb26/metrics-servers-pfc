package local.alejandrogb.metricsservers.api.services.servicio;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;
import local.alejandrogb.metricsservers.api.services.minio.MinioService;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Servicio;

/**
 * Servicio de negocio encargado de gestionar las operaciones relacionadas con
 * los servicios registrados en el sistema.
 *
 * <p>
 * Esta clase actúa como intermediario entre los recursos REST de la API y la
 * capa de persistencia representada por {@link DaoApi}. Además, integra el
 * acceso a recursos multimedia almacenados en MinIO mediante
 * {@link MinioService}.
 * </p>
 *
 * <p>
 * Sus responsabilidades incluyen:
 * </p>
 *
 * <ul>
 * <li>Recuperar servicios registrados.</li>
 * <li>Asignar URLs de acceso a los logos almacenados en MinIO.</li>
 * <li>Crear nuevos servicios.</li>
 * <li>Actualizar información de servicios.</li>
 * <li>Eliminar servicios existentes.</li>
 * </ul>
 */
public class ServicioService {

	/**
	 * Instancia del DAO utilizada para acceder a la capa de persistencia.
	 */
	private final DaoApi dao = DaoApi.getInstance();

	/**
	 * Servicio encargado de gestionar el acceso a archivos almacenados en MinIO.
	 */
	private final MinioService minioService = new MinioService();

	/**
	 * Recupera un servicio a partir de su identificador.
	 *
	 * <p>
	 * Si el servicio existe y dispone de un logo asociado, se genera la URL pública
	 * correspondiente mediante {@link MinioService}.
	 * </p>
	 *
	 * @param id identificador del servicio
	 * @return objeto {@link Servicio} encontrado o {@code null} si no existe
	 */
	public Servicio findById(int id) {

		Servicio servicio = dao.findServicioById(id);

		if (servicio != null) {
			servicio.setUrlLogo(minioService.getUrlImagen(MinioService.BUCKET_SERVICIOS, servicio.getLogo()));
		}

		return servicio;
	}

	/**
	 * Recupera todos los servicios registrados en el sistema.
	 *
	 * <p>
	 * Para cada servicio recuperado se genera la URL pública del logo almacenado en
	 * MinIO.
	 * </p>
	 *
	 * @return lista de servicios con sus URLs de logo configuradas
	 */
	public List<Servicio> findAll() {

		List<Servicio> lista = dao.findAllServicio();

		lista.forEach(s -> s.setUrlLogo(minioService.getUrlImagen(MinioService.BUCKET_SERVICIOS, s.getLogo())));

		return lista;
	}

	/**
	 * Inserta un nuevo servicio en el sistema.
	 *
	 * <p>
	 * Se valida que los datos mínimos del servicio estén presentes antes de delegar
	 * la operación al DAO.
	 * </p>
	 *
	 * @param servicio objeto {@link Servicio} a insertar
	 * @return identificador generado para el nuevo servicio
	 * @throws ValidationException si los datos proporcionados son inválidos
	 */
	public int insert(Servicio servicio) {

		if (servicio == null || servicio.getNombre() == null) {
			throw new ValidationException("Datos de servicio inválidos");
		}

		return dao.insertServicio(servicio);
	}

	/**
	 * Actualiza parcialmente un servicio existente.
	 *
	 * <p>
	 * Solo se permiten actualizar ciertos campos específicos para garantizar la
	 * integridad del modelo de datos.
	 * </p>
	 *
	 * @param id     identificador del servicio a actualizar
	 * @param fields mapa de campos y valores a modificar
	 * @throws ValidationException si los campos proporcionados no son válidos
	 * @throws NotFoundException   si el servicio no existe
	 */
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

	/**
	 * Elimina un servicio del sistema.
	 *
	 * @param id identificador del servicio a eliminar
	 * @return {@code true} si el servicio fue eliminado correctamente
	 */
	public boolean delete(int id) {
		return dao.deleteServicio(id);
	}
}