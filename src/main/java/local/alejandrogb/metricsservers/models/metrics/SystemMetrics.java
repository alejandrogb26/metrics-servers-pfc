package local.alejandrogb.metricsservers.models.metrics;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class SystemMetrics {
	public static class Cpu {
		private Double percent;
		private Integer cores;
		@BsonProperty("loadavg")
		private List<Double> loadAvg;

		public Cpu() {
		}

		public Double getPercent() {
			return percent;
		}

		public void setPercent(Double percent) {
			this.percent = percent;
		}

		public Integer getCores() {
			return cores;
		}

		public void setCores(Integer cores) {
			this.cores = cores;
		}

		public List<Double> getLoadAvg() {
			return loadAvg;
		}

		public void setLoadAvg(List<Double> loadAvg) {
			this.loadAvg = loadAvg;
		}
	}

	public static class Memoria {
		@BsonProperty("used_bytes")
		private Long usedBytes;
		@BsonProperty("total_bytes")
		private Long totalBytes;
		private Double percent;

		public Memoria() {
		}

		public Long getUsedBytes() {
			return usedBytes;
		}

		public void setUsedBytes(Long usedBytes) {
			this.usedBytes = usedBytes;
		}

		public Long getTotalBytes() {
			return totalBytes;
		}

		public void setTotalBytes(Long totalBytes) {
			this.totalBytes = totalBytes;
		}

		public Double getPercent() {
			return percent;
		}

		public void setPercent(Double percent) {
			this.percent = percent;
		}
	}

	public static class Swap {
		private Boolean present;
		@BsonProperty("used_bytes")
		private Long usedBytes;
		@BsonProperty("total_bytes")
		private Long totalBytes;
		private Double percent;

		public Swap() {
		}

		public Boolean getPresent() {
			return present;
		}

		public void setPresent(Boolean present) {
			this.present = present;
		}

		public Long getUsedBytes() {
			return usedBytes;
		}

		public void setUsedBytes(Long usedBytes) {
			this.usedBytes = usedBytes;
		}

		public Long getTotalBytes() {
			return totalBytes;
		}

		public void setTotalBytes(Long totalBytes) {
			this.totalBytes = totalBytes;
		}

		public Double getPercent() {
			return percent;
		}

		public void setPercent(Double percent) {
			this.percent = percent;
		}
	}

	public static class Disk {
		private String mount;
		@BsonProperty("fstype")
		private String fsType;
		private String device;
		@BsonProperty("used_bytes")
		private Long usedBytes;
		@BsonProperty("total_bytes")
		private Long totalBytes;
		private Double percent;

		public Disk() {
		}

		public String getMount() {
			return mount;
		}

		public void setMount(String mount) {
			this.mount = mount;
		}

		public String getFsType() {
			return fsType;
		}

		public void setFsType(String fsType) {
			this.fsType = fsType;
		}

		public String getDevice() {
			return device;
		}

		public void setDevice(String device) {
			this.device = device;
		}

		public Long getUsedBytes() {
			return usedBytes;
		}

		public void setUsedBytes(Long usedBytes) {
			this.usedBytes = usedBytes;
		}

		public Long getTotalBytes() {
			return totalBytes;
		}

		public void setTotalBytes(Long totalBytes) {
			this.totalBytes = totalBytes;
		}

		public Double getPercent() {
			return percent;
		}

		public void setPercent(Double percent) {
			this.percent = percent;
		}
	}

	public static class Network {
		@BsonProperty("rx_bytes_total")
		private Long netRx;
		@BsonProperty("tx_bytes_total")
		private Long netTx;

		public Network() {
		}

		public Long getNetRx() {
			return netRx;
		}

		public void setNetRx(Long netRx) {
			this.netRx = netRx;
		}

		public Long getNetTx() {
			return netTx;
		}

		public void setNetTx(Long netTx) {
			this.netTx = netTx;
		}
	}

	public Cpu cpu;
	public Memoria mem;
	public Swap swap;
	public List<Disk> disks;
	public Network net;

	public SystemMetrics() {
	}

	public Cpu getCpu() {
		return cpu;
	}

	public void setCpu(Cpu cpu) {
		this.cpu = cpu;
	}

	public Memoria getMem() {
		return mem;
	}

	public void setMem(Memoria mem) {
		this.mem = mem;
	}

	public Swap getSwap() {
		return swap;
	}

	public void setSwap(Swap swap) {
		this.swap = swap;
	}

	public List<Disk> getDisks() {
		return disks;
	}

	public void setDisks(List<Disk> disks) {
		this.disks = disks;
	}

	public Network getNet() {
		return net;
	}

	public void setNet(Network net) {
		this.net = net;
	}
}
