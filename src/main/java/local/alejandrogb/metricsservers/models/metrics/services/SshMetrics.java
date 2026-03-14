package local.alejandrogb.metricsservers.models.metrics.services;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class SshMetrics {
	public static class Listen {
		private Integer port;
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

	private Boolean enabled;
	@BsonProperty("systemd_state")
	private String systemdState;
	private Listen listen;
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
