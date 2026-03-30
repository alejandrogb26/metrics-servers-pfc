package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

/**
 * Mapper encargado de transformar excepciones {@link ValidationException} en
 * respuestas HTTP adecuadas para la API.
 *
 * <p>
 * Esta clase forma parte del sistema global de manejo de errores basado en
 * {@link ExceptionMapper}. Cuando durante el procesamiento de una petición se
 * detecta un error de validación en los datos proporcionados por el cliente, el
 * framework JAX-RS invoca automáticamente este mapper para generar una
 * respuesta HTTP estructurada.
 * </p>
 *
 * <p>
 * La respuesta se devuelve con código de estado
 * {@code 422 Unprocessable Entity}, indicando que la petición fue correctamente
 * interpretada por el servidor, pero contiene datos inválidos que impiden
 * completar la operación solicitada.
 * </p>
 *
 * <p>
 * Este tipo de error suele producirse en situaciones como:
 * </p>
 *
 * <ul>
 * <li>Campos obligatorios no proporcionados.</li>
 * <li>Valores con formato incorrecto.</li>
 * <li>Intento de modificar campos no permitidos.</li>
 * <li>Datos que incumplen reglas de negocio.</li>
 * </ul>
 *
 * <p>
 * La respuesta incluye un objeto {@link ApiError} que describe el error
 * ocurrido y permite al cliente identificar claramente el problema.
 * </p>
 */
@Provider
public class ValidationMapper implements ExceptionMapper<ValidationException> {

	/**
	 * Convierte una {@link ValidationException} en una respuesta HTTP con código
	 * {@code 422 Unprocessable Entity}.
	 *
	 * <p>
	 * Este método es invocado automáticamente por el framework JAX-RS cuando se
	 * lanza una excepción de tipo {@link ValidationException}.
	 * </p>
	 *
	 * @param e excepción de validación capturada durante el procesamiento de la
	 *          petición
	 * @return respuesta HTTP con código 422 y un objeto {@link ApiError} que
	 *         describe el error de validación
	 */
	@Override
	public Response toResponse(ValidationException e) {

		return Response.status(422).entity(new ApiError("VALIDATION_ERROR", e.getMessage(), null)).build();
	}
}