package local.alejandrogb.metricsservers.models.metrics.services;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApacheMetrics", description = "Métricas del servidor Apache HTTP obtenidas del módulo mod_status")
public class ApacheMetrics {

	@Schema(description = "Estado de los workers de Apache")
	public static class Worker {

		@Schema(description = "Número de workers ocupados procesando peticiones", example = "5")
		private Double busy;

		@Schema(description = "Número de workers disponibles", example = "20")
		private Double idle;

		public Worker() {
		}

		public Double getBusy() {
			return busy;
		}

		public void setBusy(Double busy) {
			this.busy = busy;
		}

		public Double getIdle() {
			return idle;
		}

		public void setIdle(Double idle) {
			this.idle = idle;
		}
	}

	@Schema(description = "Estado de las conexiones HTTP gestionadas por Apache")
	public static class Connection {

		@Schema(description = "Número total de conexiones activas", example = "50")
		private Double total;

		@Schema(description = "Conexiones en espera de I/O asíncrono", example = "2")
		@BsonProperty("async_wait_io")
		private Double asyncWaitIO;

		@Schema(description = "Conexiones en proceso de escritura", example = "3")
		@BsonProperty("async_writing")
		private Double asyncWriting;

		@Schema(description = "Conexiones en keep-alive", example = "10")
		@BsonProperty("async_keepalive")
		private Double asyncKeepalive;

		@Schema(description = "Conexiones cerrándose", example = "1")
		@BsonProperty("async_closing")
		private Double asyncClosing;

		public Connection() {
		}

		public Double getTotal() {
			return total;
		}

		public void setTotal(Double total) {
			this.total = total;
		}

		public Double getAsyncWaitIO() {
			return asyncWaitIO;
		}

		public void setAsyncWaitIO(Double asyncWaitIO) {
			this.asyncWaitIO = asyncWaitIO;
		}

		public Double getAsyncWriting() {
			return asyncWriting;
		}

		public void setAsyncWriting(Double asyncWriting) {
			this.asyncWriting = asyncWriting;
		}

		public Double getAsyncKeepalive() {
			return asyncKeepalive;
		}

		public void setAsyncKeepalive(Double asyncKeepalive) {
			this.asyncKeepalive = asyncKeepalive;
		}

		public Double getAsyncClosing() {
			return asyncClosing;
		}

		public void setAsyncClosing(Double asyncClosing) {
			this.asyncClosing = asyncClosing;
		}
	}

	@Schema(description = "Información del scoreboard interno de Apache")
	public static class Scoreboard {

		@Schema(description = "Conteo de estados del scoreboard de Apache")
		public static class Count {

			@Schema(description = "Workers libres esperando conexión", example = "15")
			@BsonProperty("_")
			private Double underscore;

			@Schema(description = "Workers procesando peticiones", example = "5")
			@BsonProperty("W")
			private Double w;

			@Schema(description = "Workers en estado keep-alive", example = "3")
			@BsonProperty(".")
			private Double dot;

			public Count() {
			}

			public Double getUnderscore() {
				return underscore;
			}

			public void setUnderscore(Double underscore) {
				this.underscore = underscore;
			}

			public Double getW() {
				return w;
			}

			public void setW(Double w) {
				this.w = w;
			}

			public Double getDot() {
				return dot;
			}

			public void setDot(Double dot) {
				this.dot = dot;
			}
		}

		@Schema(description = "Distribución de estados de los workers")
		private Count counts;

		public Scoreboard() {
		}

		public Count getCounts() {
			return counts;
		}

		public void setCounts(Count counts) {
			this.counts = counts;
		}
	}

	@Schema(description = "Indica si el servicio Apache está activo", example = "true")
	private Boolean enabled;

	@Schema(description = "Tiempo de actividad del servidor Apache en segundos", example = "86400")
	@BsonProperty("uptime_s")
	private Long uptime;

	@Schema(description = "Número de peticiones HTTP por segundo", example = "12.5")
	@BsonProperty("req_per_sec")
	private Double reqPerSec;

	@Schema(description = "Cantidad de bytes enviados por segundo", example = "2048.5")
	@BsonProperty("bytes_per_sec")
	private Double bytesPerSec;

	@Schema(description = "Tamaño medio de respuesta por petición", example = "1024.3")
	@BsonProperty("bytes_per_req")
	private Double bytesPerReq;

	@Schema(description = "Información sobre workers del servidor Apache")
	private Worker workers;

	@Schema(description = "Información sobre conexiones HTTP activas")
	private Connection connections;

	@Schema(description = "Estado interno del scoreboard de Apache")
	public Scoreboard scoreboard;

	public ApacheMetrics() {
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Long getUptime() {
		return uptime;
	}

	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}

	public Double getReqPerSec() {
		return reqPerSec;
	}

	public void setReqPerSec(Double reqPerSec) {
		this.reqPerSec = reqPerSec;
	}

	public Double getBytesPerSec() {
		return bytesPerSec;
	}

	public void setBytesPerSec(Double bytesPerSec) {
		this.bytesPerSec = bytesPerSec;
	}

	public Double getBytesPerReq() {
		return bytesPerReq;
	}

	public void setBytesPerReq(Double bytesPerReq) {
		this.bytesPerReq = bytesPerReq;
	}

	public Worker getWorkers() {
		return workers;
	}

	public void setWorkers(Worker workers) {
		this.workers = workers;
	}

	public Connection getConnections() {
		return connections;
	}

	public void setConnections(Connection connections) {
		this.connections = connections;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}
}