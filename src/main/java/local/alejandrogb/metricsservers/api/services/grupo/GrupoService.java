package local.alejandrogb.metricsservers.api.services.grupo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Grupo;
import local.alejandrogb.metricsservers.utils.BulkResult;

public class GrupoService {
	private final DaoApi dao = DaoApi.getInstance();

	public List<Grupo> getAll() {
		return dao.findAllGrupos();
	}

	public Grupo getById(int id) {
		return dao.findGrupoById(id);
	}

	public BulkResult createGrupos(List<Grupo> grupos) {
		BulkResult result = new BulkResult();
		if (grupos == null || grupos.isEmpty())
			return result;

		result.setTotal(grupos.size());
		for (Grupo g : grupos) {
			try {
				// El DAO ya maneja la transacción interna para el grupo y sus permisos
				dao.insertGrupo(g);
				result.incrementOk();
			} catch (Exception e) {
				result.incrementFailed();
				result.addError("Error al crear grupo '" + g.getNombre() + "': " + e.getMessage());
			}
		}
		return result;
	}

	public boolean patchGrupo(int id, Grupo g) {
		// 1. Verificar si existe
		Grupo existente = dao.findGrupoById(id);
		if (existente == null)
			return false;

		// 2. Construir el mapa de campos a actualizar para la tabla 'grupos'
		Map<String, Object> fieldsToUpdate = new LinkedHashMap<>();

		if (g.getNombre() != null) {
			fieldsToUpdate.put(Grupo.COL_NOMBRE, g.getNombre());
		}

		// Para booleanos, si usas la clase 'Boolean' (objeto) puedes detectar si es
		// null.
		fieldsToUpdate.put(Grupo.COL_SUPER_ADMIN, g.isSuperAdmin());

		try {
			// 3. Ejecutar la actualización en el DAO
			// Pasamos el objeto 'g' completo porque el DAO decidirá si actualizar
			// los permisos basándose en si g.getPermisos() es null o no.
			return dao.updateGrupo(id, g);
		} catch (Exception e) {
			throw new RuntimeException("Error al aplicar PATCH al grupo", e);
		}
	}

	public BulkResult deleteGrupos(List<Integer> ids) {
		BulkResult result = new BulkResult();
		if (ids == null || ids.isEmpty())
			return result;

		result.setTotal(ids.size());
		for (Integer id : ids) {
			try {
				// El deleteSingleGrupo es un DELETE simple de la tabla 'grupos'
				// La BD se encarga del ON DELETE CASCADE en las tablas de permisos
				boolean deleted = dao.deleteGrupo(id);
				if (deleted) {
					result.incrementOk();
				} else {
					result.incrementFailed();
					result.addError("ID " + id + " no encontrado.");
				}
			} catch (Exception e) {
				result.incrementFailed();
				// Captura típicamente el error de FK si hay usuarios en el grupo
				result.addError("No se pudo eliminar el grupo " + id + ": Existen usuarios vinculados.");
			}
		}
		return result;
	}
}