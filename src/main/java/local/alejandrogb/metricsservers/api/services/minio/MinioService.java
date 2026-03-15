package local.alejandrogb.metricsservers.api.services.minio;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import local.alejandrogb.metricsservers.utils.MinioConfig;

/**
 * Servicio encargado de gestionar el acceso a archivos almacenados en el
 * sistema de almacenamiento de objetos MinIO.
 *
 * <p>
 * Este servicio proporciona utilidades para:
 * </p>
 *
 * <ul>
 * <li>Generar URLs de acceso a archivos almacenados en MinIO.</li>
 * <li>Subir archivos a los buckets configurados.</li>
 * </ul>
 *
 * <p>
 * Dependiendo del bucket utilizado, el acceso a los archivos puede ser:
 * </p>
 *
 * <ul>
 * <li><b>Público</b>: acceso directo mediante URL.</li>
 * <li><b>Privado</b>: acceso mediante URL firmada con tiempo de
 * expiración.</li>
 * </ul>
 *
 * <p>
 * Este diseño permite proteger archivos sensibles (como imágenes de usuarios o
 * servidores) mientras que otros recursos, como iconos de servicios, pueden
 * mantenerse públicos.
 * </p>
 */
public class MinioService {

	/**
	 * Bucket utilizado para almacenar imágenes asociadas a usuarios.
	 */
	public static final String BUCKET_USUARIOS = "usuarios";

	/**
	 * Bucket utilizado para almacenar imágenes asociadas a servidores.
	 */
	public static final String BUCKET_SERVIDORES = "servidores";

	/**
	 * Bucket utilizado para almacenar imágenes asociadas a servicios.
	 * <p>
	 * Este bucket es público, por lo que las imágenes pueden accederse directamente
	 * mediante URL sin necesidad de firma.
	 * </p>
	 */
	public static final String BUCKET_SERVICIOS = "servicios";

	/**
	 * Genera una URL de acceso para una imagen almacenada en MinIO.
	 *
	 * <p>
	 * El comportamiento depende del bucket utilizado:
	 * </p>
	 *
	 * <ul>
	 * <li>Si el nombre del archivo es nulo o vacío, se devuelve una imagen por
	 * defecto.</li>
	 * <li>Si el bucket corresponde a {@link #BUCKET_SERVICIOS}, se devuelve una URL
	 * pública directa.</li>
	 * <li>Para otros buckets (usuarios y servidores), se genera una URL firmada con
	 * tiempo de expiración.</li>
	 * </ul>
	 *
	 * <p>
	 * Las URLs firmadas permiten acceder temporalmente a archivos privados sin
	 * exponer credenciales de acceso al sistema de almacenamiento.
	 * </p>
	 *
	 * @param bucket   nombre del bucket donde se encuentra el archivo
	 * @param fileName nombre del archivo almacenado
	 * @return URL de acceso al archivo o {@code null} si ocurre un error
	 */
	public String getUrlImagen(String bucket, String fileName) {

		// 1. Si no hay nombre, avatar por defecto
		if (fileName == null || fileName.isEmpty()) {
			return String.format("%s/estaticos/no-disponible.png", MinioConfig.ENDPOINT);
		}

		// 2. Bucket público
		if (BUCKET_SERVICIOS.equals(bucket)) {
			return String.format("%s/%s/%s", MinioConfig.ENDPOINT, BUCKET_SERVICIOS, fileName);
		}

		// 3. Bucket privado → URL firmada
		try {
			return MinioConfig.getClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET)
					.bucket(bucket).object(fileName).expiry(10, TimeUnit.MINUTES).build());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Sube un archivo a un bucket de MinIO.
	 *
	 * <p>
	 * El archivo se envía utilizando un {@link InputStream} y se almacena en el
	 * bucket especificado con el nombre indicado.
	 * </p>
	 *
	 * <p>
	 * La subida se realiza en streaming y admite archivos de tamaño desconocido. En
	 * este caso se utiliza un tamaño de fragmento máximo de 10 MB para el envío de
	 * datos.
	 * </p>
	 *
	 * @param bucket nombre del bucket de destino
	 * @param nombre nombre con el que se almacenará el archivo
	 * @param stream flujo de datos del archivo a subir
	 * @throws Exception si ocurre un error durante la subida
	 */
	public void uploadArchivo(String bucket, String nombre, InputStream stream) throws Exception {

		MinioConfig.getClient()
				.putObject(PutObjectArgs.builder().bucket(bucket).object(nombre).stream(stream, -1, 10485760)
						// -1 → tamaño desconocido
						// 10 MB → tamaño máximo por fragmento
						.contentType("application/octet-stream").build());
	}
}