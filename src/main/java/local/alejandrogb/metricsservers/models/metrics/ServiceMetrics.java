package local.alejandrogb.metricsservers.models.metrics;

import com.fasterxml.jackson.annotation.JsonInclude;

import local.alejandrogb.metricsservers.models.metrics.services.ApacheMetrics;
import local.alejandrogb.metricsservers.models.metrics.services.MariaDbMetrics;
import local.alejandrogb.metricsservers.models.metrics.services.SshMetrics;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceMetrics {
	private ApacheMetrics apache2;
	private MariaDbMetrics mariadb;
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
