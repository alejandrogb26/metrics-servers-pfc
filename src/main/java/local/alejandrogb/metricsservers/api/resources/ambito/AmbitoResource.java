package local.alejandrogb.metricsservers.api.resources.ambito;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Ambito;

@Path("/ambitos")
@Produces(MediaType.APPLICATION_JSON)
public class AmbitoResource {
	private final DaoApi dao = DaoApi.getInstance(); // Acceso directo al ser solo lectura

	@GET
	public List<Ambito> getPermisos() {
		return dao.findAllAmbito();
	}

	@GET
	@Path("/{id}")
	public Response getAmbitoById(@PathParam("id") int id) {
		Ambito ambito = dao.findAmbitoById(id);
		return (ambito != null) ? Response.ok(ambito).build() : Response.status(404).build();
	}
}
