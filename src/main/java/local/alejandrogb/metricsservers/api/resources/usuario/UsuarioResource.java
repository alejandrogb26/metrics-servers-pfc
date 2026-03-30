package local.alejandrogb.metricsservers.api.resources.usuario;

import java.io.InputStream;
import java.util.Map;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import local.alejandrogb.metricsservers.api.services.usuario.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Usuarios", description = "Operaciones sobre el perfil del usuario autenticado")
public class UsuarioResource {

	private final UsuarioService service = new UsuarioService();

	@POST
	@Path("/foto")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Subir foto de perfil", description = "Sube o reemplaza la foto de perfil del usuario autenticado. El usuario se identifica por el JWT.")
	@ApiResponse(responseCode = "200", description = "Foto subida correctamente")
	@ApiResponse(responseCode = "400", description = "Archivo no proporcionado")
	@ApiResponse(responseCode = "401", description = "Token no válido o ausente")
	public Response subirFoto(@Context ContainerRequestContext requestContext,
			@Multipart("file") Attachment fileAttachment) {

		try {
			if (fileAttachment == null) {
				return Response.status(Status.BAD_REQUEST).entity(Map.of("error", "Archivo no proporcionado")).build();
			}

			InputStream fileStream = fileAttachment.getObject(InputStream.class);
			ContentDisposition contentDisposition = fileAttachment.getContentDisposition();

			String originalFileName = null;
			if (contentDisposition != null) {
				originalFileName = contentDisposition.getParameter("filename");
			}

			if (fileStream == null || originalFileName == null || originalFileName.isBlank()) {
				return Response.status(Status.BAD_REQUEST).entity(Map.of("error", "Archivo no proporcionado")).build();
			}

			String username = (String) requestContext.getProperty("username");

			if (username == null || username.isBlank()) {
				return Response.status(Status.UNAUTHORIZED).entity(Map.of("error", "Token no válido")).build();
			}

			int lastDot = originalFileName.lastIndexOf('.');
			String extension = lastDot >= 0 ? originalFileName.substring(lastDot) : "";

			String nombreArchivoMinio = "user_" + username + "_" + System.currentTimeMillis() + extension;

			service.actualizarFotoPerfil(username, fileStream, nombreArchivoMinio);

			return Response.ok(Map.of("nombreArchivo", nombreArchivoMinio)).build();

		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", e.getMessage())).build();
		}
	}
}