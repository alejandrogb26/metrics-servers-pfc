package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.ProbeException;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

/**
 * Mapper encargado de transformar excepciones {@link ProbeException}
 * en respuestas HTTP adecuadas para la API.
 *
 * <p>
 * Esta clase forma parte del sistema global de manejo de errores basado
 * en {@link ExceptionMapper}. Cuando se produce una {@link ProbeException}
 * durante la ejecución de una operación que requiere consultar un servidor
 * remoto (por ejemplo mediante SSH), este mapper intercepta la excepción
 * y genera una respuesta HTTP estructurada.
 * </p>
 *
 * <p>
 * La respuesta se devuelve con código de estado {@code 503 Service Unavailable},
 * indicando que el servicio externo (el servidor remoto consultado) no está
 * disponible o no pudo ser alcanzado en ese momento.
 * </p>
 *
 * <p>
 * Este tipo de error suele producirse en situaciones como:
 * </p>
 *
 * <ul>
 * <li>Servidor remoto inaccesible por red.</li>
 * <li>Fallo en la conexión SSH.</li>
 * <li>Error durante la ejecución de comandos remotos.</li>
 * <li>Problemas de autenticación en el servidor.</li>
 * </ul>
 *
 * <p>
 * El uso de este mapper permite aislar los errores de infraestructura
 * relacionados con la comunicación con servidores externos y presentar
 * al cliente una respuesta consistente en formato {@link ApiError}.
 * </p>
 */
@Provider
public class ProbeMapper implements ExceptionMapper<ProbeException> {

	/**
	 * Convierte una {@link ProbeException} en una respuesta HTTP
	 * con código {@code 503 Service Unavailable}.
	 *
	 * <p>
	 * Este método es invocado automáticamente por el framework JAX-RS
	 * cuando se lanza una excepción de tipo {@link ProbeException}.
	 * </p>
	 *
	 * @param exception excepción capturada durante la consulta a un servidor remoto
	 * @return respuesta HTTP con código 503 y un objeto {@link ApiError}
	 *         que indica que el servidor no pudo ser consultado
	 */
	@Override
	public Response toResponse(ProbeException exception) {

		return Response.status(503)
				.entity(new ApiError(
						"SSH_ERROR",
						"No se puedo consultar el servidor",
						null
				))
				.build();
	}
}