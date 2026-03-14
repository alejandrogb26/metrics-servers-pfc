package local.alejandrogb.metricsservers.models.servidor;

import java.util.List;

public class ServidorDTO {
	public String serverId;
	public String dns;
	public int seccion;
	public List<Integer> servicios;

	public ServidorDTO() {
	}

	public ServidorDTO(String serverId, String dns, int seccion, List<Integer> servicios) {
		super();
		this.serverId = serverId;
		this.dns = dns;
		this.seccion = seccion;
		this.servicios = servicios;
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

	public int getSeccion() {
		return seccion;
	}

	public void setSeccion(int seccion) {
		this.seccion = seccion;
	}

	public List<Integer> getServicios() {
		return servicios;
	}

	public void setServicios(List<Integer> servicios) {
		this.servicios = servicios;
	}

	public static Servidor mapDtoToServidor(ServidorDTO dto) {
		Servidor s = new Servidor(dto.serverId, dto.dns, dto.seccion, dto.servicios);
		return s;
	}

	@Override
	public String toString() {
		return "ServidorDTO [serverId=" + serverId + ", dns=" + dns + ", seccion=" + seccion + ", servicios="
				+ servicios + "]";
	}

}
