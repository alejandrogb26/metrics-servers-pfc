package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Grupo {
	public static final String TABLE = "grupos";
	public static final String TABLE_GLOBAL_PRM = "grupo_permiso_global";
	public static final String TABLE_SECTION_PRM = "grupo_seccion";

	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_SUPER_ADMIN = "superadmin";

	public static final String COL_ID_BY_SESSION = "gid";
	public static final String COL_NOMBRE_BY_SESSION = "gnombre";

	private int id;
	private String nombre;
	private Boolean superAdmin;
	private PermissionMap<Integer> permisos;

	public Grupo() {
	}

	public Grupo(int id, String nombre, Boolean superAdmin) {
		this.id = id;
		this.nombre = nombre;
		this.superAdmin = superAdmin;
	}

	public Grupo(int id, String nombre, Boolean superAdmin, PermissionMap<Integer> permisos) {
		this.id = id;
		this.nombre = nombre;
		this.superAdmin = superAdmin;
		this.permisos = permisos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean isSuperAdmin() {
		return superAdmin;
	}

	public void setSuperAdmin(Boolean superAdmin) {
		this.superAdmin = superAdmin;
	}

	public PermissionMap<Integer> getPermisos() {
		return permisos;
	}

	public void setPermisos(PermissionMap<Integer> permisos) {
		this.permisos = permisos;
	}

	public static Grupo mapGrupo(ResultSet rs) {
		try {
			return new Grupo(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getBoolean(COL_SUPER_ADMIN));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static Grupo mapGrupoBySession(ResultSet rs) {
		try {
			return new Grupo(rs.getInt(COL_ID_BY_SESSION), rs.getString(COL_NOMBRE_BY_SESSION),
					rs.getBoolean(COL_SUPER_ADMIN));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put(COL_NOMBRE, nombre);
		map.put(COL_SUPER_ADMIN, superAdmin);

		return map;
	}

	@Override
	public String toString() {
		return "Grupo [id=" + id + ", nombre=" + nombre + ", superAdmin=" + superAdmin + ", permisos=" + permisos + "]";
	}
}
