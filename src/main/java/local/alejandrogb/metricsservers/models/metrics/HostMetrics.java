package local.alejandrogb.metricsservers.models.metrics;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class HostMetrics {
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
