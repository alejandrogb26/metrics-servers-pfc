package local.alejandrogb.metricsservers.models;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PermissionMap", description = "Estructura que agrupa permisos globales y permisos específicos por sección")
public class PermissionMap<T> {
	@Schema(description = "Lista de permisos globales aplicables a todo el sistema", example = "[1, 2, 3]")
	private List<T> global;

	@Schema(description = "Mapa de permisos por sección. La clave es el ID de la sección", example = "{\"10\": [1,2], \"20\": [3]}")
	private Map<Integer, List<T>> sections; // Clave: seccionId

	public PermissionMap() {
	}

	public PermissionMap(List<T> global, Map<Integer, List<T>> sections) {
		super();
		this.global = global;
		this.sections = sections;
	}

	public List<T> getGlobal() {
		return global;
	}

	public void setGlobal(List<T> global) {
		this.global = global;
	}

	public Map<Integer, List<T>> getSections() {
		return sections;
	}

	public void setSections(Map<Integer, List<T>> sections) {
		this.sections = sections;
	}

	@Override
	public String toString() {
		return "PermissionMap [global=" + global + ", sections=" + sections + "]";
	}
}
