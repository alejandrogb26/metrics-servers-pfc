package local.alejandrogb.metricsservers.api.resources.grupo;

import java.util.List;

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
import local.alejandrogb.metricsservers.api.services.grupo.GrupoService;
import local.alejandrogb.metricsservers.models.Grupo;
import local.alejandrogb.metricsservers.utils.BulkResult;

@Path("/grupos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GrupoResource {
	private final GrupoService service = new GrupoService();

	@GET
	public List<Grupo> getAll() {
		return service.getAll();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") int id) {
		Grupo g = service.getById(id);
		if (g == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		return Response.ok(g).build();
	}

	@POST
	public Response create(List<Grupo> grupos) {
		BulkResult result = service.createGrupos(grupos);
		return Response.status(Response.Status.CREATED).entity(result).build();
	}

	@PATCH
	@Path("/{id}")
	public Response update(@PathParam("id") int id, Grupo grupo) {
		// El objeto 'grupo' aquí solo tendrá rellenos los campos que el cliente envió
		// en el JSON
		boolean ok = service.patchGrupo(id, grupo);
		if (!ok)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(service.getById(id)).build();
	}

	@DELETE
	public Response delete(List<Integer> ids) {
		BulkResult result = service.deleteGrupos(ids);
		return Response.ok(result).build();
	}
}
