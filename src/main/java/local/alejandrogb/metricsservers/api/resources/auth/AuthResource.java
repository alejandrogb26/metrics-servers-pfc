package local.alejandrogb.metricsservers.api.resources.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.alejandrogb.metricsservers.api.services.auth.AuthService;
import local.alejandrogb.metricsservers.models.login.LoginRequest;
import local.alejandrogb.metricsservers.models.login.LoginResponse;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Autenticación contra Active Directory")
public class AuthResource {

	private final AuthService service = new AuthService();

	@POST
	@Path("/login")
	@Operation(summary = "Login contra Active Directory", description = "Autentica al usuario con sus credenciales de AD y devuelve un JWT junto con la sesión.")
	@ApiResponse(responseCode = "200", description = "Login correcto", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
	@ApiResponse(responseCode = "401", description = "Credenciales inválidas o grupo no autorizado")
	@ApiResponse(responseCode = "422", description = "Username o password no proporcionados")
	public Response login(LoginRequest request) {
		return Response.ok(service.login(request)).build();
	}
}
