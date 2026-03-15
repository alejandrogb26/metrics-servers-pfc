package local.alejandrogb.metricsservers.utils;

/**
 * Clase utilitaria que centraliza todas las sentencias SQL utilizadas por la
 * aplicación para el acceso a la base de datos relacional.
 *
 * <p>
 * El objetivo de esta clase es mantener las consultas SQL organizadas y
 * reutilizables, evitando su dispersión por las distintas capas de acceso a
 * datos. Todas las consultas se definen como constantes
 * {@code public static final}.
 * </p>
 *
 * <p>
 * Las sentencias están agrupadas por entidad funcional del sistema:
 * </p>
 * <ul>
 * <li>Servidores</li>
 * <li>Servicios</li>
 * <li>Secciones</li>
 * <li>Usuarios</li>
 * <li>Grupos</li>
 * <li>Permisos</li>
 * <li>Ámbitos</li>
 * <li>Tokens de API</li>
 * <li>Autenticación</li>
 * </ul>
 *
 * <p>
 * Las consultas que incluyen parámetros utilizan el marcador {@code ?} para ser
 * empleadas junto con {@link java.sql.PreparedStatement}, lo que permite evitar
 * problemas de inyección SQL y mejorar el rendimiento mediante la reutilización
 * de sentencias preparadas.
 * </p>
 *
 * <p>
 * Esta clase no puede ser instanciada, ya que únicamente contiene constantes
 * estáticas.
 * </p>
 *
 * @author alejandrogb
 */
public final class SqlQueries {

	// ====================================================
	// SENTENCIAS SQL ---> SERVIDORES
	// ====================================================

	/**
	 * Obtiene toda la información de un servidor incluyendo su sección y los
	 * servicios asociados.
	 *
	 * <p>
	 * Utiliza {@code LEFT JOIN} para garantizar que el servidor se recupere incluso
	 * si no tiene sección o servicios asociados.
	 * </p>
	 */
	public static final String FIND_FULL_SERV_BY_ID = """
			SELECT s.*,
			       sec.id AS sec_id,
			       sec.nombre AS sec_nombre,
			       sec.descripcion AS sec_desc,
			       srv.id AS servicio_id,
			       srv.nombre AS servicio_nombre
			FROM servidores s
			LEFT JOIN secciones sec ON sec.id = s.seccionId
			LEFT JOIN servidores_servicios ss ON ss.servidorId = s.id
			LEFT JOIN servicios srv ON srv.id = ss.servicioId
			WHERE s.id = ?
			""";

	/**
	 * Recupera todos los servidores junto con su sección y servicios asociados.
	 */
	public static final String FIND_FULL_SERV = """
			SELECT s.*,
			       sec.id AS sec_id,
			       sec.nombre AS sec_nombre,
			       sec.descripcion AS sec_desc,
			       srv.id AS servicio_id,
			       srv.nombre AS servicio_nombre
			FROM servidores s
			LEFT JOIN secciones sec ON sec.id = s.seccionId
			LEFT JOIN servidores_servicios ss ON ss.servidorId = s.id
			LEFT JOIN servicios srv ON srv.id = ss.servicioId
			""";

	/**
	 * Obtiene el identificador interno del servidor a partir de su id.
	 */
	public static final String FIND_SERVERID_SERV = "SELECT serverId FROM servidores WHERE id = ?";

	// ====================================================
	// SENTENCIAS SQL ---> SERVICIOS
	// ====================================================

	/**
	 * Recupera un servicio por su identificador.
	 */
	public static final String FIND_FULL_SRV_BY_ID = "SELECT * FROM servicios WHERE id = ?";

	/**
	 * Recupera todos los servicios registrados en el sistema.
	 */
	public static final String FIND_FULL_SRV = "SELECT * FROM servicios";

	// ====================================================
	// SENTENCIAS SQL ---> SECCIONES
	// ====================================================

	/**
	 * Recupera una sección por su identificador.
	 */
	public static final String FIND_FULL_SECCN_BY_ID = "SELECT * FROM secciones WHERE id = ?";

	/**
	 * Recupera todas las secciones.
	 */
	public static final String FIND_FULL_SECCN = "SELECT * FROM secciones";

	// ====================================================
	// SENTENCIAS SQL ---> USUARIOS
	// ====================================================

	/**
	 * Recupera un usuario por su identificador.
	 */
	public static final String FIND_FULL_USER_BY_ID = "SELECT * FROM usuarios WHERE id = ?";

	/**
	 * Recupera todos los usuarios del sistema.
	 */
	public static final String FIND_FULL_USER = "SELECT * FROM usuarios";

	/**
	 * Busca usuarios cuyo nombre de usuario coincida con un patrón.
	 */
	public static final String FIND_USERNAME_USER = "SELECT username FROM usuarios WHERE username LIKE ?";

	// ====================================================
	// SENTENCIAS SQL ---> GRUPOS
	// ====================================================

	/**
	 * Recupera un grupo por su identificador.
	 */
	public static final String FIND_FULL_GROUP_BY_ID = "SELECT * FROM grupos WHERE id = ?";

	/**
	 * Recupera todos los grupos del sistema.
	 */
	public static final String FIND_FULL_GROUP = "SELECT * FROM grupos";

