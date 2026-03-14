package local.alejandrogb.metricsservers.utils;

public class SqlQueries {
	// ====================================================
	// SENTENCIAS SQL ---> SERVIDORES
	// ====================================================
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

	public static final String FIND_SERVERID_SERV = "SELECT serverId FROM servidores WHERE id = ?";

	// ====================================================
	// SENTENCIAS SQL ---> SERVICIOS
	// ====================================================
	public static final String FIND_FULL_SRV_BY_ID = "SELECT * FROM servicios WHERE id = ?";
	public static final String FIND_FULL_SRV = "SELECT * FROM servicios";

	// ====================================================
	// SENTENCIAS SQL ---> SECCIONES
	// ====================================================
	public static final String FIND_FULL_SECCN_BY_ID = "SELECT * FROM secciones WHERE id = ?";
	public static final String FIND_FULL_SECCN = "SELECT * FROM secciones";

	// ====================================================
	// SENTENCIAS SQL ---> USUARIOS
	// ====================================================
	public static final String FIND_FULL_USER_BY_ID = "SELECT * FROM usuarios WHERE id = ?";
	public static final String FIND_FULL_USER = "SELECT * FROM usuarios";
	public static final String FIND_USERNAME_USER = "SELECT username FROM usuarios WHERE username LIKE ?";

	// ====================================================
	// SENTENCIAS SQL ---> GRUPOS
	// ====================================================
	public static final String FIND_FULL_GROUP_BY_ID = "SELECT * FROM grupos WHERE id = ?";
	public static final String FIND_FULL_GROUP = "SELECT * FROM grupos";

	public static final String DELETE_PRM_GLOBAL_BY_GROUP = "DELETE FROM grupo_permiso_global WHERE grupoId = ?";
	public static final String DELETE_PRM_SECCN_BY_GROUP = "DELETE FROM grupo_seccion WHERE grupoId = ?";

	public static final String INSERT_PRM_GLOBAL_BY_GROUP = "INSERT INTO grupo_permiso_global (grupoId, permisoId) VALUES (?, ?)";
	public static final String INSERT_PRM_SECCN_BY_GROUP = "INSERT INTO grupo_seccion (grupoId, seccionId, permisoId) VALUES (?, ?, ?)";

	// ====================================================
	// SENTENCIAS SQL ---> TOKENS API
	// ====================================================
	public static final String FIND_TOKENS_BY_USUARIOID = "SELECT * FROM api_tokens WHERE usuarioId = ?";

	// ====================================================
	// SENTENCIAS SQL ---> AUTHENTICATION
	// ====================================================
	public static final String FIND_USER_GROUP_AUTH = """
			SELECT u.username, u.nombre, u.apel1, u.apel2, u.active,
			             g.id as gid, g.nombre as gnombre, g.superadmin
			      FROM usuarios u
			      JOIN api_tokens t ON u.id = t.usuarioId
			      JOIN grupos g ON u.grupoId = g.id
			      WHERE t.token = ? AND t.active = 1
			""";

	public static final String FIND_PRM_GLOBAL_BY_GROUPID_NAME = """
			SELECT CONCAT(p.nombre, '_', a.nombre) as pkey
			FROM grupo_permiso_global gpg
			   JOIN permisos p ON gpg.permisoId = p.id
			   JOIN ambitos a ON p.ambitoId = a.id
			WHERE gpg.grupoId = ?
			""";

	public static final String FIND_PRM_SECCN_BY_GROUPID_NAME = """
			SELECT gs.seccionId,
			       CONCAT(p.nombre, '_', a.nombre) as pkey
			FROM grupo_seccion gs
			JOIN permisos p ON gs.permisoId = p.id
			JOIN ambitos a ON p.ambitoId = a.id
			WHERE gs.grupoId = ?
			""";

	public static final String FIND_PRM_GLOBAL_BY_GROUPID_ID = "SELECT permisoId FROM grupo_permiso_global WHERE grupoId = ?";
	public static final String FIND_PRM_SECCN_BY_GROUPID_ID = "SELECT seccionId, permisoId FROM grupo_seccion WHERE grupoId = ?";

	// ====================================================
	// SENTENCIAS SQL ---> PERMISOS
	// ====================================================
	public static final String FIND_FULL_PRM_BY_ID = """
			SELECT p.id as p_id, p.nombre as p_nombre, p.descripcion as p_descripcion,
					a.id as a_id, a.nombre as a_nombre, a.descripcion as a_descripcion
			FROM permisos p
			JOIN ambitos a ON p.ambitoId = a.id
			WHERE p.id = ?
			""";
	public static final String FIND_FULL_PRM = """
			SELECT p.id as p_id, p.nombre as p_nombre, p.descripcion as p_descripcion,
					a.id as a_id, a.nombre as a_nombre, a.descripcion as a_descripcion
			FROM permisos p
			JOIN ambitos a ON p.ambitoId = a.id
			ORDER BY a.nombre, p.nombre
			""";

	public static final String FIND_PRMID_BY_KEY = """
			SELECT p.id FROM permisos p
			JOIN ambitos a ON p.ambitoId = a.id
			WHERE p.nombre = ? AND a.nombre = ?
			""";

	// ====================================================
	// SENTENCIAS SQL ---> AMBITOS
	// ====================================================
	public static final String FIND_FULL_AMBT_BY_ID = "SELECT * FROM ambitos WHERE id = ?";
	public static final String FIND_FULL_AMBT = "SELECT * FROM ambitos";

	private SqlQueries() {
	}

}
