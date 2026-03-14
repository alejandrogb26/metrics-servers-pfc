package local.alejandrogb.metricsservers.api.resources.seccion;

import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.alejandrogb.metricsservers.api.services.seccion.SeccionService;
import local.alejandrogb.metricsservers.models.Seccion;

@Path("/seccion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeccionResource {

	private final SeccionService service = new SeccionService();

	@GET
	@Path("/{id}")
	public Seccion findById(@PathParam("id") int id) {
		return service.findById(id);
	}

	@GET
	public List<Seccion> findAll() {
		return service.findAll();
	}

	@POST
	public Response create(Seccion seccion) {
		int id = service.insert(seccion);
		return Response.status(Response.Status.CREATED).entity(Map.of("id", id)).build();
	}

	@PATCH
	@Path("/{id}")
	public Response update(@PathParam("id") int id, Map<String, Object> fields) {
		service.update(id, fields);
		return Response.noContent().build(); // 204
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") int id) {
		if (!service.delete(id)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.noContent().build(); // 204
	}
}