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

@Path("/usuarios/{userId}/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TokenResource {
	private final TokenService service = new TokenService();

	@GET
	public List<ApiToken> getTokens(@PathParam("userId") int userId) {
		return service.getUsuarioTokens(userId);
	}

	@POST
	public Response createToken(@PathParam("userId") int userId) {
		String token = service.generateNewToken(userId);
		return Response.status(Response.Status.CREATED).entity(Map.of("token", token)).build();
	}

	@PUT
	@Path("/{tokenId}/status")
	public Response toggleStatus(@PathParam("tokenId") int tokenId, @QueryParam("active") boolean active) {
		boolean ok = active ? service.activateToken(tokenId) : service.revokeToken(tokenId);
		return ok ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
	}
}
