package local.alejandrogb.metricsservers.models.metrics;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HostMetrics", description = "Métricas básicas del host (máquina física o virtual)")
public class HostMetrics {
	@Schema(name = "uptime_s", description = "Tiempo que el host lleva activo en segundos", example = "86400")
	@BsonProperty("uptime_s")
	private Long uptimeSeconds;

	public HostMetrics() {
	}

	public Long getUptimeSeconds() {
		return uptimeSeconds;
	}

	public void setUptimeSeconds(Long uptimeSeconds) {
		this.uptimeSeconds = uptimeSeconds;
	}
}
