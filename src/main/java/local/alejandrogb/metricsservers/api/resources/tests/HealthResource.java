package local.alejandrogb.metricsservers.api.resources.tests;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
public class HealthResource {
	@Resource(name = "jdbc/MariaDBDS")
	private DataSource dataSource;

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

	@GET
	@Path("/tables")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTables() {

		List<String> tables = new ArrayList<>();

		try (Connection conn = dataSource.getConnection()) {

			DatabaseMetaData meta = conn.getMetaData();

			try (ResultSet rs = meta.getTables(conn.getCatalog(), // database
					null, "%", new String[] { "TABLE" })) {

				while (rs.next()) {
					tables.add(rs.getString("TABLE_NAME"));
				}
			}

			return Response.ok(tables).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
