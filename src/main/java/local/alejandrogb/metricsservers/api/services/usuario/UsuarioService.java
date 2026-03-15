package local.alejandrogb.metricsservers.api.services.usuario;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ValidationException;
import local.alejandrogb.metricsservers.api.services.minio.MinioService;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.usuario.Usuario;
import local.alejandrogb.metricsservers.utils.BulkResult;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los usuarios
 * del sistema.
 *
 * <p>
 * Esta clase actúa como capa intermedia entre los recursos REST y la capa de
 * persistencia representada por {@link DaoApi}. Además integra el acceso a
 * imágenes almacenadas en MinIO mediante {@link MinioService}.
 * </p>
 *
 * <p>
 * Sus responsabilidades incluyen:
 * </p>
 *
 * <ul>
 * <li>Recuperar usuarios registrados.</li>
 * <li>Generar nombres de usuario únicos.</li>
 * <li>Crear nuevos usuarios.</li>
 * <li>Actualizar parcialmente la información de usuarios.</li>
 * <li>Eliminar usuarios existentes.</li>
 * <li>Gestionar imágenes de perfil almacenadas en MinIO.</li>
 * </ul>
 */
public class UsuarioService {

	/**
	 * DAO principal utilizado para acceder a la base de datos.
	 */
	private final DaoApi dao = DaoApi.getInstance();

	/**
	 * Servicio encargado de gestionar archivos en MinIO.
	 */
	private final MinioService minioService = new MinioService();

	/**
	 * Recupera un usuario por su identificador.
	 *
	 * <p>
	 * Si el usuario tiene una imagen de perfil asociada, se genera la URL de acceso
	 * correspondiente mediante {@link MinioService}.
	 * </p>
	 *
	 * @param id identificador del usuario
	 * @return usuario encontrado o {@code null} si no existe
	 */
	public Usuario getUsuario(int id) {

		Usuario u = dao.findUsuarioById(id);

		if (u != null) {
			u.setUrlFoto(minioService.getUrlImagen(MinioService.BUCKET_USUARIOS, u.getFotoPerfil()));
		}

		return u;
	}

	/**
	 * Recupera todos los usuarios registrados en el sistema.
	 *
	 * <p>
	 * Para cada usuario se genera la URL de acceso a su imagen de perfil almacenada
	 * en MinIO.
	 * </p>
	 *
	 * @return lista de usuarios
	 */
	public List<Usuario> getAll() {

		List<Usuario> lista = dao.findAllUsuarios();

		lista.forEach(u -> u.setUrlFoto(minioService.getUrlImagen(MinioService.BUCKET_USUARIOS, u.getFotoPerfil())));

		return lista;
	}

	/**
	 * Crea múltiples usuarios en el sistema.
	 *
	 * <p>
	 * Durante la creación se aplica la regla de negocio de generación automática de
	 * nombre de usuario único y se activa el usuario por defecto.
	 * </p>
	 *
	 * @param usuarios lista de usuarios a crear
	 * @return resultado agregado de la operación masiva
	 */
	public BulkResult createUsuarios(List<Usuario> usuarios) {

		BulkResult result = new BulkResult();

		if (usuarios == null || usuarios.isEmpty())
			return result;

		result.setTotal(usuarios.size());

		for (Usuario u : usuarios) {

			try {

				u.setUsername(generateUniqueUsername(u.getNombre(), u.getApel1()));

				u.setActive(true);

				dao.insertSingleUsuario(u);

				result.incrementOk();

			} catch (Exception e) {

				result.incrementFailed();
				result.addError("Error al crear usuario [" + u.getNombre() + "]: " + e.getMessage());
			}
		}

		return result;
	}

