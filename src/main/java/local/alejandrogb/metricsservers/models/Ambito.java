package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Ambito", description = "Representa un ámbito funcional del sistema al que pueden pertenecer distintos permisos")
public class Ambito {

	public static final String TABLE = "ambitos";
	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_DESCRIP = "descripcion";

	public static final String COL_ID_BY_PERM = "a_id";
	public static final String COL_NOMBRE_BY_PERM = "a_nombre";
	public static final String COL_DESCRIP_BY_PERM = "a_descripcion";

	@Schema(description = "Identificador único del ámbito", example = "1")
	private int id;

	@Schema(description = "Nombre del ámbito", example = "Servidor")
	private String nombre;

	@Schema(description = "Descripción del ámbito", example = "Ámbito relacionado con la gestión de servidores")
	private String descripcion;

	public Ambito() {
	}

	public Ambito(int id, String nombre, String descripcion) {
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

	public static Ambito mapAmbito(ResultSet rs) throws SQLException {
		return new Ambito(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getString(COL_DESCRIP));
	}

	public static Ambito mapAmbitoToPermiso(ResultSet rs) throws SQLException {
		return new Ambito(rs.getInt(COL_ID_BY_PERM), rs.getString(COL_NOMBRE_BY_PERM),
				rs.getString(COL_DESCRIP_BY_PERM));
	}

	@Override
	public String toString() {
		return "Ambito [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + "]";
	}
}