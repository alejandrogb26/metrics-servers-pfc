package local.alejandrogb.metricsservers.models;

import java.util.List;
import java.util.Map;

public class PermissionMap<T> {
	private List<T> global;
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