	/**
	 * Actualiza parcialmente la información de un usuario.
	 *
	 * <p>
	 * Solo se permiten modificar ciertos campos para preservar la integridad del
	 * modelo de datos.
	 * </p>
	 *
	 * <p>
	 * Si se modifica el nombre o el primer apellido del usuario, el sistema
	 * recalcula automáticamente el nombre de usuario garantizando que siga siendo
	 * único.
	 * </p>
	 *
	 * @param id identificador del usuario
	 * @param u  objeto con los campos a actualizar
	 * @return {@code true} si el usuario fue actualizado correctamente
	 */
	public boolean patchUsuario(int id, Usuario u) {

		Usuario existente = dao.findUsuarioById(id);

		if (existente == null)
			throw new ValidationException("El usuario no existe");

		Map<String, Object> fields = new LinkedHashMap<>();

		if (u.getNombre() != null)
			fields.put(Usuario.COL_NOMBRE, u.getNombre());

		if (u.getApel1() != null)
			fields.put(Usuario.COL_APEL1, u.getApel1());

		if (u.getApel2() != null)
			fields.put(Usuario.COL_APEL2, u.getApel2());

		if (u.getGrupoId() != 0)
			fields.put(Usuario.COL_GRUPO_ID, u.getGrupoId());

		if (u.isActive() != null)
			fields.put(Usuario.COL_ACTIVE, u.isActive());

		String nombreFinal = (u.getNombre() != null) ? u.getNombre() : existente.getNombre();

		String apel1Final = (u.getApel1() != null) ? u.getApel1() : existente.getApel1();

		if (!nombreFinal.equals(existente.getNombre()) || !apel1Final.equals(existente.getApel1())) {

			fields.put(Usuario.COL_USERNAME, generateUniqueUsername(nombreFinal, apel1Final));
		}

		if (fields.isEmpty())
			return true;

		return dao.updateUsuario(id, fields);
	}

	/**
	 * Elimina múltiples usuarios del sistema.
	 *
	 * @param ids lista de identificadores de usuario
	 * @return resultado agregado de la operación masiva
	 */
	public BulkResult deleteUsuarios(List<Integer> ids) {

		BulkResult result = new BulkResult();

		if (ids == null || ids.isEmpty())
			return result;

		result.setTotal(ids.size());

		for (Integer id : ids) {

			try {

				boolean deleted = dao.deleteSingleUsuario(id);

				if (deleted) {
					result.incrementOk();
				} else {
					result.incrementFailed();
					result.addError("El usuario con ID " + id + " no existe.");
				}

			} catch (Exception e) {

				result.incrementFailed();
				result.addError("Error al eliminar ID " + id + ": " + e.getMessage());
			}
		}

		return result;
	}

	/**
	 * Actualiza la imagen de perfil de un usuario.
	 *
	 * <p>
	 * El proceso consiste en subir la imagen a MinIO y posteriormente actualizar la
	 * referencia al archivo en la base de datos.
	 * </p>
	 *
	 * @param userId      identificador del usuario
	 * @param stream      flujo de datos del archivo
	 * @param nuevoNombre nombre del archivo almacenado
	 * @throws Exception si ocurre un error durante el proceso
	 */
	public void actualizarFotoPerfil(int userId, InputStream stream, String nuevoNombre) throws Exception {

		minioService.uploadArchivo(MinioService.BUCKET_USUARIOS, nuevoNombre, stream);

		Map<String, Object> campos = new HashMap<>();
		campos.put(Usuario.COL_FOTO_PERFIL, nuevoNombre);

		boolean ok = dao.updateUsuario(userId, campos);

		if (!ok)
			throw new Exception("No se pudo actualizar la ruta en la base de datos");
	}

	/**
	 * Genera un nombre de usuario único a partir del nombre y primer apellido.
	 *
	 * <p>
	 * El formato utilizado es:
	 * </p>
	 *
	 * <pre>
	 * primera_letra_nombre + primer_apellido + número_opcional
	 * </pre>
	 *
	 * <p>
	 * Ejemplo:
	 * </p>
	 *
	 * <pre>
	 * Ana Gómez → agomez
	 * Si ya existe → agomez1, agomez2...
	 * </pre>
	 *
	 * <p>
	 * Durante el proceso se eliminan tildes, caracteres especiales y espacios para
	 * garantizar compatibilidad con sistemas externos.
	 * </p>
	 *
	 * @param nombre nombre del usuario
	 * @param apel1  primer apellido del usuario
	 * @return nombre de usuario único
	 */
	private String generateUniqueUsername(String nombre, String apel1) {

		if (nombre == null || apel1 == null || nombre.isBlank() || apel1.isBlank()) {

			throw new ValidationException("Nombre y Apellido son obligatorios");
		}

		String base = (nombre.trim().substring(0, 1) + apel1.trim()).toLowerCase();

		base = Normalizer.normalize(base, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "");

		List<String> existentes = dao.findUsernamesLike(base);

		if (!existentes.contains(base))
			return base;

		int contador = 1;

		while (existentes.contains(base + contador)) {
			contador++;
		}

		return base + contador;
	}
}