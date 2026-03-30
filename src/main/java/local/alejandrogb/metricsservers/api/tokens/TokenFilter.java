package local.alejandrogb.metricsservers.api.tokens;

import java.io.IOException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class TokenFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {

		String path = ctx.getUriInfo().getPath();

		if (isPublicPath(path)) {
			return;
		}

		String authHeader = ctx.getHeaderString("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			abort(ctx, "Token no proporcionado");
			return;
		}

		String token = authHeader.substring(7).trim();

		Claims claims = validateToken(token);

		if (claims == null) {
			abort(ctx, "Token inválido");
			return;
		}

		// El JWT del nuevo flujo AD usa 'username' como subject y claim principal.
		// Ya no existe 'userId' — la identidad es el sAMAccountName.
		String username = claims.getSubject();
		Integer grupoId = toInteger(claims.get("grupoId"));
		Boolean superadmin = claims.get("superadmin", Boolean.class);

		if (username == null || username.isBlank()) {
			abort(ctx, "Token inválido");
			return;
		}

		ctx.setProperty("username", username);
		ctx.setProperty("grupoId", grupoId);
		ctx.setProperty("superadmin", superadmin);
	}

	private boolean isPublicPath(String path) {
		// Excluir el endpoint de login y la documentación Swagger/OpenAPI
		return path.equals("auth/login") || path.equals("/auth/login") || path.startsWith("openapi")
				|| path.contains("swagger-ui");
	}

	private Claims validateToken(String token) {
		try {
			return Jwts.parser().verifyWith(JwtConfig.getSigningKey()).build().parseSignedClaims(token).getPayload();
		} catch (JwtException | IllegalArgumentException e) {
			return null;
		}
	}

	private Integer toInteger(Object value) {
		if (value == null)
			return null;
		if (value instanceof Integer i)
			return i;
		if (value instanceof Number n)
			return n.intValue();
		return Integer.parseInt(value.toString());
	}

	private void abort(ContainerRequestContext ctx, String message) {
		ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON)
				.entity("{\"error\":\"" + message.replace("\"", "\\\"") + "\"}").build());
	}
}