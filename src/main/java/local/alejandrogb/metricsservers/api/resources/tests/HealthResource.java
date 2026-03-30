package local.alejandrogb.metricsservers.api.resources.tests;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.alejandrogb.metricsservers.utils.GetDataSource;

/**
 * Endpoint de diagnóstico del sistema.
 *
 * <p>
 * {@code GET /health/status} es público (necesario para healthchecks de
 * infraestructura). El endpoint {@code /health/tables} se ha eliminado —
 * exponer el esquema de la base de datos es un riesgo de seguridad innecesario.
 * </p>
 */
@Path("/health")
public class HealthResource {

	private final DataSource dataSource = GetDataSource.getDataSource();

	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkDb() {
		try (Connection conn = dataSource.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT 1")) {

			if (rs.next()) {
				return Response.ok("{\"status\":\"OK\",\"db\":\"up\"}").build();
			}

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"status\":\"ERROR\",\"db\":\"no result\"}").build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"status\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}").build();
		}
	}
}
