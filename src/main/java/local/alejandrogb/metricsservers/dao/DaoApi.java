package local.alejandrogb.metricsservers.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import jakarta.validation.ValidationException;
import local.alejandrogb.metricsservers.exceptions.DaoException;
import local.alejandrogb.metricsservers.models.Ambito;
import local.alejandrogb.metricsservers.models.Grupo;
import local.alejandrogb.metricsservers.models.Permiso;
import local.alejandrogb.metricsservers.models.PermissionMap;
import local.alejandrogb.metricsservers.models.Seccion;
import local.alejandrogb.metricsservers.models.Servicio;
import local.alejandrogb.metricsservers.models.Session;
import local.alejandrogb.metricsservers.models.servidor.Servidor;
import local.alejandrogb.metricsservers.models.usuario.ApiToken;
import local.alejandrogb.metricsservers.models.usuario.Usuario;
import local.alejandrogb.metricsservers.utils.GetDataSource;
import local.alejandrogb.metricsservers.utils.SqlQueries;
import local.alejandrogb.metricsservers.utils.interfaces.SQLConsumer;
import local.alejandrogb.metricsservers.utils.interfaces.SQLFunction;

public class DaoApi {
	private static final DaoApi INSTANCE = new DaoApi();
	private final MongoDao mongoDao = new MongoDao();

	private DaoApi() {
	}

	public static DaoApi getInstance() {
		return INSTANCE;
	}

	private DataSource getDS() {
		return GetDataSource.getDataSource();
	}

	// ==========================================
	// MÉTODOS SERVIDOR
	// ==========================================
	public Servidor findServidorById(int id) {
		try {
			return findAll(SqlQueries.FIND_FULL_SERV_BY_ID, ps -> ps.setInt(1, id),
					rs -> Servidor.extractServidores(rs).values().stream().findFirst().orElse(null));
		} catch (SQLException e) {
			throw new DaoException("Find Server BD falló", e);
		}
	}

	public List<Servidor> findAllServidor() {
		try {
			return query(SqlQueries.FIND_FULL_SERV, rs -> new ArrayList<>(Servidor.extractServidores(rs).values()));
		} catch (SQLException e) {
			throw new DaoException("FindAll Servers BD falló", e);
		}
	}

	public String findServerIdById(int id) {
		try {
			return findOne(SqlQueries.FIND_SERVERID_SERV, ps -> ps.setInt(1, id), rs -> rs.getString("serverId"));
		} catch (SQLException e) {
			throw new DaoException("Error obteniendo serverId", e);
		}
	}

