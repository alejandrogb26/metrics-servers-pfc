package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.DaoException;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

/**
 * Mapper encargado de transformar excepciones {@link DaoException} en
 * respuestas HTTP adecuadas para la API.
 *
 * <p>
 * Esta clase forma parte del mecanismo de manejo global de errores
 * proporcionado por JAX-RS. Cuando se lanza una {@link DaoException} durante el
 * procesamiento de una petición, este mapper intercepta la excepción y genera
 * una respuesta HTTP estructurada.
 * </p>
 *
 * <p>
 * La respuesta se devuelve con código de estado
 * {@code 500 Internal Server Error} e incluye un objeto {@link ApiError} con
 * información detallada sobre el problema ocurrido.
 * </p>
 *
 * <p>
 * El uso de {@code ExceptionMapper} permite centralizar el tratamiento de
 * errores y mantener los recursos y servicios libres de lógica específica de
 * manejo de excepciones.
 * </p>
 */
@Provider
public class DaoMapper implements ExceptionMapper<DaoException> {

	/**
	 * Convierte una {@link DaoException} en una respuesta HTTP estructurada.
	 *
	 * <p>
	 * Este método es invocado automáticamente por el framework JAX-RS cuando se
	 * lanza una excepción de tipo {@link DaoException}.
	 * </p>
	 *
	 * @param e excepción capturada durante la ejecución de una operación DAO
	 * @return respuesta HTTP con código 500 y un objeto {@link ApiError} que
	 *         describe el error ocurrido
	 */
	@Override
	public Response toResponse(DaoException e) {

		return Response.status(500).entity(new ApiError("DB_ERROR", "Error de base de datos", e.getMessage())).build();
	}
}