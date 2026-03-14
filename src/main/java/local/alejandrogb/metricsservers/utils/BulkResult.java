package local.alejandrogb.metricsservers.utils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BulkResult {
	private final AtomicInteger total = new AtomicInteger(0);
	private final AtomicInteger ok = new AtomicInteger(0);
	private final AtomicInteger failed = new AtomicInteger(0);

	// Lista segura para hilos (no bloqueante)
	private final ConcurrentLinkedQueue<String> errors = new ConcurrentLinkedQueue<>();

	// Métodos para actualizar (Thread-safe)
	public void setTotal(int value) {
		total.set(value);
	}

	public void incrementOk() {
		ok.incrementAndGet();
	}

	public void incrementFailed() {
		failed.incrementAndGet();
	}

	public void addError(String error) {
		errors.add(error);
	}

	// Getters para la serialización JSON (Jackson/Jersey los usará)
	public int getTotal() {
		return total.get();
	}

	public int getOk() {
		return ok.get();
	}

	public int getFailed() {
		return failed.get();
	}

	public List<String> getErrors() {
		return List.copyOf(errors);
	}
}