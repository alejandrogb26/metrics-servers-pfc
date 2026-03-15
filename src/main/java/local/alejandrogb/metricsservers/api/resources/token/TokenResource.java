package local.alejandrogb.metricsservers.api.resources.token;

import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import local.alejandrogb.metricsservers.api.services.token.TokenService;
import local.alejandrogb.metricsservers.models.usuario.ApiToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@Path("/usuarios/{userId}/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tokens", description = "Gestión de tokens de acceso de los usuarios")
public class TokenResource {

	private final TokenService service = new TokenService();

	@GET
	@Operation(summary = "Obtener tokens de un usuario", description = "Devuelve todos los tokens asociados a un usuario")
	@ApiResponse(responseCode = "200", description = "Lista de tokens", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiToken.class))))
	public List<ApiToken> getTokens(
			@Parameter(description = "ID del usuario propietario de los tokens", required = true, example = "5") @PathParam("userId") int userId) {

		return service.getUsuarioTokens(userId);
	}

	@POST
	@Operation(summary = "Crear token de acceso", description = "Genera un nuevo token de acceso para el usuario")
	@ApiResponse(responseCode = "201", description = "Token creado correctamente")
	public Response createToken(
			@Parameter(description = "ID del usuario", required = true, example = "5") @PathParam("userId") int userId) {

		String token = service.generateNewToken(userId);

		return Response.status(Response.Status.CREATED).entity(Map.of("token", token)).build();
	}

	@PUT
	@Path("/{tokenId}/status")
	@Operation(summary = "Activar o revocar token", description = "Permite activar o desactivar un token existente")
	@ApiResponse(responseCode = "200", description = "Estado del token actualizado")
	@ApiResponse(responseCode = "404", description = "Token no encontrado")
	public Response toggleStatus(

			@Parameter(description = "ID del token", required = true, example = "12") @PathParam("tokenId") int tokenId,

			@Parameter(description = "Indica si el token debe estar activo", required = true, example = "true") @QueryParam("active") boolean active) {

		boolean ok = active ? service.activateToken(tokenId) : service.revokeToken(tokenId);

		return ok ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
	}
}