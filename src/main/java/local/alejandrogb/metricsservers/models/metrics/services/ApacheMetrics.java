package local.alejandrogb.metricsservers.models.metrics.services;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class ApacheMetrics {
	public static class Worker {
		private Double busy;
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

	public static class Connection {
		private Double total;
		@BsonProperty("async_wait_io")
		private Double asyncWaitIO;
		@BsonProperty("async_writing")
		private Double asyncWriting;
		@BsonProperty("async_keepalive")
		private Double asyncKeepalive;
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

	public static class Scoreboard {
		public static class Count {
			@BsonProperty("_")
			private Double underscore;
			@BsonProperty("W")
			private Double w;
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

	private Boolean enabled;
	@BsonProperty("uptime_s")
	private Long uptime;
	@BsonProperty("req_per_sec")
	private Double reqPerSec;
	@BsonProperty("bytes_per_sec")
	private Double bytesPerSec;
	@BsonProperty("bytes_per_req")
	private Double bytesPerReq;
	private Worker workers;
	private Connection connections;
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
