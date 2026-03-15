package local.alejandrogb.metricsservers.exceptions.api;

/**
 * Representa la estructura estándar de respuesta de error utilizada por la API.
 *
 * <p>
 * Esta clase se utiliza para devolver información estructurada cuando ocurre
 * una excepción durante el procesamiento de una petición. Permite proporcionar
 * detalles claros al cliente sobre el tipo de error ocurrido.
 * </p>
 *
 * <p>
 * Normalmente este objeto es generado por los {@code ExceptionMapper} globales
 * de la aplicación cuando se captura una excepción y se transforma en una
 * respuesta HTTP con formato JSON.
 * </p>
 *
 * <p>
 * La estructura típica de respuesta es la siguiente:
 * </p>
 *
 * <pre>
 * {
 *   "code": "VALIDATION_ERROR",
 *   "message": "Datos inválidos en la solicitud",
 *   "details": "El campo 'dns' contiene un valor incorrecto"
 * }
 * </pre>
 */
public class ApiError {

	/**
	 * Código identificador del tipo de error.
	 *
	 * <p>
	 * Permite a los clientes identificar programáticamente el tipo de error
	 * ocurrido sin depender únicamente del mensaje textual.
	 * </p>
	 */
	public String code;

	/**
	 * Mensaje principal que describe el error ocurrido.
	 *
	 * <p>
	 * Este mensaje está pensado para ser legible por humanos y explicar
	 * de forma clara el problema.
	 * </p>
	 */
	public String message;

	/**
	 * Información adicional sobre el error.
	 *
	 * <p>
	 * Puede incluir detalles técnicos o información específica que ayude
	 * a comprender mejor el contexto del fallo.
	 * </p>
	 */
	public String details;

	/**
	 * Crea una nueva instancia de {@link ApiError}.
	 *
	 * @param code código identificador del error
	 * @param message mensaje descriptivo del error
	 * @param details información adicional sobre el error
	 */
	public ApiError(String code, String message, String details) {
		this.code = code;
		this.message = message;
		this.details = details;
	}
}