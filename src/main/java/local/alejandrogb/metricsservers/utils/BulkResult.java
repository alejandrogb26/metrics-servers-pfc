package local.alejandrogb.metricsservers.utils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa el resultado de una operación masiva (bulk) realizada sobre
 * múltiples elementos del sistema.
 *
 * <p>
 * Esta clase permite registrar el número total de elementos procesados, cuántos
 * se completaron correctamente y cuántos fallaron durante la ejecución de la
 * operación. Además, almacena una lista de mensajes de error asociados a los
 * elementos que no pudieron procesarse.
 * </p>
 *
 * <p>
 * Está diseñada para ser utilizada en entornos concurrentes, por lo que emplea
 * estructuras thread-safe como {@link AtomicInteger} y
 * {@link ConcurrentLinkedQueue}. Esto permite que múltiples hilos actualicen el
 * resultado de la operación sin necesidad de sincronización explícita.
 * </p>
 *
 * <p>
 * Habitualmente se utiliza como objeto de respuesta en endpoints REST que
 * realizan operaciones en lote, como por ejemplo:
 * </p>
 *
 * <ul>
 * <li>Creación masiva de recursos</li>
 * <li>Actualización de múltiples registros</li>
 * <li>Procesamiento de listas de servidores o usuarios</li>
 * </ul>
 *
 * <p>
 * Los métodos getter permiten que frameworks de serialización como Jackson o
 * Jersey generen automáticamente la representación JSON utilizada en las
 * respuestas de la API.
 * </p>
 *
 * @author alejandrogb
 */
@Schema(name = "BulkResult", description = "Resultado de una operación masiva (bulk) que puede afectar a múltiples elementos")
public class BulkResult {

	/**
	 * Número total de elementos procesados durante la operación.
	 */
	@Schema(description = "Número total de elementos procesados", example = "10")
	private final AtomicInteger total = new AtomicInteger(0);

	/**
	 * Número de elementos procesados correctamente.
	 */
	@Schema(description = "Número de elementos procesados correctamente", example = "8")
	private final AtomicInteger ok = new AtomicInteger(0);

	/**
	 * Número de elementos cuyo procesamiento falló.
	 */
	@Schema(description = "Número de elementos que fallaron durante el procesamiento", example = "2")
	private final AtomicInteger failed = new AtomicInteger(0);

	/**
	 * Cola concurrente que almacena los mensajes de error generados durante la
	 * ejecución de la operación masiva.
	 */
	@Schema(description = "Lista de errores ocurridos durante la operación", example = "[\"Servidor server-03 no encontrado\", \"Error al crear usuario\"]")
	private final ConcurrentLinkedQueue<String> errors = new ConcurrentLinkedQueue<>();

	/**
	 * Establece el número total de elementos que se van a procesar.
	 *
	 * @param value número total de elementos
	 */
	public void setTotal(int value) {
		total.set(value);
	}

	/**
	 * Incrementa en uno el contador de elementos procesados correctamente.
	 * 
	 * <p>
	 * Este método es seguro para entornos concurrentes.
	 * </p>
	 */
	public void incrementOk() {
		ok.incrementAndGet();
	}

	/**
	 * Incrementa en uno el contador de elementos que fallaron durante el
	 * procesamiento.
	 *
	 * <p>
	 * Este método es seguro para entornos concurrentes.
	 * </p>
	 */
	public void incrementFailed() {
		failed.incrementAndGet();
	}

	/**
	 * Añade un mensaje de error asociado a la operación.
	 *
	 * @param error descripción del error ocurrido
	 */
	public void addError(String error) {
		errors.add(error);
	}

	/**
	 * Devuelve el número total de elementos procesados.
	 *
	 * @return número total de elementos
	 */
	public int getTotal() {
		return total.get();
	}

	/**
	 * Devuelve el número de elementos procesados correctamente.
	 *
	 * @return número de elementos correctos
	 */
	public int getOk() {
		return ok.get();
	}

	/**
	 * Devuelve el número de elementos que fallaron durante el procesamiento.
	 *
	 * @return número de elementos fallidos
	 */
	public int getFailed() {
		return failed.get();
	}

	/**
	 * Devuelve la lista de errores producidos durante la operación.
	 *
	 * <p>
	 * Se devuelve una copia inmutable de la cola interna para evitar modificaciones
	 * externas.
	 * </p>
	 *
	 * @return lista de mensajes de error
	 */
	public List<String> getErrors() {
		return List.copyOf(errors);
	}
}