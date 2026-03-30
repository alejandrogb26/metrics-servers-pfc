package local.alejandrogb.metricsservers.api.services.usuario;

import java.io.InputStream;

import local.alejandrogb.metricsservers.api.services.minio.MinioService;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.usuario.UsuarioApp;

/**
 * Servicio encargado de las operaciones sobre el perfil local del usuario.
 *
 * <p>
 * Con el flujo AD, la gestión de usuarios se reduce a una única
 * responsabilidad: actualizar la foto de perfil almacenada en MinIO y reflejar
 * el cambio en {@code usuarios_app}.
 * </p>
 */
public class UsuarioService {

	private final DaoApi dao = DaoApi.getInstance();
	private final MinioService minioService = new MinioService();

	/**
	 * Sube una nueva foto de perfil a MinIO y actualiza la referencia en
	 * {@code usuarios_app}.
	 *
	 * @param username    sAMAccountName del usuario (obtenido del JWT)
	 * @param stream      flujo de datos del archivo
	 * @param nuevoNombre nombre del archivo en MinIO
	 * @throws Exception si falla la subida o la actualización en BD
	 */
	public void actualizarFotoPerfil(String username, InputStream stream, String nuevoNombre) throws Exception {

		// 1. Subir archivo a MinIO
		minioService.uploadArchivo(MinioService.BUCKET_USUARIOS, nuevoNombre, stream);

		// 2. Buscar el registro en usuarios_app
		UsuarioApp usuarioApp = dao.getUsuarioAppByUsername(username);

		if (usuarioApp == null) {
			throw new Exception("Usuario no encontrado en usuarios_app: " + username);
		}

		// 3. Actualizar la referencia a la foto
		boolean ok = dao.updateUsuarioAppFoto(usuarioApp.getId(), nuevoNombre);

		if (!ok) {
			throw new Exception("No se pudo actualizar la foto en la base de datos");
		}
	}
}