package local.alejandrogb.metricsservers.api;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(title = "Metrics Servers API", version = "1.0", description = "API para gestión y consulta de métricas de servidores"), security = {
		@SecurityRequirement(name = "bearerAuth") })

@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class RestConfig extends ResourceConfig {

	public RestConfig() {

		// Escanea los resources
		packages("local.alejandrogb.metricsservers.api.resources");

		// Multipart
		register(MultiPartFeature.class);

		// JSON
		register(JacksonFeature.class);

		// Swagger
		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
	}
}