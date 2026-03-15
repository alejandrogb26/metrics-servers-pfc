package local.alejandrogb.metricsservers.models.servidor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ServidorDTO", description = "Objeto utilizado para registrar uno o varios servidores en el sistema")
public class ServidorDTO {
	@Schema(description = "Identificador único del servidor dentro del sistema de métricas", example = "server-01", requiredMode = Schema.RequiredMode.REQUIRED)
	public String serverId;

	@Schema(description = "Nombre DNS del servidor", example = "server01.example.com", requiredMode = Schema.RequiredMode.REQUIRED)
	public String dns;

	@Schema(description = "ID de la sección a la que pertenece el servidor", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	public int seccion;

	@Schema(description = "Lista de IDs de servicios asociados al servidor", example = "[1,2,3]")
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
