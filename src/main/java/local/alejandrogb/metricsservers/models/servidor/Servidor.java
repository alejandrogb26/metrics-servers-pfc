package local.alejandrogb.metricsservers.models.servidor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	private int id;
	private String serverId, dns, hostname, prettyOs, arch, kernel, imagen, imagenUrl;
	private int seccion;
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
