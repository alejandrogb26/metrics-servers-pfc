package local.alejandrogb.metricsservers.models.metrics.services;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MariaDbMetrics", description = "Métricas del servidor de base de datos MariaDB")
public class MariaDbMetrics {

	@Schema(description = "Información sobre los hilos del servidor MariaDB")
	public static class Thread {

		@Schema(description = "Número de conexiones activas al servidor", example = "10")
		private Integer connected;

		@Schema(description = "Número de consultas actualmente en ejecución", example = "2")
		private Integer running;

		@Schema(description = "Número de hilos cacheados por el servidor", example = "5")
		private Integer cached;

		public Thread() {
		}

		public Integer getConnected() {
			return connected;
		}

		public void setConnected(Integer connected) {
			this.connected = connected;
		}

		public Integer getRunning() {
			return running;
		}

		public void setRunning(Integer running) {
			this.running = running;
		}

		public Integer getCached() {
			return cached;
		}

		public void setCached(Integer cached) {
			this.cached = cached;
		}
	}

	@Schema(description = "Estadísticas de conexiones del servidor MariaDB")
	public static class Connection {

		@Schema(description = "Número máximo de conexiones usadas desde el arranque", example = "50")
		@BsonProperty("max_used")
		private Integer maxUsed;

		@Schema(description = "Número de conexiones abortadas por el cliente", example = "3")
		@BsonProperty("aborted_clients")
		private Integer abortedClients;

		@Schema(description = "Número de intentos de conexión fallidos", example = "1")
		@BsonProperty("aborted_connects")
		private Integer abortedConnects;

		public Connection() {
		}

		public Integer getMaxUsed() {
			return maxUsed;
		}

		public void setMaxUsed(Integer maxUsed) {
			this.maxUsed = maxUsed;
		}

		public Integer getAbortedClients() {
			return abortedClients;
		}

		public void setAbortedClients(Integer abortedClients) {
			this.abortedClients = abortedClients;
		}

		public Integer getAbortedConnects() {
			return abortedConnects;
		}

		public void setAbortedConnects(Integer abortedConnects) {
			this.abortedConnects = abortedConnects;
		}
	}

	@Schema(description = "Información sobre consultas ejecutadas en MariaDB")
	public static class Query {

		@Schema(description = "Número total de consultas ejecutadas", example = "120000")
		@BsonProperty("queries_total")
		private Long queriesTotal;

		@Schema(description = "Número de consultas enviadas por los clientes", example = "115000")
		private Long questions;

		@Schema(description = "Número de consultas lentas detectadas", example = "3")
		@BsonProperty("slow_queries")
		private Double slowQueries;

		public Query() {
		}

		public Long getQueriesTotal() {
			return queriesTotal;
		}

		public void setQueriesTotal(Long queriesTotal) {
			this.queriesTotal = queriesTotal;
		}

		public Long getQuestions() {
			return questions;
		}

		public void setQuestions(Long questions) {
			this.questions = questions;
		}

		public Double getSlowQueries() {
			return slowQueries;
		}

		public void setSlowQueries(Double slowQueries) {
			this.slowQueries = slowQueries;
		}
	}

	@Schema(description = "Estadísticas de tráfico de red de MariaDB")
	public static class Traffic {

		@Schema(description = "Cantidad total de bytes recibidos por el servidor", example = "10485760")
		@BsonProperty("bytes_received")
		private Long bytesReceived;

		@Schema(description = "Cantidad total de bytes enviados por el servidor", example = "20971520")
		@BsonProperty("bytes_sent")
		private Long bytesSent;

		public Traffic() {
		}

		public Long getBytesReceived() {
			return bytesReceived;
		}

		public void setBytesReceived(Long bytesReceived) {
			this.bytesReceived = bytesReceived;
		}

		public Long getBytesSent() {
			return bytesSent;
		}

		public void setBytesSent(Long bytesSent) {
			this.bytesSent = bytesSent;
		}
	}

	@Schema(description = "Información del buffer pool de InnoDB")
	public static class Innodb {

		@Schema(description = "Número total de páginas del buffer pool", example = "8192")
		@BsonProperty("buffer_pool_pages_total")
		private Double bufferPoolPagesTotal;

		@Schema(description = "Número de páginas libres en el buffer pool", example = "2048")
		@BsonProperty("buffer_pool_pages_free")
		private Double bufferPoolPagesFree;

		@Schema(description = "Número de páginas sucias en el buffer pool", example = "150")
		@BsonProperty("buffer_pool_pages_dirty")
		private Double bufferPoolPagesDirty;

		public Innodb() {
		}

		public Double getBufferPoolPagesTotal() {
			return bufferPoolPagesTotal;
		}

		public void setBufferPoolPagesTotal(Double bufferPoolPagesTotal) {
			this.bufferPoolPagesTotal = bufferPoolPagesTotal;
		}

		public Double getBufferPoolPagesFree() {
			return bufferPoolPagesFree;
		}

		public void setBufferPoolPagesFree(Double bufferPoolPagesFree) {
			this.bufferPoolPagesFree = bufferPoolPagesFree;
		}

		public Double getBufferPoolPagesDirty() {
			return bufferPoolPagesDirty;
		}

		public void setBufferPoolPagesDirty(Double bufferPoolPagesDirty) {
			this.bufferPoolPagesDirty = bufferPoolPagesDirty;
		}
	}

	@Schema(description = "Indica si el servicio MariaDB está activo", example = "true")
	private Boolean enabled;

	@Schema(description = "Tiempo de actividad del servidor MariaDB en segundos", example = "86400")
	@BsonProperty("uptime_s")
	private Long uptime;

	@Schema(description = "Información sobre los hilos del servidor")
	private Thread threads;

	@Schema(description = "Estadísticas de conexiones")
	private Connection connections;

	@Schema(description = "Estadísticas de consultas")
	private Query queries;

	@Schema(description = "Información sobre el tráfico de red")
	private Traffic traffic;

	@Schema(description = "Información sobre el estado del motor InnoDB")
	private Innodb innodb;

	public MariaDbMetrics() {
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

	public Thread getThreads() {
		return threads;
	}

	public void setThreads(Thread threads) {
		this.threads = threads;
	}

	public Connection getConnections() {
		return connections;
	}

	public void setConnections(Connection connections) {
		this.connections = connections;
	}

	public Query getQueries() {
		return queries;
	}

	public void setQueries(Query queries) {
		this.queries = queries;
	}

	public Traffic getTraffic() {
		return traffic;
	}

	public void setTraffic(Traffic traffic) {
		this.traffic = traffic;
	}

	public Innodb getInnodb() {
		return innodb;
	}

	public void setInnodb(Innodb innodb) {
		this.innodb = innodb;
	}
}