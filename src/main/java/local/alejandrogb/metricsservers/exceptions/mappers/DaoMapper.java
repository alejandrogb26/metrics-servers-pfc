package local.alejandrogb.metricsservers.exceptions.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.exceptions.DaoException;
import local.alejandrogb.metricsservers.exceptions.api.ApiError;

@Provider
public class DaoMapper implements ExceptionMapper<DaoException> {

	@Override
	public Response toResponse(DaoException e) {
		return Response.status(500).entity(new ApiError("DB_ERROR", "Error de base de datos", e.getMessage())).build();
	}

}
