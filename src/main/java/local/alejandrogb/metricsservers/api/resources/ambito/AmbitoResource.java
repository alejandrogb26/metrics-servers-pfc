package local.alejandrogb.metricsservers.api.resources.ambito;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import local.alejandrogb.metricsservers.api.services.ambito.AmbitoService;
import local.alejandrogb.metricsservers.models.Ambito;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Path("/ambitos")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Ámbitos", description = "Operaciones relacionadas con los ámbitos del sistema")
public class AmbitoResource {

	private final AmbitoService service = new AmbitoService();

	@GET
	@Operation(summary = "Obtiene todos los ámbitos", description = "Devuelve una lista con todos los ámbitos registrados en el sistema")
	@ApiResponse(responseCode = "200", description = "Lista de ámbitos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Ambito.class))))
	public List<Ambito> getAll() {
		return service.getAll();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Obtiene un ámbito por ID", description = "Devuelve la información de un ámbito específico")
	@ApiResponse(responseCode = "200", description = "Ámbito encontrado", content = @Content(schema = @Schema(implementation = Ambito.class)))
	@ApiResponse(responseCode = "404", description = "Ámbito no encontrado")
	public Response getById(
			@Parameter(description = "Identificador único del ámbito", required = true, example = "1") @PathParam("id") int id) {

		return Response.ok(service.getById(id)).build();
	}
}
