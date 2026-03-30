package local.alejandrogb.metricsservers.api.resources.servidor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Servidores", description = "Gestión de servidores monitorizados")
public class ServidorResource {

	private final ServidorService service = new ServidorService();

	@GET
	@Path("/{id}")
	@Operation(summary = "Obtiene un servidor por ID")
	@ApiResponse(responseCode = "200", description = "Servidor encontrado", content = @Content(schema = @Schema(implementation = Servidor.class)))
	@ApiResponse(responseCode = "404", description = "Servidor no encontrado")
	public Response findServidorById(
			@Parameter(description = "ID del servidor", example = "1") @PathParam("id") int id) {

		Servidor s = service.findServidorById(id);
		return (s != null) ? Response.ok(s).build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@GET
	@Operation(summary = "Obtiene todos los servidores")
	@ApiResponse(responseCode = "200", description = "Lista de servidores", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Servidor.class))))
	public List<Servidor> findServidores() {
		return service.findServidores();
	}

	@POST
	@Path("/bulk")
	@Operation(summary = "Crear servidores en lote")
	@RequestBody(description = "Lista de servidores a crear", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServidorDTO.class))))
	@ApiResponse(responseCode = "201", description = "Todos los servidores creados", content = @Content(schema = @Schema(implementation = BulkResult.class)))
	@ApiResponse(responseCode = "207", description = "Creación parcial", content = @Content(schema = @Schema(implementation = BulkResult.class)))
	public Response createServidores(List<ServidorDTO> servidores) {

		if (servidores == null || servidores.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		BulkResult result = service.insertServidores(servidores);
		int status = (result.getFailed() == 0) ? 201 : 207;

		return Response.status(status).entity(result).build();
	}

	@PATCH
	@Path("/{id}")
	@Operation(summary = "Actualizar parcialmente un servidor")
	@RequestBody(description = "Campos del servidor a actualizar")
	@ApiResponse(responseCode = "204", description = "Servidor actualizado")
	public Response patchServidor(@Parameter(description = "ID del servidor", example = "1") @PathParam("id") int id,
			Map<String, Object> fields) {

		service.updateServidor(id, fields);
		return Response.noContent().build();
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Eliminar un servidor")
	@ApiResponse(responseCode = "204", description = "Servidor eliminado")
	@ApiResponse(responseCode = "404", description = "Servidor no encontrado")
	public Response deleteServidor(@Parameter(description = "ID del servidor", example = "1") @PathParam("id") int id) {

		boolean deleted = service.deleteServidor(id);

		if (!deleted)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.noContent().build();
	}

	@DELETE
	@Path("/bulk")
	@Operation(summary = "Eliminar servidores en lote")
	@RequestBody(description = "Lista de IDs de servidores", required = true, content = @Content(array = @ArraySchema(schema = @Schema(type = "integer"))))
	@ApiResponse(responseCode = "200", description = "Resultado de la operación", content = @Content(schema = @Schema(implementation = BulkResult.class)))
	public Response deleteServidores(List<Integer> ids) {

		if (ids == null || ids.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		BulkResult result = service.deleteServidores(ids);
		return Response.ok(result).build();
	}

	@POST
	@Path("/{id}/servicios")
	@Operation(summary = "Añadir servicios a un servidor")
	@RequestBody(description = "Lista de IDs de servicios", content = @Content(array = @ArraySchema(schema = @Schema(type = "integer"))))
	public Response addServicios(
			@Parameter(description = "ID del servidor", example = "1") @PathParam("id") int servidorId,
			List<Integer> servicioIds) {

		int count = service.addServicios(servidorId, servicioIds);
		return Response.ok(Map.of("added", count)).build();
	}

	@DELETE
	@Path("/{id}/servicios")
	@Operation(summary = "Eliminar servicios de un servidor")
	@RequestBody(description = "Lista de IDs de servicios", content = @Content(array = @ArraySchema(schema = @Schema(type = "integer"))))
	public Response removeServicios(
			@Parameter(description = "ID del servidor", example = "1") @PathParam("id") int servidorId,
			List<Integer> servicioIds) {

		int count = service.removeServicios(servidorId, servicioIds);
		return Response.ok(Map.of("removed", count)).build();
	}

	@POST
	@Path("/{id}/foto")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Subir foto del servidor")
	@ApiResponse(responseCode = "200", description = "Foto subida correctamente")
	@ApiResponse(responseCode = "404", description = "Servidor no encontrado")
	public Response subirFoto(@Parameter(description = "ID del servidor", example = "1") @PathParam("id") int id,
			@Multipart("file") Attachment fileAttachment) {

		try {
			Servidor s = service.findServidorById(id);
			if (s == null) {
				return Response.status(Status.NOT_FOUND).build();
			}

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

			int lastDot = originalFileName.lastIndexOf('.');
			String extension = lastDot >= 0 ? originalFileName.substring(lastDot) : "";

			String nombreArchivoMinio = "server_" + id + "_" + System.currentTimeMillis() + extension;

			service.actualizarFotoPerfil(id, fileStream, nombreArchivoMinio);

			return Response.ok(Map.of("nombreArchivo", nombreArchivoMinio)).build();

		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", e.getMessage())).build();
		}
	}

	@GET
	@Path("/{serverId}/metrics")
	@Operation(summary = "Obtener histórico de métricas del servidor")
	@ApiResponse(responseCode = "200", description = "Histórico de métricas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MetricPoint.class))))
	@ApiResponse(responseCode = "204", description = "No hay métricas")
	public Response getMetrics(@Parameter(description = "ID del servidor") @PathParam("serverId") String serverId,

			@Parameter(description = "Rango de minutos de histórico", example = "60") @QueryParam("range") @DefaultValue("60") Long minutes) {

		List<MetricPoint> metrics = service.getServidorHistory(serverId, minutes);

		if (metrics.isEmpty())
			return Response.status(Response.Status.NO_CONTENT).build();

		return Response.ok(metrics).build();
	}
}