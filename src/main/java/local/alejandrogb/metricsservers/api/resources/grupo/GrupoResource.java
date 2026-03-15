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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/grupos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Grupos", description = "Gestión de grupos del sistema")
public class GrupoResource {

	private final GrupoService service = new GrupoService();

	@GET
	@Operation(summary = "Obtiene todos los grupos", description = "Devuelve la lista completa de grupos registrados en el sistema")
	@ApiResponse(responseCode = "200", description = "Lista de grupos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grupo.class))))
	public List<Grupo> getAll() {
		return service.getAll();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Obtiene un grupo por ID", description = "Devuelve la información de un grupo específico")
	@ApiResponse(responseCode = "200", description = "Grupo encontrado", content = @Content(schema = @Schema(implementation = Grupo.class)))
	@ApiResponse(responseCode = "404", description = "Grupo no encontrado")
	public Response getById(
			@Parameter(description = "Identificador único del grupo", required = true, example = "1") @PathParam("id") int id) {

		Grupo g = service.getById(id);

		if (g == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(g).build();
	}

	@POST
	@Operation(summary = "Crear grupos", description = "Crea uno o varios grupos en el sistema")
	@RequestBody(description = "Lista de grupos a crear", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grupo.class))))
	@ApiResponse(responseCode = "201", description = "Grupos creados correctamente", content = @Content(schema = @Schema(implementation = BulkResult.class)))
	public Response create(List<Grupo> grupos) {

		BulkResult result = service.createGrupos(grupos);

		return Response.status(Response.Status.CREATED).entity(result).build();
	}

	@PATCH
	@Path("/{id}")
	@Operation(summary = "Actualizar parcialmente un grupo", description = "Actualiza únicamente los campos enviados en el JSON")
	@RequestBody(description = "Campos del grupo que se desean actualizar", required = true, content = @Content(schema = @Schema(implementation = Grupo.class)))
	@ApiResponse(responseCode = "200", description = "Grupo actualizado", content = @Content(schema = @Schema(implementation = Grupo.class)))
	@ApiResponse(responseCode = "404", description = "Grupo no encontrado")
	public Response update(
			@Parameter(description = "Identificador del grupo", required = true, example = "1") @PathParam("id") int id,
			Grupo grupo) {

		boolean ok = service.patchGrupo(id, grupo);

		if (!ok)
			return Response.status(Response.Status.NOT_FOUND).build();

		return Response.ok(service.getById(id)).build();
	}

	@DELETE
	@Operation(summary = "Eliminar grupos", description = "Elimina uno o varios grupos del sistema a partir de sus IDs")
	@RequestBody(description = "Lista de IDs de los grupos a eliminar", required = true, content = @Content(array = @ArraySchema(schema = @Schema(type = "integer"))))
	@ApiResponse(responseCode = "200", description = "Resultado de la eliminación", content = @Content(schema = @Schema(implementation = BulkResult.class)))
	public Response delete(List<Integer> ids) {

		BulkResult result = service.deleteGrupos(ids);

		return Response.ok(result).build();
	}
}