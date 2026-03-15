package local.alejandrogb.metricsservers.api.resources.usuario;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import local.alejandrogb.metricsservers.api.services.usuario.UsuarioService;
import local.alejandrogb.metricsservers.models.usuario.Usuario;
import local.alejandrogb.metricsservers.utils.BulkResult;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioResource {

	private final UsuarioService service = new UsuarioService();

	@GET
	@Operation(summary = "Obtiene todos los usuarios")
	@ApiResponse(responseCode = "200", description = "Lista de usuarios", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
	public List<Usuario> getAll() {
		return service.getAll();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Obtiene un usuario por ID")
	@ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = Usuario.class)))
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	public Response getById(@Parameter(description = "ID del usuario", example = "1") @PathParam("id") int id) {

		Usuario u = service.getUsuario(id);

		return (u != null) ? Response.ok(u).build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Operation(summary = "Crear usuarios en lote")
	@RequestBody(description = "Lista de usuarios a crear", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
	@ApiResponse(responseCode = "200", description = "Resultado de la creación", content = @Content(schema = @Schema(implementation = BulkResult.class)))
	public Response create(List<Usuario> usuarios) {

		BulkResult result = service.createUsuarios(usuarios);

		return Response.ok(result).build();
	}

	@PATCH
	@Path("/{id}")
	@Operation(summary = "Actualizar parcialmente un usuario")
	@RequestBody(description = "Campos del usuario a actualizar", content = @Content(schema = @Schema(implementation = Usuario.class)))
	@ApiResponse(responseCode = "200", description = "Usuario actualizado")
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	public Response update(@Parameter(description = "ID del usuario", example = "1") @PathParam("id") int id,
			Usuario datosParciales) {

		boolean updated = service.patchUsuario(id, datosParciales);

		return updated ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@DELETE
	@Operation(summary = "Eliminar usuarios en lote")
	@RequestBody(description = "Lista de IDs de usuarios", content = @Content(array = @ArraySchema(schema = @Schema(type = "integer"))))
	@ApiResponse(responseCode = "200", description = "Resultado de la eliminación", content = @Content(schema = @Schema(implementation = BulkResult.class)))
	public Response delete(List<Integer> ids) {

		BulkResult result = service.deleteUsuarios(ids);

		return Response.ok(result).build();
	}

	@POST
	@Path("/{id}/foto")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Subir foto de perfil del usuario")
	@ApiResponse(responseCode = "200", description = "Foto subida correctamente")
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	public Response subirFoto(@Parameter(description = "ID del usuario", example = "1") @PathParam("id") int id,
			@FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		try {

			Usuario u = service.getUsuario(id);
			if (u == null)
				return Response.status(Status.NOT_FOUND).build();

			String extension = fileDetail.getFileName().substring(fileDetail.getFileName().lastIndexOf("."));

			String nombreArchivoMinio = "user_" + id + "_" + System.currentTimeMillis() + extension;

			service.actualizarFotoPerfil(id, fileStream, nombreArchivoMinio);

			return Response.ok(Map.of("nombreArchivo", nombreArchivoMinio)).build();

		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}