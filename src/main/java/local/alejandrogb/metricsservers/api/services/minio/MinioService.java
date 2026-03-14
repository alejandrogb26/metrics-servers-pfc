package local.alejandrogb.metricsservers.api.services.minio;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import local.alejandrogb.metricsservers.utils.MinioConfig;

public class MinioService {
	public static final String BUCKET_USUARIOS = "usuarios";
	public static final String BUCKET_SERVIDORES = "servidores";
	public static final String BUCKET_SERVICIOS = "servicios";

	public String getUrlImagen(String bucket, String fileName) {
		// 1. Si no hay nombre, avatar por defecto (Público en bucket 'estaticos')
		if (fileName == null || fileName.isEmpty()) {
			return String.format("%s/estaticos/no-disponible.png", MinioConfig.ENDPOINT);
		}

		// 2. Si el bucket es 'servicios', es público (URL directa sin firma)
		if (BUCKET_SERVICIOS.equals(bucket)) {
			return String.format("%s/%s/%s", MinioConfig.ENDPOINT, BUCKET_SERVICIOS, fileName);
		}

		// 3. Para usuarios y servidores: Generar URL firmada (Privado)
		try {
			return MinioConfig.getClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET)
					.bucket(bucket).object(fileName).expiry(10, TimeUnit.MINUTES).build());
		} catch (Exception e) {
			return null;
		}
	}

	public void uploadArchivo(String bucket, String nombre, InputStream stream) throws Exception {
		MinioConfig.getClient()
				.putObject(PutObjectArgs.builder().bucket(bucket).object(nombre).stream(stream, -1, 10485760) // -1
																												// porque
																												// no
																												// sabemos
																												// el
																												// tamaño
																												// exacto,
																												// 10MB
																												// por
																												// parte
						.contentType("application/octet-stream") // O detectarlo por la extensión
						.build());
	}
}
