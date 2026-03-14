package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Permiso {
	public static final String TABLE = "permisos";
	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_DESCRIP = "descripcion";
	public static final String COL_AMBITO_ID = "ambitoId";

	public static final String COL_ID_BY_SIMPL = "p_id";
	public static final String COL_NOMBRE_BY_SIMPL = "p_nombre";
	public static final String COL_DESCRIP_BY_SIMPL = "p_descripcion";

	private int id;
	private String nombre, descripcion;
	private Ambito ambito;

	public Permiso() {
	}

	public Permiso(int id, String nombre, String descripcion, Ambito ambito) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.ambito = ambito;
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Ambito getAmbito() {
		return ambito;
	}

	public void setAmbito(Ambito ambito) {
		this.ambito = ambito;
	}

	public static Permiso mapPermiso(ResultSet rs) throws SQLException {
		return new Permiso(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getString(COL_DESCRIP),
				Ambito.mapAmbito(rs));
	}

	public static Permiso mapPermisoToSimpl(ResultSet rs) throws SQLException {
		return new Permiso(rs.getInt(COL_ID_BY_SIMPL), rs.getString(COL_NOMBRE_BY_SIMPL),
				rs.getString(COL_DESCRIP_BY_SIMPL), Ambito.mapAmbitoToPermiso(rs));
	}

//	public Map<String, Object> toMap() {
//		Map<String, Object> map = new LinkedHashMap<String, Object>();
//
//		map.put(COL_NOMBRE, nombre);
//		map.put(COL_DESCRIP, descripcion);
//		map.put(COL_AMBITO_ID, ambito.getId());
//		return map;
//	}

	@Override
	public String toString() {
		return "Permiso [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", ambito=" + ambito + "]";
	}
}
