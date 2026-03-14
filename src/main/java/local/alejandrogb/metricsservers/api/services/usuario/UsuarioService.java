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

public class UsuarioService {
	private final DaoApi dao = DaoApi.getInstance();
	private final MinioService minioService = new MinioService();

	public Usuario getUsuario(int id) {
		Usuario u = dao.findUsuarioById(id);
		if (u != null) {
			// Llenamos el campo transitorio urlFoto
			u.setUrlFoto(minioService.getUrlImagen(MinioService.BUCKET_USUARIOS, u.getFotoPerfil()));
		}
		return u;
	}

	public List<Usuario> getAll() {
		List<Usuario> lista = dao.findAllUsuarios();
		// Procesamos cada usuario para ponerle su URL firmada
		lista.forEach(u -> u.setUrlFoto(minioService.getUrlImagen(MinioService.BUCKET_USUARIOS, u.getFotoPerfil())));
		return lista;
	}

	public BulkResult createUsuarios(List<Usuario> usuarios) {
		BulkResult result = new BulkResult();
		if (usuarios == null || usuarios.isEmpty())
			return result;

		result.setTotal(usuarios.size());

		for (Usuario u : usuarios) {
			try {
				// Regla de negocio
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

	public boolean patchUsuario(int id, Usuario u) {
		Usuario existente = dao.findUsuarioById(id);
		if (existente == null)
			throw new ValidationException("El usuario no existe");

		// Preparamos el mapa para el DAO basándonos en lo que el cliente envió
		Map<String, Object> fields = new LinkedHashMap<>();

		// 1. Campos permitidos (solo si vienen en el JSON/no son null)
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

		// 2. PROTECCIÓN: Ignoramos explícitamente username y fotos del objeto 'u'
		// No los añadimos al mapa 'fields', así el DAO no los toca.

		// 3. Lógica de negocio para el Username
		String nombreFinal = (u.getNombre() != null) ? u.getNombre() : existente.getNombre();
		String apel1Final = (u.getApel1() != null) ? u.getApel1() : existente.getApel1();

		if (!nombreFinal.equals(existente.getNombre()) || !apel1Final.equals(existente.getApel1())) {
			fields.put(Usuario.COL_USERNAME, generateUniqueUsername(nombreFinal, apel1Final));
		}

		if (fields.isEmpty())
			return true; // Nada que cambiar

		return dao.updateUsuario(id, fields);
	}

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

	public void actualizarFotoPerfil(int userId, InputStream stream, String nuevoNombre) throws Exception {
		// A. Subir el chorro de bytes a MinIO
		// Usamos el bucket 'usuarios' y el nombre que generamos
		minioService.uploadArchivo(MinioService.BUCKET_USUARIOS, nuevoNombre, stream);

		// B. Guardar el nombre en la BD
		Map<String, Object> campos = new HashMap<>();
		campos.put(Usuario.COL_FOTO_PERFIL, nuevoNombre);

		boolean ok = dao.updateUsuario(userId, campos);

		if (!ok)
			throw new Exception("No se pudo actualizar la ruta en la base de datos");
	}

	/**
	 * Lógica de generación de Username: 1ª letra nombre + primer apellido +
	 * correlativo
	 */
	private String generateUniqueUsername(String nombre, String apel1) {
		if (nombre == null || apel1 == null || nombre.isBlank() || apel1.isBlank()) {
			throw new ValidationException("Nombre y Apellido son obligatorios");
		}

		// Normalizar: agomez
		String base = (nombre.trim().substring(0, 1) + apel1.trim()).toLowerCase();
		base = Normalizer.normalize(base, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "") // Quita tildes/eñes
				.replaceAll("\\s+", ""); // Quita espacios

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