	/**
	 * Elimina todos los permisos globales asociados a un grupo.
	 */
	public static final String DELETE_PRM_GLOBAL_BY_GROUP = "DELETE FROM grupo_permiso_global WHERE grupoId = ?";

	/**
	 * Elimina todos los permisos de sección asociados a un grupo.
	 */
	public static final String DELETE_PRM_SECCN_BY_GROUP = "DELETE FROM grupo_seccion WHERE grupoId = ?";

	/**
	 * Inserta un permiso global para un grupo.
	 */
	public static final String INSERT_PRM_GLOBAL_BY_GROUP = "INSERT INTO grupo_permiso_global (grupoId, permisoId) VALUES (?, ?)";

	/**
	 * Inserta un permiso asociado a una sección para un grupo.
	 */
	public static final String INSERT_PRM_SECCN_BY_GROUP = "INSERT INTO grupo_seccion (grupoId, seccionId, permisoId) VALUES (?, ?, ?)";

	// ====================================================
	// SENTENCIAS SQL ---> TOKENS API
	// ====================================================

	/**
	 * Recupera todos los tokens de API asociados a un usuario.
	 */
	public static final String FIND_TOKENS_BY_USUARIOID = "SELECT * FROM api_tokens WHERE usuarioId = ?";

	// ====================================================
	// SENTENCIAS SQL ---> AUTHENTICATION
	// ====================================================

	/**
	 * Consulta utilizada durante el proceso de autenticación mediante token.
	 * Devuelve información del usuario y del grupo al que pertenece.
	 */
	public static final String FIND_USER_GROUP_AUTH = """
			SELECT u.username, u.nombre, u.apel1, u.apel2, u.active,
			             g.id as gid, g.nombre as gnombre, g.superadmin
			      FROM usuarios u
			      JOIN api_tokens t ON u.id = t.usuarioId
			      JOIN grupos g ON u.grupoId = g.id
			      WHERE t.token = ? AND t.active = 1
			""";

	/**
	 * Recupera los permisos globales de un grupo en formato clave
	 * {@code permiso_ambito}.
	 */
	public static final String FIND_PRM_GLOBAL_BY_GROUPID_NAME = """
			SELECT CONCAT(p.nombre, '_', a.nombre) as pkey
			FROM grupo_permiso_global gpg
			   JOIN permisos p ON gpg.permisoId = p.id
			   JOIN ambitos a ON p.ambitoId = a.id
			WHERE gpg.grupoId = ?
			""";

	/**
	 * Recupera los permisos asociados a secciones específicas para un grupo.
	 */
	public static final String FIND_PRM_SECCN_BY_GROUPID_NAME = """
			SELECT gs.seccionId,
			       CONCAT(p.nombre, '_', a.nombre) as pkey
			FROM grupo_seccion gs
			JOIN permisos p ON gs.permisoId = p.id
			JOIN ambitos a ON p.ambitoId = a.id
			WHERE gs.grupoId = ?
			""";

	/**
	 * Recupera los identificadores de permisos globales de un grupo.
	 */
	public static final String FIND_PRM_GLOBAL_BY_GROUPID_ID = "SELECT permisoId FROM grupo_permiso_global WHERE grupoId = ?";

	/**
	 * Recupera los identificadores de permisos asociados a secciones.
	 */
	public static final String FIND_PRM_SECCN_BY_GROUPID_ID = "SELECT seccionId, permisoId FROM grupo_seccion WHERE grupoId = ?";

	// ====================================================
	// SENTENCIAS SQL ---> PERMISOS
	// ====================================================

	/**
	 * Recupera un permiso junto con su ámbito asociado.
	 */
	public static final String FIND_FULL_PRM_BY_ID = """
			SELECT p.id as p_id, p.nombre as p_nombre, p.descripcion as p_descripcion,
					a.id as a_id, a.nombre as a_nombre, a.descripcion as a_descripcion
			FROM permisos p
			JOIN ambitos a ON p.ambitoId = a.id
			WHERE p.id = ?
			""";

	/**
	 * Recupera todos los permisos junto con su ámbito asociado.
	 */
	public static final String FIND_FULL_PRM = """
			SELECT p.id as p_id, p.nombre as p_nombre, p.descripcion as p_descripcion,
					a.id as a_id, a.nombre as a_nombre, a.descripcion as a_descripcion
			FROM permisos p
			JOIN ambitos a ON p.ambitoId = a.id
			ORDER BY a.nombre, p.nombre
			""";

	/**
	 * Obtiene el identificador de un permiso a partir de su nombre y ámbito.
	 */
	public static final String FIND_PRMID_BY_KEY = """
			SELECT p.id FROM permisos p
			JOIN ambitos a ON p.ambitoId = a.id
			WHERE p.nombre = ? AND a.nombre = ?
			""";

	// ====================================================
	// SENTENCIAS SQL ---> AMBITOS
	// ====================================================

	/**
	 * Recupera un ámbito por su identificador.
	 */
	public static final String FIND_FULL_AMBT_BY_ID = "SELECT * FROM ambitos WHERE id = ?";

	/**
	 * Recupera todos los ámbitos del sistema.
	 */
	public static final String FIND_FULL_AMBT = "SELECT * FROM ambitos";

	/**
	 * Constructor privado para evitar la instanciación de la clase.
	 */
	private SqlQueries() {
	}
}