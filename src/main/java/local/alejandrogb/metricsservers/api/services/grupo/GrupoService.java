package local.alejandrogb.metricsservers.api.services.grupo;

import java.util.List;
import java.util.Map;

import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Grupo;
import local.alejandrogb.metricsservers.utils.BulkResult;

/**
 * Servicio de negocio encargado de gestionar las operaciones relacionadas con
 * los grupos del sistema.
 *
 * <p>
 * Esta clase actúa como capa intermedia entre los recursos de la API
 * (controladores REST) y la capa de persistencia representada por
 * {@link DaoApi}.
 * </p>
 *
 * <p>
 * Sus responsabilidades principales son:
 * </p>
 *
 * <ul>
 * <li>Orquestar operaciones de creación, actualización y eliminación de
 * grupos.</li>
 * <li>Gestionar operaciones masivas (bulk) mediante {@link BulkResult}.</li>
 * <li>Aplicar validaciones o lógica de negocio antes de acceder al DAO.</li>
 * </ul>
 *
 * <p>
 * El acceso a la base de datos se delega completamente al DAO, manteniendo así
 * una separación clara entre lógica de negocio y persistencia.
 * </p>
 */
public class GrupoService {

	/**
	 * Instancia del DAO principal utilizada para acceder a la capa de persistencia.
	 */
	private final DaoApi dao = DaoApi.getInstance();

	/**
	 * Recupera todos los grupos registrados en el sistema.
	 *
	 * @return lista de grupos existentes
	 */
	public List<Grupo> getAll() {
		return dao.findAllGrupos();
	}

	/**
	 * Recupera un grupo a partir de su identificador.
	 *
	 * @param id identificador del grupo
	 * @return grupo encontrado o {@code null} si no existe
	 */
	public Grupo getById(int id) {
		return dao.findGrupoById(id);
	}

	/**
	 * Crea múltiples grupos en el sistema.
	 *
	 * <p>
	 * Cada grupo se inserta individualmente delegando la operación al método
	 * {@link DaoApi#insertGrupo(Grupo)}. El DAO se encarga de gestionar
	 * internamente la transacción necesaria para insertar el grupo y sus permisos
	 * asociados.
	 * </p>
	 *
	 * <p>
	 * El resultado de la operación se encapsula en un objeto {@link BulkResult},
	 * que indica cuántos registros se procesaron correctamente y cuáles fallaron.
	 * </p>
	 *
	 * @param grupos lista de grupos a crear
	 * @return resultado de la operación masiva
	 */
	public BulkResult createGrupos(List<Grupo> grupos) {

		BulkResult result = new BulkResult();

		if (grupos == null || grupos.isEmpty())
			return result;

		result.setTotal(grupos.size());

		for (Grupo g : grupos) {

			try {
				dao.insertGrupo(g);
				result.incrementOk();

			} catch (Exception e) {

				result.incrementFailed();
				result.addError("Error al crear grupo '" + g.getNombre() + "': " + e.getMessage());
			}
		}

		return result;
	}

	/**
	 * Actualiza parcialmente un grupo existente.
	 *
	 * <p>
	 * Este método implementa la lógica de un {@code PATCH} sobre la entidad grupo.
	 * Primero se verifica que el grupo exista y posteriormente se delega la
	 * actualización al DAO.
	 * </p>
	 *
	 * <p>
	 * El DAO se encarga de aplicar la actualización de campos básicos y, si
	 * corresponde, de actualizar también los permisos asociados.
	 * </p>
	 *
	 * @param id identificador del grupo a actualizar
	 * @param g  objeto que contiene los nuevos valores
	 * @return {@code true} si el grupo fue actualizado correctamente, {@code false}
	 *         si el grupo no existe
	 */
	public boolean patchGrupo(int id, Grupo g) {

		// 1. Verificar existencia
		Grupo existente = dao.findGrupoById(id);

		if (existente == null)
			return false;

		try {

			// 3. Delegar actualización al DAO
			return dao.updateGrupo(id, g);

		} catch (Exception e) {

			throw new RuntimeException("Error al aplicar PATCH al grupo", e);
		}
	}

	/**
	 * Elimina múltiples grupos del sistema.
	 *
	 * <p>
	 * Cada grupo se elimina individualmente utilizando el método
	 * {@link DaoApi#deleteGrupo(int)}. En caso de que existan usuarios asociados al
	 * grupo, la base de datos puede impedir la eliminación debido a restricciones
	 * de integridad referencial.
	 * </p>
	 *
	 * <p>
	 * El resultado se devuelve encapsulado en un {@link BulkResult}.
	 * </p>
	 *
	 * @param ids lista de identificadores de grupos a eliminar
	 * @return resultado de la operación masiva
	 */
	public BulkResult deleteGrupos(List<Integer> ids) {

		BulkResult result = new BulkResult();

		if (ids == null || ids.isEmpty())
			return result;

		result.setTotal(ids.size());

		for (Integer id : ids) {

			try {

				boolean deleted = dao.deleteGrupo(id);

				if (deleted) {
					result.incrementOk();
				} else {
					result.incrementFailed();
					result.addError("ID " + id + " no encontrado.");
				}

			} catch (Exception e) {

				result.incrementFailed();
				result.addError("No se pudo eliminar el grupo " + id + ": Existen usuarios vinculados.");
			}
		}

		return result;
	}
}