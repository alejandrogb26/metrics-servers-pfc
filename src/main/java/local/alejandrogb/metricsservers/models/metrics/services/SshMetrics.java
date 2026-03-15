package local.alejandrogb.metricsservers.models.metrics.services;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SshMetrics", description = "Métricas del servicio SSH del servidor")
public class SshMetrics {

	@Schema(description = "Información sobre el puerto de escucha del servicio SSH")
	public static class Listen {

		@Schema(description = "Puerto en el que escucha el servicio SSH", example = "22")
		private Integer port;

		@Schema(description = "Indica si el puerto SSH está abierto", example = "true")
		@BsonProperty("port_open")
		private Boolean portOpen;

		public Listen() {
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public Boolean getPortOpen() {
			return portOpen;
		}

		public void setPortOpen(Boolean portOpen) {
			this.portOpen = portOpen;
		}
	}

	@Schema(description = "Indica si el servicio SSH está activo", example = "true")
	private Boolean enabled;

	@Schema(description = "Estado del servicio SSH según systemd", example = "active")
	@BsonProperty("systemd_state")
	private String systemdState;

	@Schema(description = "Información sobre el puerto de escucha del servicio SSH")
	private Listen listen;

	@Schema(description = "Número estimado de sesiones SSH activas", example = "2")
	@BsonProperty("sessions_estimated")
	private Integer sessionsEstimated;

	public SshMetrics() {
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getSystemdState() {
		return systemdState;
	}

	public void setSystemdState(String systemdState) {
		this.systemdState = systemdState;
	}

	public Listen getListen() {
		return listen;
	}

	public void setListen(Listen listen) {
		this.listen = listen;
	}

	public Integer getSessionsEstimated() {
		return sessionsEstimated;
	}

	public void setSessionsEstimated(Integer sessionsEstimated) {
		this.sessionsEstimated = sessionsEstimated;
	}
}