package local.alejandrogb.metricsservers.api;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import local.alejandrogb.metricsservers.api.resources.ambito.AmbitoResource;
import local.alejandrogb.metricsservers.api.resources.auth.AuthResource;
import local.alejandrogb.metricsservers.api.resources.grupo.GrupoResource;
import local.alejandrogb.metricsservers.api.resources.permisos.PermisoResource;
import local.alejandrogb.metricsservers.api.resources.seccion.SeccionResource;
import local.alejandrogb.metricsservers.api.resources.servidor.ServidorResource;
import local.alejandrogb.metricsservers.api.resources.servicio.ServicioResource;
import local.alejandrogb.metricsservers.api.resources.tests.HealthResource;
import local.alejandrogb.metricsservers.api.resources.usuario.UsuarioResource;
import local.alejandrogb.metricsservers.api.tokens.TokenFilter;
import local.alejandrogb.metricsservers.exceptions.mappers.DaoMapper;
import local.alejandrogb.metricsservers.exceptions.mappers.NotFoundMapper;
import local.alejandrogb.metricsservers.exceptions.mappers.ProbeMapper;
import local.alejandrogb.metricsservers.exceptions.mappers.ValidationMapper;

@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(title = "Metrics Servers API", version = "1.0", description = "API para gestión y consulta de métricas de servidores"), security = {
		@SecurityRequirement(name = "bearerAuth") })
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class RestConfig extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();

		// Resources
		classes.add(AuthResource.class);
		classes.add(ServidorResource.class);
		classes.add(GrupoResource.class);
		classes.add(PermisoResource.class);
		classes.add(SeccionResource.class);
		classes.add(UsuarioResource.class);
		classes.add(AmbitoResource.class);
		classes.add(HealthResource.class);
		classes.add(ServicioResource.class);

		// Filtros y mappers — deben registrarse explícitamente al usar Application
		classes.add(TokenFilter.class);
		classes.add(DaoMapper.class);
		classes.add(NotFoundMapper.class);
		classes.add(ProbeMapper.class);
		classes.add(ValidationMapper.class);

		// Swagger/OpenAPI
		classes.add(OpenApiResource.class);
		classes.add(AcceptHeaderOpenApiResource.class);

		return classes;
	}
}
