package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.ProbeException;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

@Provider
public class ProbeMapper implements ExceptionMapper<ProbeException> {

	@Override
	public Response toResponse(ProbeException exception) {
		return Response.status(503).entity(new ApiError("SSH_ERROR", "No se puedo consultar el servidor", null))
				.build();
	}

}
