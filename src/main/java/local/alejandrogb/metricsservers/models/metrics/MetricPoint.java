package local.alejandrogb.metricsservers.models.metrics;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MetricPoint", description = "Punto temporal de métricas de un servidor. Contiene métricas del host, del sistema y de los servicios en un instante concreto.")
public class MetricPoint {
	@Schema(description = "Timestamp del punto de métricas en formato UTC", example = "2024-06-01T12:30:15.123Z")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date ts;

	@Schema(description = "Métricas del host físico o máquina virtual")
	private HostMetrics host;

	@Schema(description = "Métricas del sistema operativo")
	private SystemMetrics metrics;

	@Schema(description = "Métricas de los servicios monitorizados en el servidor")
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
