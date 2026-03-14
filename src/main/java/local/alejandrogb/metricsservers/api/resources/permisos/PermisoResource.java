package local.alejandrogb.metricsservers.api.resources.permisos;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Permiso;

@Path("/permisos")
@Produces(MediaType.APPLICATION_JSON)
public class PermisoResource {
	private final DaoApi dao = DaoApi.getInstance(); // Acceso directo al ser solo lectura

	@GET
	public List<Permiso> getPermisos() {
		return dao.findAllPermisos();
	}

	@GET
	@Path("/{id}")
	public Response getPermisoById(@PathParam("id") int id) {
		Permiso p = dao.findPermisoById(id);
		return (p != null) ? Response.ok(p).build() : Response.status(404).build();
	}
}
