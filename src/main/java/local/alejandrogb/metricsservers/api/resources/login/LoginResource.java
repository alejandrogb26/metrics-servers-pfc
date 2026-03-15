package local.alejandrogb.metricsservers.api.resources.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Login", description = "Gestión de autenticación y sesión de usuario")
public class LoginResource {

	private final LoginService service = new LoginService();

	@GET
	@Operation(summary = "Obtener sesión del usuario", description = "Valida el token enviado en el header Authorization y devuelve la información de la sesión asociada al usuario")
	@ApiResponse(responseCode = "200", description = "Sesión válida")
	@ApiResponse(responseCode = "401", description = "Token no proporcionado o inválido")
	public Response getSession(

			@Parameter(description = "Token de autenticación en formato Bearer", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") @HeaderParam("Authorization") String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer "))
			return Response.status(Response.Status.UNAUTHORIZED).build();

		String token = authHeader.substring(7);

		return Response.ok(service.getSession(token)).build();
	}
}