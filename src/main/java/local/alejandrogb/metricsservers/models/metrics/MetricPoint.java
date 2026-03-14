package local.alejandrogb.metricsservers.models.metrics;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MetricPoint {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date ts;
	private HostMetrics host;
	private SystemMetrics metrics;
	private ServiceMetrics services;

	public MetricPoint() {
	}

	public MetricPoint(Date ts, HostMetrics host, SystemMetrics metrics, ServiceMetrics services) {
		this.ts = ts;
		this.host = host;
		this.metrics = metrics;
		this.services = services;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}

	public HostMetrics getHost() {
		return host;
	}

	public void setHost(HostMetrics host) {
		this.host = host;
	}

	public SystemMetrics getMetrics() {
		return metrics;
	}

	public void setMetrics(SystemMetrics metrics) {
		this.metrics = metrics;
	}

	public ServiceMetrics getServices() {
		return services;
	}

	public void setServices(ServiceMetrics services) {
		this.services = services;
	}
}