	public int insertServidor(Servidor servidor) {

		if (servidor == null)
			return -1;

		Connection conn = null;
		boolean oldAutoCommit = true;

		try {
			conn = getDS().getConnection();

			oldAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			int idServidor = insert(conn, Servidor.TABLE, servidor.toMap());

			if (idServidor <= 0)
				throw new SQLException("ID inválido generado para Servidor");

			if (servidor.getServicios() != null && !servidor.getServicios().isEmpty()) {
				insertBatch(conn, Servicio.TABLE_RELATION, servidor.getServicios().stream().map(srv -> {
					Map<String, Object> m = new LinkedHashMap<>();
					m.put("servidorId", idServidor);
					m.put("servicioId", srv);
					return m;
				}).toList());
			}

			conn.commit();
			return idServidor;

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
			}
			throw new DaoException("Insert DB falló", e);
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException ignored) {
				}
			}
		}
	}

	public boolean updateServidor(int id, Map<String, Object> fields) {
		try {
			// Mapear claves JSON --> columnas BD
			Map<String, Object> values = new LinkedHashMap<>();

			for (Map.Entry<String, Object> entry : fields.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				switch (key) {
				case Servidor.COL_SERV_ID -> values.put(Servidor.COL_SERV_ID, value);
				case Servidor.COL_DNS -> values.put(Servidor.COL_DNS, value);
				case Servidor.COL_HOSTNAME -> values.put(Servidor.COL_HOSTNAME, value);
				case Servidor.COL_PRETTY_OS -> values.put(Servidor.COL_PRETTY_OS, value);
				case Servidor.COL_ARCH -> values.put(Servidor.COL_ARCH, value);
				case Servidor.COL_KERNEL -> values.put(Servidor.COL_KERNEL, value);
				case Servidor.COL_SECCION_ID -> values.put(Servidor.COL_SECCION_ID, value);
				case Servidor.COL_IMAGEN -> values.put(Servidor.COL_IMAGEN, value);
				default -> throw new ValidationException("Campo no permitido: " + key);
				}
			}

			if (values.isEmpty())
				throw new ValidationException("No hay campos válidos");

			// 1. Obtener serverId actual si se va a actualizar (ANTES del update)
			String oldServerId = null;
			if (values.containsKey(Servidor.COL_SERV_ID)) {
				oldServerId = findServerIdById(id);
				if (oldServerId == null)
					throw new ValidationException("ServerId no encontrado");
			}

			// 2. Ejecutar update SQL (pasando una conexión)
			boolean updated = false;
			try (Connection conn = getDS().getConnection()) {
				updated = update(conn, Servidor.TABLE, values, "id = ?", new Object[] { id });
			}

			// 3. Actualizar Mongo si fue exitoso y el serverId cambió
			if (updated && oldServerId != null) {
				String newServerId = (String) values.get(Servidor.COL_SERV_ID);
				if (!oldServerId.equals(newServerId)) {
					System.out.printf("Mongo actualizado: %s documentos%n",
							mongoDao.updateServerId(oldServerId, newServerId));
				}
			}

			return updated;
		} catch (SQLException e) {
			throw new DaoException("Update servidor falló", e);
		}
	}

	public boolean deleteServidor(int id) {
		try {
			// 1. Obtener el serverId actual (ANTES del delete)
			String serverId = findServerIdById(id);

			if (serverId == null)
				throw new ValidationException("ServerId no encontrado");

			// 2. Ejecutar DROP SQL
			boolean deleted = false;
			try (Connection conn = getDS().getConnection()) {
				deleted = deleteOne(conn, Servidor.TABLE, Servidor.COL_ID, id);
			}

			// 3. Borrar los registros en Mongo
			if (deleted)
				System.out.printf("Mongo eliminado: %s documentos%n", mongoDao.deleteByServerId(serverId));

			return deleted;
		} catch (SQLException e) {
			throw new DaoException("Delete servidor falló", e);
		}
	}

	public int deleteServidoresById(List<Integer> ids) {
		try {
			List<String> serverIds = ids.stream().map(this::findServerIdById).toList();
			int total = 0;

			try (Connection conn = getDS().getConnection()) {
				total = deleteAll(conn, Servidor.TABLE, Servidor.COL_ID, ids);
			}

			if (total <= 0)
				return 0;

			for (String srvId : serverIds) {
				if (srvId != null)
					mongoDao.deleteByServerId(srvId);
			}

			return total;
		} catch (SQLException e) {
			throw new DaoException("Delete servidores falló", e);
		}
	}

	public int addServiciosToServidor(int servidorId, List<Integer> servicioIds) {
		if (servicioIds == null || servicioIds.isEmpty())
			return 0;

		try (Connection conn = getDS().getConnection()) {
			List<Map<String, Object>> rows = servicioIds.stream().map(servicioId -> {
				Map<String, Object> m = new LinkedHashMap<>();
				m.put("servidorId", servidorId);
				m.put("servicioId", servicioId);
				return m;
			}).toList();

			int[] results = insertBatch(conn, Servicio.TABLE_RELATION, rows);
			return results.length;
		} catch (SQLException e) {
			throw new DaoException("Error al vincular servicios al servidor", e);
		}
	}

	public int removeServiciosFromServidor(int servidorId, List<Integer> servicioIds) {
		if (servicioIds == null || servicioIds.isEmpty())
			return 0;

		// Generamos los placeholders (?, ?, ?) para el IN clause
		String placeholders = servicioIds.stream().map(id -> "?").collect(Collectors.joining(","));
		String sql = String.format("DELETE FROM %s WHERE servidorId = ? AND servicioId IN (%s)",
				Servicio.TABLE_RELATION, placeholders);

		try (Connection conn = getDS().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, servidorId);
			int index = 2;
			for (Integer srvId : servicioIds) {
				ps.setInt(index++, srvId);
			}

			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException("Error al desvincular servicios del servidor", e);
		}
	}

	// ==========================================
	// MÉTODOS SERVICIO
	// ==========================================
	public Servicio findServicioById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_SRV_BY_ID, ps -> ps.setInt(1, id), Servicio::mapServicio);
		} catch (SQLException e) {
			throw new DaoException("Find Servicio BD falló", e);
		}
	}

	public List<Servicio> findAllServicio() {
		try {
			return findAll(SqlQueries.FIND_FULL_SRV, null, rs -> mapList(rs, Servicio::mapServicio));
		} catch (SQLException e) {
			throw new DaoException("Find Servicios BD falló", e);
		}
	}

	public int insertServicio(Servicio servicio) {
		if (servicio == null)
			return -1;

		try (Connection conn = getDS().getConnection()) {
			int idServicio = insert(conn, Servicio.TABLE, servicio.toMap());
			if (idServicio <= 0)
				throw new SQLException("ID inválido generado para Servicio");

			return idServicio;
		} catch (SQLException e) {
			throw new DaoException("Insert Servicio falló", e);
		}
	}

	public boolean updateServicio(int id, Map<String, Object> fields) {
		try (Connection conn = getDS().getConnection()) {
			return update(conn, Servicio.TABLE, fields, "id = ?", new Object[] { id });
		} catch (SQLException e) {
			throw new DaoException("Update servicio falló en BD", e);
		}
	}

	public boolean deleteServicio(int id) {
		try (Connection conn = getDS().getConnection()) {
			return deleteOne(conn, Servicio.TABLE, Servicio.COL_ID, id);
		} catch (SQLException e) {
			throw new DaoException("Delete Servicio falló", e);
		}
	}

	// ==========================================
	// MÉTODOS SECCIÓN
	// ==========================================
	public Seccion findSeccionById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_SECCN_BY_ID, ps -> ps.setInt(1, id), Seccion::mapSeccion);
		} catch (SQLException e) {
			throw new DaoException("Find Sección BD falló", e);
		}
	}

	public List<Seccion> findAllSeccion() {
		try {
			return findAll(SqlQueries.FIND_FULL_SECCN, null, rs -> mapList(rs, Seccion::mapSeccion));
		} catch (SQLException e) {
			throw new DaoException("Find All Secciones BD falló", e);
		}
	}

	public int insertSeccion(Seccion seccion) {
		if (seccion == null)
			return -1;

		try (Connection conn = getDS().getConnection()) {
			int idSeccion = insert(conn, Seccion.TABLE, seccion.toMap());
			if (idSeccion <= 0)
				throw new SQLException("ID inválido generado para Sección");

			return idSeccion;
		} catch (SQLException e) {
			throw new DaoException("Insert Sección falló", e);
		}
	}

	public boolean updateSeccion(int id, Map<String, Object> fields) {
		try {
			Map<String, Object> values = new LinkedHashMap<>();

			for (Map.Entry<String, Object> entry : fields.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				switch (key) {
				case Seccion.COL_NOMBRE -> values.put(Seccion.COL_NOMBRE, value);
				case Seccion.COL_DESCRIP -> values.put(Seccion.COL_DESCRIP, value);
				default -> throw new ValidationException("Campo no permitido en Sección: " + key);
				}
			}

			if (values.isEmpty())
				throw new ValidationException("No hay campos válidos para actualizar en Sección");

			try (Connection conn = getDS().getConnection()) {
				return update(conn, Seccion.TABLE, values, "id = ?", new Object[] { id });
			}
		} catch (SQLException e) {
			throw new DaoException("Update sección falló", e);
		}
	}

	public boolean deleteSeccion(int id) {
		try (Connection conn = getDS().getConnection()) {
			return deleteOne(conn, Seccion.TABLE, Seccion.COL_ID, id);
		} catch (SQLException e) {
			// Nota: Esto fallará si hay servidores vinculados a esta sección (FK
			// constraint)
			throw new DaoException("Delete Sección falló. Verifique si tiene servidores asociados.", e);
		}
	}

	// ==========================================
	// MÉTODOS USUARIO
	// ==========================================
	public Usuario findUsuarioById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_USER_BY_ID, ps -> ps.setInt(1, id), rs -> Usuario.mapUsuario(rs));
		} catch (SQLException e) {
			throw new DaoException("Error al buscar usuario por ID: " + id, e);
		}
	}

	public List<Usuario> findAllUsuarios() {
		try {
			return findAll(SqlQueries.FIND_FULL_USER, null, rs -> mapList(rs, r -> Usuario.mapUsuario(r)));
		} catch (SQLException e) {
			throw new DaoException("Error al recuperar todos los usuarios", e);
		}
	}

	public int insertSingleUsuario(Usuario usuario) throws SQLException {
		try (Connection conn = getDS().getConnection()) {
			return insert(conn, Usuario.TABLE, usuario.toMap());
		}
	}

	public int insertUsuarios(List<Usuario> usuarios) {
		if (usuarios == null || usuarios.isEmpty())
			return 0;

		try (Connection conn = getDS().getConnection()) {
			List<Map<String, Object>> rows = usuarios.stream().map(Usuario::toMap).collect(Collectors.toList());

			int[] results = insertBatch(conn, Usuario.TABLE, rows);
			return Arrays.stream(results).sum();
		} catch (SQLException e) {
			throw new DaoException("Error al insertar usuarios en bloque", e);
		}
	}

	public boolean updateUsuario(int id, Map<String, Object> fields) {
		try (Connection conn = getDS().getConnection()) {
			return update(conn, Usuario.TABLE, fields, Usuario.COL_ID + " = ?", new Object[] { id });
		} catch (SQLException e) {
			throw new DaoException("Error al actualizar el usuario ID: " + id, e);
		}
	}

	public boolean deleteSingleUsuario(int id) throws SQLException {
		try (Connection conn = getDS().getConnection()) {
			return deleteOne(conn, Usuario.TABLE, Usuario.COL_ID, id);
		}
	}

	public int deleteUsuarios(List<Integer> ids) {
		try (Connection conn = getDS().getConnection()) {
			return deleteAll(conn, Usuario.TABLE, Usuario.COL_ID, ids);
		} catch (SQLException e) {
			throw new DaoException("Error al eliminar usuarios", e);
		}
	}

	public List<String> findUsernamesLike(String baseUsername) {
		try {
			return findAll(SqlQueries.FIND_USERNAME_USER, ps -> ps.setString(1, baseUsername + "%"),
					rs -> mapList(rs, r -> r.getString("username")));
		} catch (SQLException e) {
			throw new DaoException("Error al buscar usernames similares", e);
		}
	}

	// ==========================================
	// MÉTODOS GRUPO
	// ==========================================
	public Grupo findGrupoById(int id) {
		try {
			Grupo grupo = findOne(SqlQueries.FIND_FULL_GROUP_BY_ID, ps -> ps.setInt(1, id), rs -> Grupo.mapGrupo(rs));
			if (grupo != null) {
				// Grupo espera PermissionMap<Integer>
				PermissionMap<Integer> pMap = new PermissionMap<>(new ArrayList<>(), new HashMap<>());
				pMap.setGlobal(getGlobalPermissionIds(id));
				pMap.setSections(getSectionPermissionIds(id));
				grupo.setPermisos(pMap);
			}
			return grupo;
		} catch (SQLException e) {
			throw new DaoException("Error Grupo", e);
		}
	}

	public List<Grupo> findAllGrupos() {
		try {
			// 1. Obtenemos la lista base de grupos
			List<Grupo> grupos = findAll(SqlQueries.FIND_FULL_GROUP, null, rs -> mapList(rs, r -> Grupo.mapGrupo(r)));

			// 2. Poblamos los permisos (IDs) para cada grupo
			for (Grupo g : grupos) {
				PermissionMap<Integer> pMap = new PermissionMap<>(new ArrayList<>(), new HashMap<>());

				// Usamos los métodos que devuelven IDs que creamos antes
				pMap.setGlobal(getGlobalPermissionIds(g.getId()));
				pMap.setSections(getSectionPermissionIds(g.getId()));

				g.setPermisos(pMap);
			}

			return grupos;
		} catch (SQLException e) {
			throw new DaoException("Error al recuperar todos los grupos con sus permisos", e);
		}
	}

	public int insertGrupo(Grupo grupo) throws SQLException {
		Connection conn = null;
		try {
			conn = getDS().getConnection();
			conn.setAutoCommit(false); // Iniciamos transacción

			// 1. Insertar el grupo base
			int grupoId = insert(conn, Grupo.TABLE, grupo.toMap());

			// 2. Insertar permisos si existen
			if (grupo.getPermisos() != null) {
				savePermissions(conn, grupoId, grupo.getPermisos());
			}

			conn.commit();
			return grupoId;
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			throw e;
		} finally {
			if (conn != null) {
				// RESTAURACIÓN CRÍTICA
				try {
					conn.setAutoCommit(true);
				} catch (SQLException ex) {
					// Loguear error pero no tapar la excepción principal
				}
				conn.close(); // Ahora vuelve al pool en estado "limpio"
			}
		}
	}

	public boolean updateGrupo(int id, Grupo grupo) throws SQLException {
		Connection conn = null;
		try {
			conn = getDS().getConnection();
			conn.setAutoCommit(false);

			// --- PARTE DINÁMICA (PATCH de campos básicos) ---
			Map<String, Object> fields = new LinkedHashMap<>();
			if (grupo.getNombre() != null)
				fields.put(Grupo.COL_NOMBRE, grupo.getNombre());

			if (grupo.isSuperAdmin() != null)
				fields.put(Grupo.COL_SUPER_ADMIN, grupo.isSuperAdmin());

			if (!fields.isEmpty()) {
				update(conn, Grupo.TABLE, fields, Grupo.COL_ID + " = ?", new Object[] { id });
			}

			// --- PARTE DE PERMISOS ---
			// Si el cliente no envía la clave "permisos" en el JSON,
			// grupo.getPermisos() será NULL y NO TOCAREMOS la base de datos.
			if (grupo.getPermisos() != null) {
				// Borramos relaciones antiguas
				deleteOne(conn, Grupo.TABLE_GLOBAL_PRM, "grupoId", id);
				deleteOne(conn, Grupo.TABLE_SECTION_PRM, "grupoId", id);

				// Insertamos las nuevas
				savePermissions(conn, id, grupo.getPermisos());
			}

			conn.commit();
			return true;
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			throw e;
		} finally {
			if (conn != null) {
				// RESTAURACIÓN CRÍTICA
				try {
					conn.setAutoCommit(true);
				} catch (SQLException ex) {
					// Loguear error pero no tapar la excepción principal
				}
				conn.close(); // Ahora vuelve al pool en estado "limpio"
			}
		}
	}

	public int deleteGrupos(List<Integer> ids) {
		try (Connection conn = getDS().getConnection()) {
			return deleteAll(conn, Grupo.TABLE, Grupo.COL_ID, ids);
		} catch (SQLException e) {
			throw new DaoException("Error al eliminar los grupos seleccionados", e);
		}
	}

	public boolean deleteGrupo(int id) throws SQLException {
		try (Connection conn = getDS().getConnection()) {
			return deleteOne(conn, Grupo.TABLE, Grupo.COL_ID, id);
		}
	}

	// ==========================================
	// MÉTODOS TOKEN
	// ==========================================
	public List<ApiToken> findTokensByUsuario(int usuarioId) {
		try {
			return findAll(SqlQueries.FIND_TOKENS_BY_USUARIOID, ps -> ps.setInt(1, usuarioId),
					rs -> mapList(rs, ApiToken::mapToken));
		} catch (SQLException e) {
			throw new DaoException("Error al listar tokens del usuario", e);
		}
	}

	public int createToken(int usuarioId, String tokenValue) {
		try (Connection conn = getDS().getConnection()) {
			Map<String, Object> values = new LinkedHashMap<>();
			values.put(ApiToken.COL_USER_ID, usuarioId);
			values.put(ApiToken.COL_TOKEN, tokenValue);
			values.put(ApiToken.COL_ACTIVE, true);
			return insert(conn, ApiToken.TABLE, values);
		} catch (SQLException e) {
			throw new DaoException("Error al insertar nuevo token", e);
		}
	}

	public boolean updateTokenStatus(int tokenId, boolean active) {
		try (Connection conn = getDS().getConnection()) {
			Map<String, Object> fields = Map.of(ApiToken.COL_ACTIVE, active);
			return update(conn, ApiToken.TABLE, fields, ApiToken.COL_ID + " = ?", new Object[] { tokenId });
		} catch (SQLException e) {
			throw new DaoException("Error al cambiar estado del token", e);
		}
	}

	// ==========================================
	// MÉTODOS AUTHENTICATION
	// ==========================================
	public Session getSessionByToken(String token) {
		try {
			Session session = findOne(SqlQueries.FIND_USER_GROUP_AUTH, ps -> ps.setString(1, token),
					Session::mapSession);
			if (session == null)
				return null;

			// Session espera PermissionMap<String>
			PermissionMap<String> pMap = new PermissionMap<>(new ArrayList<>(), new HashMap<>());
			if (session.getGrupo() != null && !session.getGrupo().isSuperAdmin()) {
				int gId = session.getGrupo().getId();
				pMap.setGlobal(getGlobalPermissionNames(gId));
				pMap.setSections(getSectionPermissionNames(gId));
			}
			session.setPermisos(pMap);
			return session;
		} catch (SQLException e) {
			throw new DaoException("Error Session", e);
		}
	}

	// ==========================================
	// MÉTODOS PERMISO
	// ==========================================
	public Permiso findPermisoById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_PRM_BY_ID, ps -> ps.setInt(1, id), rs -> Permiso.mapPermisoToSimpl(rs));
		} catch (SQLException e) {
			throw new DaoException("Error al buscar permiso por ID: " + id, e);
		}
	}

	public List<Permiso> findAllPermisos() {
		try {
			return findAll(SqlQueries.FIND_FULL_PRM, null, rs -> mapList(rs, r -> Permiso.mapPermisoToSimpl(r)));
		} catch (SQLException e) {
			throw new DaoException("Error al recuperar el catálogo de permisos", e);
		}
	}

	/* --- PARA SESIÓN (STRINGS) --- */
	private List<String> getGlobalPermissionNames(int grupoId) throws SQLException {
		return findAll(SqlQueries.FIND_PRM_GLOBAL_BY_GROUPID_NAME, ps -> ps.setInt(1, grupoId),
				rs -> mapList(rs, r -> r.getString("pkey")));
	}

	private Map<Integer, List<String>> getSectionPermissionNames(int grupoId) throws SQLException {
		return findAll(SqlQueries.FIND_PRM_SECCN_BY_GROUPID_NAME, ps -> ps.setInt(1, grupoId), rs -> {
			Map<Integer, List<String>> map = new HashMap<>();
			while (rs.next()) {
				map.computeIfAbsent(rs.getInt("seccionId"), k -> new ArrayList<>()).add(rs.getString("pkey"));
			}
			return map;
		});
	}

	/* --- PARA GRUPO (INTEGERS) --- */
	private List<Integer> getGlobalPermissionIds(int grupoId) throws SQLException {
		return findAll(SqlQueries.FIND_PRM_GLOBAL_BY_GROUPID_ID, ps -> ps.setInt(1, grupoId),
				rs -> mapList(rs, r -> r.getInt("permisoId")));
	}

	private Map<Integer, List<Integer>> getSectionPermissionIds(int grupoId) throws SQLException {
		return findAll(SqlQueries.FIND_PRM_SECCN_BY_GROUPID_ID, ps -> ps.setInt(1, grupoId), rs -> {
			Map<Integer, List<Integer>> map = new HashMap<>();
			while (rs.next()) {
				map.computeIfAbsent(rs.getInt("seccionId"), k -> new ArrayList<>()).add(rs.getInt("permisoId"));
			}
			return map;
		});
	}

	private void savePermissions(Connection conn, int grupoId, PermissionMap<Integer> pMap) throws SQLException {
		// Inserción Globales
		if (pMap.getGlobal() != null && !pMap.getGlobal().isEmpty()) {
			try (PreparedStatement ps = conn.prepareStatement(SqlQueries.INSERT_PRM_GLOBAL_BY_GROUP)) {
				for (Integer pId : pMap.getGlobal()) {
					ps.setInt(1, grupoId);
					ps.setInt(2, pId);
					ps.addBatch();
				}
				ps.executeBatch();
			}
		}

		// Inserción Secciones
		if (pMap.getSections() != null && !pMap.getSections().isEmpty()) {
			try (PreparedStatement ps = conn.prepareStatement(SqlQueries.INSERT_PRM_SECCN_BY_GROUP)) {
				for (Map.Entry<Integer, List<Integer>> entry : pMap.getSections().entrySet()) {
					int seccionId = entry.getKey();
					for (Integer pId : entry.getValue()) {
						ps.setInt(1, grupoId);
						ps.setInt(2, seccionId);
						ps.setInt(3, pId);
						ps.addBatch();
					}
				}
				ps.executeBatch();
			}
		}
	}

	// ==========================================
	// MÉTODOS AMBITO
	// ==========================================
	public Ambito findAmbitoById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_AMBT_BY_ID, ps -> ps.setInt(1, id), rs -> Ambito.mapAmbito(rs));
		} catch (SQLException e) {
			throw new DaoException("Error al buscar ámbito por ID: " + id, e);
		}
	}

	public List<Ambito> findAllAmbito() {
		try {
			return findAll(SqlQueries.FIND_FULL_AMBT, null, rs -> mapList(rs, r -> Ambito.mapAmbito(rs)));
		} catch (SQLException e) {
			throw new DaoException("Error al recuperar el listado de ámbitos.", e);
		}
	}

	// ==========================================
	// MÉTODOS GENÉRICOS
	// ==========================================
	private <R> R findOne(String sql, SQLConsumer<PreparedStatement> blinder, SQLFunction<ResultSet, R> mapper)
			throws SQLException {
		try (Connection conn = getDS().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			if (blinder != null)
				blinder.accept(ps);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapper.apply(rs);
				}
			}
		}
		return null;
	}

	private <R> R findAll(String sql, SQLConsumer<PreparedStatement> blinder, SQLFunction<ResultSet, R> mapper)
			throws SQLException {
		try (Connection conn = getDS().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			if (blinder != null)
				blinder.accept(ps);

			try (ResultSet rs = ps.executeQuery()) {
				return mapper.apply(rs);
			}
		}
	}

	private <R> R query(String sql, SQLFunction<ResultSet, R> extractor) throws SQLException {
		try (Connection conn = getDS().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			return extractor.apply(rs);
		}
	}

	private int insert(Connection conn, String table, Map<String, Object> values) throws SQLException {
		if (values == null || values.isEmpty())
			throw new SQLException("No hay valores para insertar");

		StringBuilder columns = new StringBuilder();
		StringBuilder placeholders = new StringBuilder();

		for (String key : values.keySet()) {
			columns.append(key).append(",");
			placeholders.append("?,");
		}

		columns.setLength(columns.length() - 1);
		placeholders.setLength(placeholders.length() - 1);

		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, placeholders);

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			int index = 1;
			for (Object value : values.values()) {
				ps.setObject(index++, value);
			}

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					int id = rs.getInt(1);
					if (id <= 0)
						throw new SQLException("Clave primaria inválida generada");

					return id;
				}
			}
		}
		throw new SQLException("No se generó clave primaria");
	}

	private int[] insertBatch(Connection conn, String table, List<Map<String, Object>> rows) throws SQLException {
		if (rows == null || rows.isEmpty())
			return new int[0];

		Map<String, Object> first = new LinkedHashMap<>(rows.get(0));

		StringBuilder columns = new StringBuilder();
		StringBuilder placeholders = new StringBuilder();

		for (String key : first.keySet()) {
			columns.append(key).append(",");
			placeholders.append("?,");
		}

		columns.setLength(columns.length() - 1);
		placeholders.setLength(placeholders.length() - 1);

		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, placeholders);

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Map<String, Object> row : rows) {
				if (!row.keySet().equals(first.keySet()))
					throw new SQLException("Columnas inconsistentes en batch insert");

				int index = 1;
				for (String key : first.keySet()) {
					ps.setObject(index++, row.get(key));
				}
				ps.addBatch();
			}
			return ps.executeBatch();
		}
	}

	private boolean update(Connection conn, String table, Map<String, Object> values, String whereClause,
			Object[] whereParams) throws SQLException {
		if (values == null || values.isEmpty())
			throw new SQLException("No hay valores para actualizar");

		String setPart = values.keySet().stream().map(k -> k + " = ?").collect(Collectors.joining(", "));
		String sql = String.format("UPDATE %s SET %s WHERE %s", table, setPart, whereClause);

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			int index = 1;
			for (Map.Entry<String, Object> entry : values.entrySet())
				ps.setObject(index++, entry.getValue());

			for (Object param : whereParams)
				ps.setObject(index++, param);

			return ps.executeUpdate() > 0;
		}
	}

	private boolean deleteOne(Connection conn, String table, String idColum, int id) throws SQLException {
		String sql = String.format("DELETE FROM %s WHERE %s = ?", table, idColum);

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	private int deleteAll(Connection conn, String table, String idColum, List<Integer> ids) throws SQLException {
		if (ids == null || ids.isEmpty())
			return 0;

		String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
		String sql = String.format("DELETE FROM %s WHERE %s IN (%s)", table, idColum, placeholders);

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			int index = 1;
			for (Integer id : ids)
				ps.setObject(index++, id);

			return ps.executeUpdate();
		}
	}

	private <T> List<T> mapList(ResultSet rs, SQLFunction<ResultSet, T> mapper) throws SQLException {
		List<T> list = new ArrayList<>();
		while (rs.next()) {
			list.add(mapper.apply(rs));
		}
		return list;
	}
}