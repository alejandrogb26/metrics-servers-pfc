package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Grupo", description = "Representa un grupo de usuarios del sistema con sus permisos asociados")
public class Grupo {
	public static final String TABLE = "grupos";
	public static final String TABLE_GLOBAL_PRM = "grupo_permiso_global";
	public static final String TABLE_SECTION_PRM = "grupo_seccion";

	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_DN = "dn";
	public static final String COL_SUPERADMIN = "superadmin";

	@Schema(description = "Identificador único del grupo", example = "1")
	private int id;

	@Schema(description = "Nombre del grupo", example = "Administradores")
	private String nombre;

	private String dn;

	@Schema(description = "Indica si el grupo tiene privilegios de superadministrador", example = "true")
	private Boolean superAdmin;

	@Schema(description = "Mapa de permisos asignados al grupo")
	private PermissionMap<Integer> permisos;

	public Grupo() {
	}

	public Grupo(int id, String nombre, String dn, Boolean superAdmin) {
		this.id = id;
		this.nombre = nombre;
		this.dn = dn;
		this.superAdmin = superAdmin;
	}

	public Grupo(int id, String nombre, String dn, Boolean superAdmin, PermissionMap<Integer> permisos) {
		this.id = id;
		this.nombre = nombre;
		this.dn = dn;
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

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
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
			return new Grupo(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getString(COL_DN),
					rs.getBoolean(COL_SUPERADMIN));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Grupo> mapGrupos(ResultSet rs) throws SQLException {
		List<Grupo> grupos = new ArrayList<>();
		while (rs.next()) {
			grupos.add(new Grupo(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getString(COL_DN),
					rs.getBoolean(COL_SUPERADMIN)));
		}
		return grupos;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put(COL_NOMBRE, nombre);
		map.put(COL_SUPERADMIN, superAdmin);

		return map;
	}

	@Override
	public String toString() {
		return "Grupo [id=" + id + ", nombre=" + nombre + ", superAdmin=" + superAdmin + ", permisos=" + permisos + "]";
	}
}
