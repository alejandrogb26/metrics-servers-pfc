package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Seccion {
	public static final String TABLE = "secciones";

	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_DESCRIP = "descripcion";

	private int id;
	private String nombre;
	private String descripcion;

	public Seccion() {
	}

	public Seccion(int id) {
		this.id = id;
	}

	public Seccion(int id, String nombre, String descripcion) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
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

	public static Seccion mapSeccion(ResultSet rs) {
		try {
			return new Seccion(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getString(COL_DESCRIP));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put(COL_NOMBRE, nombre);
		map.put(COL_DESCRIP, descripcion);

		return map;
	}

	@Override
	public String toString() {
		return "Seccion [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + "]";
	}

}
