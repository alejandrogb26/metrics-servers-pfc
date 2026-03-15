package local.alejandrogb.metricsservers.exceptions;

/**
 * Excepción utilizada para indicar errores de validación en los datos
 * proporcionados por el cliente.
 *
 * <p>
 * Esta excepción se lanza cuando los datos recibidos en una petición
 * no cumplen con las reglas de negocio o con las restricciones de
 * validación definidas por la aplicación.
 * </p>
 *
 * <p>
 * Es utilizada principalmente en la capa de servicios para evitar que
 * datos inválidos lleguen a la capa de persistencia o provoquen
 * comportamientos incorrectos en el sistema.
 * </p>
 *
 * <p>
 * Ejemplos de situaciones en las que puede lanzarse esta excepción:
 * </p>
 *
 * <ul>
 * <li>Campos obligatorios no proporcionados.</li>
 * <li>Valores con formato inválido (por ejemplo DNS incorrecto).</li>
 * <li>Intento de modificar campos no permitidos.</li>
 * </ul>
 *
 * <p>
 * Normalmente esta excepción es capturada por un {@code ExceptionMapper}
 * global que la transforma en una respuesta HTTP adecuada (por ejemplo,
 * {@code 400 Bad Request}) utilizando la estructura {@code ApiError}.
 * </p>
 *
 * <p>
 * Al extender {@link RuntimeException}, no requiere ser declarada en la
 * firma de los métodos, lo que simplifica la propagación de errores en
 * la capa de servicios.
 * </p>
 */
public class ValidationException extends RuntimeException {

	/**
	 * Identificador de versión para la serialización.
	 */
	private static final long serialVersionUID = -4534410942205519555L;

	/**
	 * Crea una nueva excepción de validación.
	 *
	 * @param msg mensaje descriptivo del error de validación
	 */
	public ValidationException(String msg) {
		super(msg);
	}
}