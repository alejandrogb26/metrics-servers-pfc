package local.alejandrogb.metricsservers.models.metrics;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SystemMetrics", description = "Métricas del sistema operativo del servidor: CPU, memoria, swap, discos y red")
public class SystemMetrics {

	@Schema(description = "Información sobre el uso de CPU del sistema")
	public static class Cpu {

		@Schema(description = "Porcentaje de uso de CPU", example = "34.5")
		private Double percent;

		@Schema(description = "Número de núcleos de CPU disponibles", example = "8")
		private Integer cores;

		@Schema(description = "Load average del sistema (1, 5 y 15 minutos)", example = "[0.12, 0.34, 0.28]")
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

	@Schema(description = "Información sobre el uso de memoria RAM")
	public static class Memoria {

		@Schema(description = "Cantidad de memoria usada en bytes", example = "4294967296")
		@BsonProperty("used_bytes")
		private Long usedBytes;

		@Schema(description = "Cantidad total de memoria disponible en bytes", example = "8589934592")
		@BsonProperty("total_bytes")
		private Long totalBytes;

		@Schema(description = "Porcentaje de memoria usada", example = "50.0")
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

	@Schema(description = "Información sobre el uso de memoria swap")
	public static class Swap {

		@Schema(description = "Indica si el sistema tiene swap configurado", example = "true")
		private Boolean present;

		@Schema(description = "Cantidad de swap usado en bytes", example = "104857600")
		@BsonProperty("used_bytes")
		private Long usedBytes;

		@Schema(description = "Cantidad total de swap disponible en bytes", example = "2147483648")
		@BsonProperty("total_bytes")
		private Long totalBytes;

		@Schema(description = "Porcentaje de swap usado", example = "4.8")
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

	@Schema(description = "Información de uso de un disco montado en el sistema")
	public static class Disk {

		@Schema(description = "Punto de montaje del sistema de archivos", example = "/")
		private String mount;

		@Schema(description = "Tipo de sistema de archivos", example = "ext4")
		@BsonProperty("fstype")
		private String fsType;

		@Schema(description = "Dispositivo de almacenamiento", example = "/dev/sda1")
		private String device;

		@Schema(description = "Espacio usado en bytes", example = "10737418240")
		@BsonProperty("used_bytes")
		private Long usedBytes;

		@Schema(description = "Espacio total del disco en bytes", example = "21474836480")
		@BsonProperty("total_bytes")
		private Long totalBytes;

		@Schema(description = "Porcentaje de uso del disco", example = "50.0")
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

	@Schema(description = "Estadísticas de tráfico de red del sistema")
	public static class Network {

		@Schema(description = "Cantidad total de bytes recibidos", example = "104857600")
		@BsonProperty("rx_bytes_total")
		private Long netRx;

		@Schema(description = "Cantidad total de bytes enviados", example = "52428800")
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

	@Schema(description = "Información de CPU")
	public Cpu cpu;

	@Schema(description = "Información de memoria RAM")
	public Memoria mem;

	@Schema(description = "Información de memoria swap")
	public Swap swap;

	@Schema(description = "Lista de discos del sistema")
	public List<Disk> disks;

	@Schema(description = "Información de red del sistema")
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