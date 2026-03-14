package local.alejandrogb.metricsservers.models.metrics.services;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class MariaDbMetrics {
	public static class Thread {
		private Integer connected;
		private Integer running;
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

	public static class Connection {
		@BsonProperty("max_used")
		private Integer maxUsed;
		@BsonProperty("aborted_clients")
		private Integer abortedClients;
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

	public static class Query {
		@BsonProperty("queries_total")
		private Long queriesTotal;
		private Long questions;
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

	public static class Traffic {
		@BsonProperty("bytes_received")
		private Long bytesReceived;
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

	public static class Innodb {
		@BsonProperty("buffer_pool_pages_total")
		private Double bufferPoolPagesTotal;
		@BsonProperty("buffer_pool_pages_free")
		private Double bufferPoolPagesFree;
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

	private Boolean enabled;
	@BsonProperty("uptime_s")
	private Long uptime;
	private Thread threads;
	private Connection connections;
	private Query queries;
	private Traffic traffic;
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
