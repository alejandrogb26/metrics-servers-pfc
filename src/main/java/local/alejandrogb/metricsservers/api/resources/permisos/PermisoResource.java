package local.alejandrogb.metricsservers.api.resources.permisos;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import local.alejandrogb.metricsservers.api.services.permiso.PermisoService;
import local.alejandrogb.metricsservers.models.Permiso;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@Path("/permisos")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Permisos", description = "Consulta de permisos del sistema")
public class PermisoResource {

	private final PermisoService service = new PermisoService();

	@GET
	@Operation(summary = "Obtiene todos los permisos", description = "Devuelve la lista completa de permisos registrados en el sistema")
	@ApiResponse(responseCode = "200", description = "Lista de permisos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Permiso.class))))
	public List<Permiso> getAll() {
		return service.getAll();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Obtiene un permiso por ID", description = "Devuelve la información de un permiso específico")
	@ApiResponse(responseCode = "200", description = "Permiso encontrado", content = @Content(schema = @Schema(implementation = Permiso.class)))
	@ApiResponse(responseCode = "404", description = "Permiso no encontrado")
	public Response getById(
			@Parameter(description = "Identificador único del permiso", required = true, example = "1") @PathParam("id") int id) {

		return Response.ok(service.getById(id)).build();
	}
}
