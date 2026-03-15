package local.alejandrogb.metricsservers.api.resources.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import local.alejandrogb.metricsservers.utils.GetDataSource;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class TokenFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {

		String path = ctx.getUriInfo().getPath();

		if (path.startsWith("openapi") || path.contains("swagger-ui")) {
			return;
		}

		String authHeader = ctx.getHeaderString("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			abort(ctx);
			return;
		}

		String token = authHeader.substring(7);

		Integer userId = getUserFromToken(token);

		if (userId == null) {
			abort(ctx);
			return;
		}

		// Guardar usuario para usar luego
		ctx.setProperty("userId", userId);
	}

	private Integer getUserFromToken(String token) {

		String sql = """
				    SELECT usuarioId
				    FROM api_tokens
				    WHERE token=? AND active=1
				""";

		DataSource dataSource = GetDataSource.getDataSource();

		if (dataSource == null)
			return null;

		try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, token);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("usuarioId");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void abort(ContainerRequestContext ctx) {
		ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Token inválido\"}").build());
	}
}
