package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

@Provider
public class ValidationMapper implements ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException e) {
		return Response.status(422).entity(new ApiError("VALIDATION_ERROR", e.getMessage(), null)).build();
	}

}
