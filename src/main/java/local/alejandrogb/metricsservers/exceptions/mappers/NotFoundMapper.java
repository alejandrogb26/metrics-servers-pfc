package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

/**
 * Mapper encargado de transformar excepciones {@link NotFoundException}
 * en respuestas HTTP estructuradas para la API.
 *
 * <p>
 * Este componente forma parte del sistema global de manejo de errores
 * de la aplicación basado en {@link ExceptionMapper}. Cuando se lanza
 * una {@link NotFoundException} durante el procesamiento de una petición,
 * el framework JAX-RS invoca automáticamente este mapper para generar
 * una respuesta HTTP adecuada.
 * </p>
 *
 * <p>
 * La respuesta generada utiliza el código de estado {@code 404 Not Found}
 * e incluye un objeto {@link ApiError} que describe el problema ocurrido.
 * </p>
 *
 * <p>
 * Este tipo de error suele producirse cuando el cliente intenta acceder
 * a un recurso que no existe en el sistema, por ejemplo:
 * </p>
 *
 * <ul>
 * <li>Consultar un usuario inexistente.</li>
 * <li>Actualizar un servidor que no está registrado.</li>
 * <li>Eliminar un recurso que ya ha sido borrado.</li>
 * </ul>
 *
 * <p>
 * El uso de este mapper permite mantener los recursos y servicios
 * libres de lógica de generación de respuestas HTTP para errores,
 * centralizando el comportamiento en un único punto del sistema.
 * </p>
 */
@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {

	/**
	 * Convierte una {@link NotFoundException} en una respuesta HTTP
	 * con código {@code 404 Not Found}.
	 *
	 * <p>
	 * Este método es invocado automáticamente por JAX-RS cuando se
	 * lanza una excepción de tipo {@link NotFoundException}.
	 * </p>
	 *
	 * @param e excepción capturada durante el procesamiento de la petición
	 * @return respuesta HTTP con código 404 y un objeto {@link ApiError}
	 *         que describe el recurso no encontrado
	 */
	@Override
	public Response toResponse(NotFoundException e) {

		return Response.status(404)
				.entity(new ApiError(
						"NOT_FOUND",
						"El recurso solicitado no existe",
						e.getMessage()
				))
				.build();
	}
}