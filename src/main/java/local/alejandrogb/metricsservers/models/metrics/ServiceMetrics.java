package local.alejandrogb.metricsservers.models.metrics;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import local.alejandrogb.metricsservers.models.metrics.services.ApacheMetrics;
import local.alejandrogb.metricsservers.models.metrics.services.MariaDbMetrics;
import local.alejandrogb.metricsservers.models.metrics.services.SshMetrics;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ServiceMetrics", description = "Métricas de los servicios monitorizados en el servidor. Solo se incluyen los servicios que están activos o configurados.")
public class ServiceMetrics {
	@Schema(description = "Métricas del servicio Apache HTTP Server")
	private ApacheMetrics apache2;

	@Schema(description = "Métricas del servicio MariaDB")
	private MariaDbMetrics mariadb;

	@Schema(description = "Métricas del servicio SSH")
	private SshMetrics ssh;

	public ServiceMetrics() {
	}

	public ApacheMetrics getApache2() {
		return apache2;
	}

	public void setApache2(ApacheMetrics apache2) {
		this.apache2 = apache2;
	}

	public MariaDbMetrics getMariadb() {
		return mariadb;
	}

	public void setMariadb(MariaDbMetrics mariadb) {
		this.mariadb = mariadb;
	}

	public SshMetrics getSsh() {
		return ssh;
	}

	public void setSsh(SshMetrics ssh) {
		this.ssh = ssh;
	}
}
