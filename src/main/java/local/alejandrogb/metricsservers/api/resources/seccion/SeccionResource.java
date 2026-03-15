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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/seccion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Secciones", description = "Gestión de secciones del sistema")
public class SeccionResource {

	private final SeccionService service = new SeccionService();

	@GET
	@Path("/{id}")
	@Operation(summary = "Obtiene una sección por ID", description = "Devuelve la información de una sección específica")
	@ApiResponse(responseCode = "200", description = "Sección encontrada", content = @Content(schema = @Schema(implementation = Seccion.class)))
	@ApiResponse(responseCode = "404", description = "Sección no encontrada")
	public Seccion findById(
			@Parameter(description = "Identificador único de la sección", required = true, example = "1") @PathParam("id") int id) {

		return service.findById(id);
	}

	@GET
	@Operation(summary = "Obtiene todas las secciones", description = "Devuelve la lista completa de secciones registradas")
	@ApiResponse(responseCode = "200", description = "Lista de secciones", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Seccion.class))))
	public List<Seccion> findAll() {
		return service.findAll();
	}

	@POST
	@Operation(summary = "Crear una sección", description = "Crea una nueva sección en el sistema")
	@RequestBody(description = "Datos de la sección a crear", required = true, content = @Content(schema = @Schema(implementation = Seccion.class)))
	@ApiResponse(responseCode = "201", description = "Sección creada correctamente")
	public Response create(Seccion seccion) {

		int id = service.insert(seccion);

		return Response.status(Response.Status.CREATED).entity(Map.of("id", id)).build();
	}

	@PATCH
	@Path("/{id}")
	@Operation(summary = "Actualizar parcialmente una sección", description = "Actualiza solo los campos enviados en el JSON")
	@RequestBody(description = "Campos de la sección a actualizar", required = true)
	@ApiResponse(responseCode = "204", description = "Sección actualizada correctamente")
	@ApiResponse(responseCode = "404", description = "Sección no encontrada")
	public Response update(
			@Parameter(description = "Identificador de la sección", required = true, example = "1") @PathParam("id") int id,
			Map<String, Object> fields) {

		service.update(id, fields);

		return Response.noContent().build();
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Eliminar una sección", description = "Elimina una sección del sistema")
	@ApiResponse(responseCode = "204", description = "Sección eliminada correctamente")
	@ApiResponse(responseCode = "404", description = "Sección no encontrada")
	public Response delete(
			@Parameter(description = "Identificador de la sección", required = true, example = "1") @PathParam("id") int id) {

		if (!service.delete(id)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.noContent().build();
	}
}