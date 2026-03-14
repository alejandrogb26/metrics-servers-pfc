package local.alejandrogb.metricsservers.api;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class RestConfig extends ResourceConfig {
	public RestConfig() {
		// Escanea tus recursos
		packages("local.alejandrogb.metrics_servers.api.resources");

		// FORZAR el registro del soporte de Multipart
		register(MultiPartFeature.class);

		// Registra soporte para JSON (Jackson)
		register(JacksonFeature.class);
	}
}
