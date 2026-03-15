package local.alejandrogb.metricsservers.models.servidor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Servidor", description = "Representa un servidor monitorizado por el sistema")
public class Servidor {
	public static final String TABLE = "servidores";

	public static final String COL_ID = "id";
	public static final String COL_SERV_ID = "serverId";
	public static final String COL_DNS = "dns";
	public static final String COL_HOSTNAME = "hostname";
	public static final String COL_PRETTY_OS = "prettyOs";
	public static final String COL_ARCH = "arch";
	public static final String COL_KERNEL = "kernel";
	public static final String COL_SECCION_ID = "seccionId";
	public static final String COL_IMAGEN = "imagen";

	@Schema(description = "Identificador interno del servidor", example = "1")
	private int id;

	@Schema(description = "Identificador único del servidor utilizado por el sistema de métricas", example = "server-01")
	private String serverId;

	@Schema(description = "Nombre DNS del servidor", example = "server01.example.com")
	private String dns;

	@Schema(description = "Hostname del sistema operativo", example = "server01")
	private String hostname;

	@Schema(description = "Nombre legible del sistema operativo", example = "Debian GNU/Linux 12 (bookworm)")
	private String prettyOs;

	@Schema(description = "Arquitectura del sistema", example = "x86_64")
	private String arch;

	@Schema(description = "Versión del kernel del sistema operativo", example = "6.1.0-18-amd64")
	private String kernel;

	@Schema(description = "Nombre del archivo de imagen asociado al servidor", example = "server01.png")
	private String imagen;

	@Schema(description = "URL pública de la imagen del servidor", example = "https://cdn.example.com/images/server01.png", accessMode = Schema.AccessMode.READ_ONLY)
	private String imagenUrl;

	@Schema(description = "ID de la sección a la que pertenece el servidor", example = "2")
	private int seccion;

	@Schema(description = "Lista de IDs de servicios asociados al servidor", example = "[1,2,3]")
	private List<Integer> servicios;

	public Servidor() {
	}

	public Servidor(String serverId, String dns, int seccion, List<Integer> servicios) {
		this.serverId = serverId;
		this.dns = dns;
		this.seccion = seccion;
		this.servicios = servicios;
	}

	public Servidor(int id, String serverId, String dns, String hostname, String prettyOs, String arch, String kernel,
			int seccion, String imagen) {
		this.id = id;
		this.serverId = serverId;
		this.dns = dns;
		this.hostname = hostname;
		this.prettyOs = prettyOs;
		this.arch = arch;
		this.kernel = kernel;
		this.seccion = seccion;
		this.imagen = imagen;
	}

	public Servidor(int id, String serverId, String dns, String hostname, String prettyOs, String arch, String kernel,
			int seccion, String imagen, List<Integer> servicios) {
		super();
		this.id = id;
		this.serverId = serverId;
		this.dns = dns;
		this.hostname = hostname;
		this.prettyOs = prettyOs;
		this.arch = arch;
		this.kernel = kernel;
		this.seccion = seccion;
		this.servicios = servicios;
		this.imagen = imagen;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPrettyOs() {
		return prettyOs;
	}

	public void setPrettyOs(String prettyOs) {
		this.prettyOs = prettyOs;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getKernel() {
		return kernel;
	}

	public void setKernel(String kernel) {
		this.kernel = kernel;
	}

	public int getSeccion() {
		return seccion;
	}

	public void setSeccion(int seccion) {
		this.seccion = seccion;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	public List<Integer> getServicios() {
		return servicios;
	}

	public void setServicios(List<Integer> servicios) {
		this.servicios = servicios;
	}

	public static Servidor mapServidor(ResultSet rs) {
		try {
			return new Servidor(rs.getInt(COL_ID), rs.getString(COL_SERV_ID), rs.getString(COL_DNS),
					rs.getString(COL_HOSTNAME), rs.getString(COL_PRETTY_OS), rs.getString(COL_ARCH),
					rs.getString(COL_KERNEL), rs.getInt(COL_SECCION_ID), rs.getString(COL_IMAGEN));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<Integer, Servidor> extractServidores(ResultSet rs) throws SQLException {

		Map<Integer, Servidor> map = new LinkedHashMap<>();

		while (rs.next()) {

			int id = rs.getInt("id");

			Servidor servidor = map.get(id);

			if (servidor == null) {
				servidor = Servidor.mapServidor(rs);
				servidor.setServicios(new ArrayList<>());
				map.put(id, servidor);
			}

			Integer servicioId = rs.getObject("servicio_id", Integer.class);

			if (servicioId != null) {
				servidor.getServicios().add(servicioId);
			}
		}

		return map;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put(COL_SERV_ID, serverId);
		map.put(COL_DNS, dns);
		map.put(COL_HOSTNAME, hostname);
		map.put(COL_PRETTY_OS, prettyOs);
		map.put(COL_ARCH, arch);
		map.put(COL_KERNEL, kernel);
		map.put(COL_SECCION_ID, seccion);
		map.put(COL_IMAGEN, imagen);

		return map;
	}

	@Override
	public String toString() {
		return "Servidor [id=" + id + ", serverId=" + serverId + ", dns=" + dns + ", hostname=" + hostname
				+ ", prettyOs=" + prettyOs + ", arch=" + arch + ", kernel=" + kernel + ", imagen=" + imagen
				+ ", fotoUrl=" + imagenUrl + ", seccion=" + seccion + ", servicios=" + servicios + "]";
	}
}
