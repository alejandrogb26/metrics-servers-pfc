package local.alejandrogb.metricsservers.api.resources.servicio;

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

import local.alejandrogb.metricsservers.api.services.servicio.ServicioService;
import local.alejandrogb.metricsservers.models.Servicio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/servicio")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Servicios", description = "Gestión de servicios monitorizados")
public class ServicioResource {

	private final ServicioService service = new ServicioService();

	@GET
	@Path("/{id}")
	@Operation(summary = "Obtiene un servicio por ID", description = "Devuelve la información de un servicio específico")
	@ApiResponse(responseCode = "200", description = "Servicio encontrado", content = @Content(schema = @Schema(implementation = Servicio.class)))
	@ApiResponse(responseCode = "404", description = "Servicio no encontrado")
	public Response findById(
			@Parameter(description = "Identificador único del servicio", required = true, example = "1") @PathParam("id") int id) {

		Servicio s = service.findById(id);

		return (s != null) ? Response.ok(s).build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@GET
	@Operation(summary = "Obtiene todos los servicios", description = "Devuelve la lista completa de servicios registrados")
	@ApiResponse(responseCode = "200", description = "Lista de servicios", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Servicio.class))))
	public List<Servicio> findAll() {
		return service.findAll();
	}

	@POST
	@Operation(summary = "Crear un servicio", description = "Crea un nuevo servicio en el sistema")
	@RequestBody(description = "Datos del servicio a crear", required = true, content = @Content(schema = @Schema(implementation = Servicio.class)))
	@ApiResponse(responseCode = "201", description = "Servicio creado correctamente")
	public Response create(Servicio servicio) {

		int id = service.insert(servicio);

		return Response.status(Response.Status.CREATED).entity(Map.of("id", id)).build();
	}

	@PATCH
	@Path("/{id}")
	@Operation(summary = "Actualizar parcialmente un servicio", description = "Actualiza únicamente los campos enviados en el JSON")
	@RequestBody(description = "Campos del servicio a actualizar", required = true)
	@ApiResponse(responseCode = "204", description = "Servicio actualizado correctamente")
	@ApiResponse(responseCode = "404", description = "Servicio no encontrado")
	public Response update(
			@Parameter(description = "Identificador del servicio", required = true, example = "1") @PathParam("id") int id,
			Map<String, Object> fields) {

		service.update(id, fields);

		return Response.noContent().build();
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Eliminar un servicio", description = "Elimina un servicio del sistema")
	@ApiResponse(responseCode = "204", description = "Servicio eliminado correctamente")
	@ApiResponse(responseCode = "404", description = "Servicio no encontrado")
	public Response delete(
			@Parameter(description = "Identificador del servicio", required = true, example = "1") @PathParam("id") int id) {

		if (!service.delete(id)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.noContent().build();
	}
}