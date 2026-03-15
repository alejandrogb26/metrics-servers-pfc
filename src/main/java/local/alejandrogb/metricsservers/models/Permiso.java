package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Permiso", description = "Representa un permiso que puede asignarse a grupos o usuarios dentro de un ámbito concreto")
public class Permiso {
	public static final String TABLE = "permisos";
	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_DESCRIP = "descripcion";
	public static final String COL_AMBITO_ID = "ambitoId";

	public static final String COL_ID_BY_SIMPL = "p_id";
	public static final String COL_NOMBRE_BY_SIMPL = "p_nombre";
	public static final String COL_DESCRIP_BY_SIMPL = "p_descripcion";

	@Schema(description = "Identificador único del permiso", example = "10")
	private int id;

	@Schema(description = "Nombre del permiso", example = "SERVIDOR_EDITAR")
	private String nombre;

	@Schema(description = "Descripción del permiso", example = "Permite modificar la configuración de un servidor")
	private String descripcion;

	@Schema(description = "Ámbito al que pertenece el permiso")
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
