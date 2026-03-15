package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Servicio", description = "Representa un servicio que puede estar asociado a uno o varios servidores")
public class Servicio {
	public static final String TABLE = "servicios";
	public static final String TABLE_RELATION = "servidores_servicios";

	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_LOGO = "logo";

	@Schema(description = "Identificador único del servicio", example = "3")
	private int id;

	@Schema(description = "Nombre del servicio", example = "Grafana")
	private String nombre;

	@Schema(description = "Nombre del archivo de logo almacenado", example = "grafana.png")
	private String logo;

	@Schema(description = "URL pública del logo del servicio", example = "https://cdn.example.com/logos/grafana.png")
	private String urlLogo;

	public Servicio() {
	}

	public Servicio(int id) {
		this.id = id;
	}

	public Servicio(int id, String nombre, String logo) {
		this.id = id;
		this.nombre = nombre;
		this.logo = logo;
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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getUrlLogo() {
		return urlLogo;
	}

	public void setUrlLogo(String urlLogo) {
		this.urlLogo = urlLogo;
	}

	public static Servicio mapServicio(ResultSet rs) {
		try {
			return new Servicio(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getString(COL_LOGO));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put(COL_NOMBRE, nombre);
		map.put(COL_LOGO, logo);

		return map;
	}

	@Override
	public String toString() {
		return "Servicio [id=" + id + ", nombre=" + nombre + ", logo=" + logo + ", urlLogo=" + urlLogo + "]";
	}

}
