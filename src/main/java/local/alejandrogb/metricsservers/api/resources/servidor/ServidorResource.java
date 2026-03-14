package local.alejandrogb.metricsservers.api.resources.servidor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import local.alejandrogb.metricsservers.api.services.servidor.ServidorService;
import local.alejandrogb.metricsservers.models.metrics.MetricPoint;
import local.alejandrogb.metricsservers.models.servidor.Servidor;
import local.alejandrogb.metricsservers.models.servidor.ServidorDTO;
import local.alejandrogb.metricsservers.utils.BulkResult;

@Path("/servidor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServidorResource {

	private final ServidorService service = new ServidorService();

	@GET
	@Path("/{id}")
	public Response findServidorById(@PathParam("id") int id) {
		Servidor s = service.findServidorById(id);
		return (s != null) ? Response.ok(s).build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@GET
	public List<Servidor> findServidores() {
		return service.findServidores();
	}

	@POST
	@Path("/bulk")
	public Response createServidores(List<ServidorDTO> servidores) {
		if (servidores == null || servidores.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		BulkResult result = service.insertServidores(servidores);

		// Si fallaron todos o algunos, usamos 207 (Multi-Status)
		// Si todos fueron OK, usamos 201 (Created)
		int status = (result.getFailed() == 0) ? 201 : 207;

		return Response.status(status).entity(result).build();
	}

	@PATCH
	@Path("/{id}")
	public Response patchServidor(@PathParam("id") int id, Map<String, Object> fields) {
		service.updateServidor(id, fields);

		// 204: Éxito, pero no envío el cuerpo del objeto (API ligera)
		return Response.noContent().build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteServidor(@PathParam("id") int id) {
		boolean deleted = service.deleteServidor(id);

		if (!deleted) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.noContent().build(); // 204
	}

	@DELETE
	@Path("/bulk")
	public Response deleteServidores(List<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		BulkResult result = service.deleteServidores(ids);

		// En borrado por lotes, siempre devolvemos el informe del resultado
		return Response.ok(result).build();
	}

	@POST
	@Path("/{id}/servicios")
	public Response addServicios(@PathParam("id") int servidorId, List<Integer> servicioIds) {
		int count = service.addServicios(servidorId, servicioIds);
		return Response.ok(Map.of("added", count)).build();
	}

	@DELETE
	@Path("/{id}/servicios")
	public Response removeServicios(@PathParam("id") int servidorId, List<Integer> servicioIds) {
		int count = service.removeServicios(servidorId, servicioIds);
		return Response.ok(Map.of("removed", count)).build();
	}

	@POST
	@Path("/{id}/foto")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response subirFoto(@PathParam("id") int id, @FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		try {
			// 1. Validar que el servidor existe
			Servidor s = service.findServidorById(id);
			if (s == null)
				return Response.status(Status.NOT_FOUND).build();

			// 2. Extraer extensión y generar nombre único
			String extension = fileDetail.getFileName().substring(fileDetail.getFileName().lastIndexOf("."));
			String nombreArchivoMinio = "server_" + id + "_" + System.currentTimeMillis() + extension;

			// 3. Delegar al servicio
			service.actualizarFotoPerfil(id, fileStream, nombreArchivoMinio);

			return Response.ok("{\"nombreArchivo\": \"" + nombreArchivoMinio + "\"}").build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{serverId}/metrics")
	public Response getMetrics(@PathParam("serverId") String serverId,
			@QueryParam("range") @DefaultValue("60") Long minutes) {
		List<MetricPoint> metrics = service.getServidorHistory(serverId, minutes);

		if (metrics.isEmpty())
			return Response.status(Response.Status.NO_CONTENT).build();

		return Response.ok(metrics).build();
	}
}