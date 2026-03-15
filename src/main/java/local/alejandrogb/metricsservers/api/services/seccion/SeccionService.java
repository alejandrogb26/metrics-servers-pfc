package local.alejandrogb.metricsservers.api.services.seccion;

import java.util.List;
import java.util.Map;

import jakarta.ws.rs.NotFoundException;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Seccion;

/**
 * Servicio de negocio encargado de gestionar las operaciones relacionadas con
 * las secciones del sistema.
 *
 * <p>
 * Esta clase actúa como capa intermedia entre los recursos REST de la API y la
 * capa de persistencia representada por {@link DaoApi}.
 * </p>
 *
 * <p>
 * Sus responsabilidades principales son:
 * </p>
 *
 * <ul>
 * <li>Recuperar secciones existentes.</li>
 * <li>Crear nuevas secciones.</li>
 * <li>Actualizar información de secciones.</li>
 * <li>Eliminar secciones del sistema.</li>
 * </ul>
 *
 * <p>
 * La lógica de acceso a datos se delega completamente al DAO, mientras que este
 * servicio se encarga de aplicar validaciones básicas y gestionar las
 * excepciones adecuadas para la API.
 * </p>
 */
public class SeccionService {

	/**
	 * Instancia del DAO utilizada para acceder a la capa de persistencia.
	 */
	private final DaoApi dao = DaoApi.getInstance();

	/**
	 * Recupera una sección a partir de su identificador.
	 *
	 * @param id identificador de la sección
	 * @return objeto {@link Seccion} correspondiente
	 * @throws NotFoundException si no existe una sección con el identificador
	 *                           indicado
	 */
	public Seccion findById(int id) {
		Seccion seccion = dao.findSeccionById(id);

		if (seccion == null)
			throw new NotFoundException("Sección no encontrada");

		return seccion;
	}

	/**
	 * Recupera todas las secciones registradas en el sistema.
	 *
	 * @return lista de secciones disponibles
	 */
	public List<Seccion> findAll() {
		return dao.findAllSeccion();
	}

	/**
	 * Inserta una nueva sección en el sistema.
	 *
	 * @param seccion objeto {@link Seccion} con la información de la sección
	 * @return identificador generado para la nueva sección
	 */
	public int insert(Seccion seccion) {
		return dao.insertSeccion(seccion);
	}

	/**
	 * Actualiza parcialmente los datos de una sección existente.
	 *
	 * <p>
	 * Los campos a modificar se proporcionan mediante un mapa donde cada clave
	 * representa una columna de la base de datos y su valor el nuevo contenido.
	 * </p>
	 *
	 * @param id     identificador de la sección a actualizar
	 * @param fields mapa de campos y valores a actualizar
	 * @throws NotFoundException si la sección no existe
	 */
	public void update(int id, Map<String, Object> fields) {

		if (!dao.updateSeccion(id, fields)) {
			throw new NotFoundException("Sección no encontrada para actualizar");
		}
	}

	/**
	 * Elimina una sección del sistema.
	 *
	 * <p>
	 * Si existen servidores asociados a esta sección, la base de datos puede
	 * impedir la eliminación mediante restricciones de integridad referencial
	 * (foreign key).
	 * </p>
	 *
	 * <p>
	 * En ese caso, {@link DaoApi} lanzará una excepción que deberá ser gestionada
	 * por un {@code ExceptionMapper} global de la API.
	 * </p>
	 *
	 * @param id identificador de la sección a eliminar
	 * @return {@code true} si la sección fue eliminada correctamente
	 */
	public boolean delete(int id) {
		return dao.deleteSeccion(id);
	}
}