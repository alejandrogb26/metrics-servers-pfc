package local.alejandrogb.metricsservers.api.resources.login;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.alejandrogb.metricsservers.api.services.login.LoginService;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
	private final LoginService service = new LoginService();

	@GET
	public Response getSession(@HeaderParam("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer "))
			return Response.status(Response.Status.UNAUTHORIZED).build();

		String token = authHeader.substring(7);
		return Response.ok(service.getSession(token)).build();
	}
}
