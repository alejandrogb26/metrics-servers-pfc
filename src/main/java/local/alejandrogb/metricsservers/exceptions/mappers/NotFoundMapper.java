package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(NotFoundException e) {
		return Response.status(404).entity(new ApiError("NOT_FOUND", "El recurso solicitado no existe", e.getMessage()))
				.build();
	}

}
