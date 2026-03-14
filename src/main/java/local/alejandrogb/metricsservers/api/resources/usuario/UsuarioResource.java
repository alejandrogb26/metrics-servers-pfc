package local.alejandrogb.metricsservers.api.resources.usuario;

import java.io.InputStream;
import java.util.List;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
import jakarta.ws.rs.core.Response.Status;
import local.alejandrogb.metricsservers.api.services.usuario.UsuarioService;
import local.alejandrogb.metricsservers.models.usuario.Usuario;
import local.alejandrogb.metricsservers.utils.BulkResult;

@Path("/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
	private final UsuarioService service = new UsuarioService();

	@GET
	public List<Usuario> getAll() {
		return service.getAll();
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") int id) {
		Usuario u = service.getUsuario(id);
		return (u != null) ? Response.ok(u).build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	public Response create(List<Usuario> usuarios) {
		BulkResult result = service.createUsuarios(usuarios);

		return Response.ok(result).build();
	}

	@PATCH
	@Path("/{id}")
	public Response update(@PathParam("id") int id, Usuario datosParciales) {
		boolean updated = service.patchUsuario(id, datosParciales);
		return updated ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@DELETE
	public Response delete(List<Integer> ids) {
		BulkResult result = service.deleteUsuarios(ids);
		return Response.ok(result).build();
	}

	@POST
	@Path("/{id}/foto")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response subirFoto(@PathParam("id") int id, @FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		try {
			// 1. Validar que el usuario existe
			Usuario u = service.getUsuario(id);
			if (u == null)
				return Response.status(Status.NOT_FOUND).build();

			// 2. Extraer extensión y generar nombre único
			String extension = fileDetail.getFileName().substring(fileDetail.getFileName().lastIndexOf("."));
			String nombreArchivoMinio = "user_" + id + "_" + System.currentTimeMillis() + extension;

			// 3. Delegar al servicio
			service.actualizarFotoPerfil(id, fileStream, nombreArchivoMinio);

			return Response.ok("{\"nombreArchivo\": \"" + nombreArchivoMinio + "\"}").build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
