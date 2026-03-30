package local.alejandrogb.metricsservers.utils;

import io.minio.MinioClient;

/**
 * Clase de configuración y acceso al cliente de MinIO utilizado por la
 * aplicación.
 *
 * <p>
 * Esta clase proporciona un punto centralizado para la creación y reutilización
 * de una instancia de {@link MinioClient}. El cliente se inicializa mediante el
 * patrón <b>Lazy Initialization</b>, creándose únicamente en la primera llamada
 * al método {@link #getClient()}.
 * </p>
 *
 * <p>
 * MinIO se utiliza como sistema de almacenamiento de objetos compatible con la
 * API de Amazon S3. La configuración incluye el endpoint del servidor y las
 * credenciales de acceso necesarias para autenticarse.
 * </p>
 *
 * <p>
 * Ejemplo de uso:
 * </p>
 *
 * <pre>
 * MinioClient client = MinioConfig.getClient();
 * </pre>
 *
 * <p>
 * El cliente devuelto puede utilizarse para realizar operaciones como: subida
 * de objetos, descarga, gestión de buckets o eliminación de archivos.
 * </p>
 *
 * <p>
 * Nota: en entornos de producción reales, las credenciales deberían almacenarse
 * en variables de entorno o en un gestor de configuración seguro en lugar de
 * definirse directamente en el código.
 * </p>
 *
 * @author alejandrogb
 */
public class MinioConfig {

	/**
	 * Instancia única del cliente MinIO utilizada por la aplicación.
	 */
	private static MinioClient minioClient;

	/**
	 * Endpoint del servidor MinIO.
	 */
	public static final String ENDPOINT = EnvConfig.MINIO_ENDPOINT;

	/**
	 * Clave de acceso utilizada para autenticarse en el servidor MinIO.
	 */
	private static final String ACCESS_KEY = EnvConfig.MINIO_ACCESS_KEY;

	/**
	 * Clave secreta asociada al usuario de acceso en MinIO.
	 */
	private static final String SECRET_KEY = EnvConfig.MINIO_SECRET_KEY;

	/**
	 * Devuelve la instancia del cliente {@link MinioClient}.
	 *
	 * <p>
	 * Si el cliente aún no ha sido creado, se inicializa utilizando el endpoint y
	 * las credenciales configuradas en la clase.
	 * </p>
	 *
	 * @return instancia de {@link MinioClient} lista para realizar operaciones
	 *         sobre el servidor MinIO
	 */
	public static MinioClient getClient() {
		if (minioClient == null)
			minioClient = MinioClient.builder().endpoint(ENDPOINT).credentials(ACCESS_KEY, SECRET_KEY).build();

		return minioClient;
	}
}