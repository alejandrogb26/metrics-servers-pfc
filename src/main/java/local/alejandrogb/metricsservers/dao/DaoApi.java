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

/**
 * Objeto de acceso a datos (DAO) principal del sistema encargado de gestionar
 * las operaciones de persistencia sobre la base de datos relacional y la
 * integración con MongoDB.
 *
 * <p>
 * Esta clase implementa el patrón <b>Singleton</b> para proporcionar una única
 * instancia reutilizable durante todo el ciclo de vida de la aplicación.
 * Centraliza todas las operaciones de acceso a datos relacionadas con:
 * </p>
 *
 * <ul>
 * <li>Servidores</li>
 * <li>Servicios</li>
 * <li>Secciones</li>
 * <li>Usuarios</li>
 * <li>Grupos y permisos</li>
 * <li>Tokens de autenticación</li>
 * <li>Ámbitos</li>
 * </ul>
 *
 * <p>
 * La capa DAO utiliza JDBC mediante un {@link javax.sql.DataSource} gestionado
 * por el contenedor de aplicaciones. Todas las consultas SQL se encuentran
 * centralizadas en {@link local.alejandrogb.metricsservers.utils.SqlQueries}.
 * </p>
 *
 * <p>
 * Además, esta clase coordina operaciones híbridas entre la base de datos
 * relacional y MongoDB cuando es necesario mantener la coherencia entre ambas
 * fuentes de datos. Por ejemplo, cuando cambia el identificador de un servidor
 * en la base de datos relacional, se actualizan también los registros de
 * métricas almacenados en MongoDB mediante {@link MongoDao}.
 * </p>
 *
 * <p>
 * Para facilitar la reutilización de lógica JDBC y reducir código repetido, se
 * emplean métodos genéricos internos que utilizan las interfaces funcionales
 * {@link local.alejandrogb.metricsservers.utils.interfaces.SQLConsumer} y
 * {@link local.alejandrogb.metricsservers.utils.interfaces.SQLFunction}. Estas
 * permiten trabajar con expresiones lambda que pueden lanzar
 * {@link java.sql.SQLException}.
 * </p>
 *
 * <p>
 * La clase también gestiona transacciones cuando una operación requiere
 * múltiples modificaciones en la base de datos, asegurando consistencia
 * mediante commit y rollback.
 * </p>
 *
 * <p>
 * En caso de error durante una operación de acceso a datos, las excepciones SQL
 * se encapsulan en
 * {@link local.alejandrogb.metricsservers.exceptions.DaoException} para
 * desacoplar la capa de persistencia del resto de la aplicación.
 * </p>
 *
 * @author alejandrogb
 */
public class DaoApi {
	/**
	 * Instancia única del DAO utilizada por toda la aplicación.
	 */
	private static final DaoApi INSTANCE = new DaoApi();

	/**
	 * DAO especializado en operaciones sobre MongoDB para la gestión de métricas de
	 * servidores.
	 */
	private final MongoDao mongoDao = new MongoDao();

	/**
	 * Constructor privado para evitar la creación de instancias externas.
	 *
	 * <p>
	 * La clase utiliza el patrón Singleton, por lo que la instancia se crea
	 * internamente y se expone mediante {@link #getInstance()}.
	 * </p>
	 */
	private DaoApi() {
	}

	/**
	 * Devuelve la instancia única de {@link DaoApi}.
	 *
	 * @return instancia singleton del DAO principal
	 */
	public static DaoApi getInstance() {
		return INSTANCE;
	}

	/**
	 * Obtiene el {@link DataSource} configurado en el contenedor de aplicaciones.
	 *
	 * <p>
	 * Este método proporciona acceso al pool de conexiones JDBC utilizado para
	 * realizar operaciones sobre la base de datos relacional.
	 * </p>
	 *
	 * @return instancia de {@link DataSource} para obtener conexiones JDBC
	 */
	private DataSource getDS() {
		return GetDataSource.getDataSource();
	}

	// ==========================================
	// MÉTODOS SERVIDOR
	// ==========================================

	/**
	 * Recupera un servidor a partir de su identificador.
	 *
	 * <p>
	 * Este método ejecuta una consulta que obtiene toda la información relacionada
	 * con el servidor, incluyendo:
	 * </p>
	 *
	 * <ul>
	 * <li>Datos propios del servidor</li>
	 * <li>La sección a la que pertenece</li>
	 * <li>Los servicios asociados</li>
	 * </ul>
	 *
	 * <p>
	 * El resultado de la consulta se procesa mediante el método
	 * {@link Servidor#extractServidores(ResultSet)}, que agrupa los registros
	 * obtenidos en instancias completas de {@link Servidor}.
	 * </p>
	 *
	 * @param id identificador del servidor
	 * @return objeto {@link Servidor} correspondiente al identificador indicado, o
	 *         {@code null} si no existe
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public Servidor findServidorById(int id) {
		try {
			return findAll(SqlQueries.FIND_FULL_SERV_BY_ID, ps -> ps.setInt(1, id),
					rs -> Servidor.extractServidores(rs).values().stream().findFirst().orElse(null));
		} catch (SQLException e) {
			throw new DaoException("Find Server BD falló", e);
		}
	}

	/**
	 * Recupera todos los servidores registrados en el sistema.
	 *
	 * <p>
	 * La consulta incluye información adicional asociada a cada servidor, como su
	 * sección y los servicios vinculados.
	 * </p>
	 *
	 * <p>
	 * Los resultados de la consulta se transforman en objetos {@link Servidor}
	 * mediante el método {@link Servidor#extractServidores(ResultSet)}.
	 * </p>
	 *
	 * @return lista de servidores registrados en el sistema
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<Servidor> findAllServidor() {
		try {
			return query(SqlQueries.FIND_FULL_SERV, rs -> new ArrayList<>(Servidor.extractServidores(rs).values()));
		} catch (SQLException e) {
			throw new DaoException("FindAll Servers BD falló", e);
		}
	}

	/**
	 * Obtiene el identificador lógico de un servidor a partir de su id interno.
	 *
	 * <p>
	 * Este identificador corresponde al valor utilizado en sistemas externos de
	 * monitorización o almacenamiento de métricas.
	 * </p>
	 *
	 * @param id identificador interno del servidor en la base de datos
	 * @return identificador lógico del servidor ({@code serverId})
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public String findServerIdById(int id) {
		try {
			return findOne(SqlQueries.FIND_SERVERID_SERV, ps -> ps.setInt(1, id), rs -> rs.getString("serverId"));
		} catch (SQLException e) {
			throw new DaoException("Error obteniendo serverId", e);
		}
	}

	/**
	 * Inserta un nuevo servidor en la base de datos junto con las relaciones con
	 * los servicios asociados.
	 *
	 * <p>
	 * La operación se ejecuta dentro de una transacción para garantizar la
	 * consistencia de los datos. El proceso consta de los siguientes pasos:
	 * </p>
	 *
	 * <ol>
	 * <li>Inserción del servidor en la tabla principal {@link Servidor#TABLE}.</li>
	 * <li>Obtención de la clave primaria generada para el servidor.</li>
	 * <li>Inserción de las relaciones servidor-servicio en la tabla
	 * {@link Servicio#TABLE_RELATION} mediante inserción por lotes
	 * ({@code batch insert}).</li>
	 * </ol>
	 *
	 * <p>
	 * Si ocurre cualquier error durante la operación, se realiza un
	 * {@code rollback} de la transacción para evitar estados inconsistentes en la
	 * base de datos.
	 * </p>
	 *
	 * <p>
	 * En caso de éxito, la transacción se confirma mediante {@code commit} y se
	 * devuelve el identificador generado para el nuevo servidor.
	 * </p>
	 *
	 * @param servidor objeto {@link Servidor} que contiene la información del
	 *                 servidor a insertar
	 * @return identificador generado para el servidor insertado, o {@code -1} si el
	 *         objeto proporcionado es {@code null}
	 * @throws DaoException si ocurre un error durante la inserción en la base de
	 *                      datos
	 */
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

	/**
	 * Actualiza los campos de un servidor existente.
	 *
	 * <p>
	 * Este método permite actualizar de forma parcial los atributos de un servidor
	 * a partir de un conjunto dinámico de campos. Los campos recibidos se validan y
	 * se transforman en columnas de base de datos antes de ejecutar la
	 * actualización.
	 * </p>
	 *
	 * <p>
	 * El proceso de actualización consta de las siguientes fases:
	 * </p>
	 *
	 * <ol>
	 * <li>Validación de los campos permitidos para actualización.</li>
	 * <li>Construcción del mapa de columnas y valores a actualizar.</li>
	 * <li>Obtención del {@code serverId} actual si dicho campo se va a
	 * modificar.</li>
	 * <li>Ejecución de la sentencia {@code UPDATE} en la base de datos
	 * relacional.</li>
	 * <li>Si el {@code serverId} ha cambiado, actualización de las métricas
	 * asociadas en MongoDB mediante
	 * {@link MongoDao#updateServerId(String, String)}.</li>
	 * </ol>
	 *
	 * <p>
	 * La sincronización con MongoDB es necesaria porque las métricas almacenadas en
	 * la colección {@code host_metrics} utilizan el campo {@code server_id} como
	 * referencia al servidor. Si dicho identificador cambia en la base de datos
	 * relacional, es necesario actualizar también los documentos de MongoDB para
	 * mantener la coherencia entre ambos sistemas de persistencia.
	 * </p>
	 *
	 * @param id     identificador del servidor que se desea actualizar
	 * @param fields mapa de campos a actualizar donde la clave representa el nombre
	 *               del campo y el valor el nuevo valor a asignar
	 * @return {@code true} si el servidor fue actualizado correctamente,
	 *         {@code false} si no se modificó ningún registro
	 * @throws ValidationException si se intenta actualizar un campo no permitido o
	 *                             no se proporcionan campos válidos
	 * @throws DaoException        si ocurre un error durante la operación en la
	 *                             base de datos
	 */
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

	/**
	 * Elimina un servidor del sistema a partir de su identificador.
	 *
	 * <p>
	 * La operación realiza dos acciones para mantener la coherencia entre los
	 * sistemas de persistencia utilizados por la aplicación:
	 * </p>
	 *
	 * <ol>
	 * <li>Eliminación del servidor en la base de datos relacional.</li>
	 * <li>Eliminación de todas las métricas asociadas en MongoDB.</li>
	 * </ol>
	 *
	 * <p>
	 * Antes de realizar la eliminación en la base de datos SQL, se obtiene el valor
	 * del {@code serverId}, ya que este identificador es utilizado como referencia
	 * en la colección de métricas de MongoDB ({@code host_metrics.server_id}).
	 * </p>
	 *
	 * <p>
	 * Si la eliminación en la base de datos relacional se completa con éxito, se
	 * procede a eliminar los documentos asociados en MongoDB mediante
	 * {@link MongoDao#deleteByServerId(String)}.
	 * </p>
	 *
	 * @param id identificador interno del servidor
	 * @return {@code true} si el servidor fue eliminado correctamente,
	 *         {@code false} si no se encontró ningún registro con ese id
	 * @throws ValidationException si no se encuentra el {@code serverId} asociado
	 *                             al servidor
	 * @throws DaoException        si ocurre un error durante la operación en la
	 *                             base de datos
	 */
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

	/**
	 * Elimina múltiples servidores a partir de una lista de identificadores.
	 *
	 * <p>
	 * Este método permite realizar una eliminación masiva de servidores en la base
	 * de datos relacional y posteriormente limpiar las métricas asociadas en
	 * MongoDB.
	 * </p>
	 *
	 * <p>
	 * El proceso consta de los siguientes pasos:
	 * </p>
	 *
	 * <ol>
	 * <li>Obtención de los {@code serverId} asociados a cada identificador.</li>
	 * <li>Eliminación masiva en la base de datos SQL mediante una cláusula
	 * {@code IN}.</li>
	 * <li>Eliminación de los documentos correspondientes en MongoDB para cada
	 * {@code serverId} recuperado.</li>
	 * </ol>
	 *
	 * <p>
	 * Esta operación permite mantener la coherencia entre los registros almacenados
	 * en la base de datos relacional y las métricas almacenadas en MongoDB.
	 * </p>
	 *
	 * @param ids lista de identificadores de los servidores a eliminar
	 * @return número total de servidores eliminados en la base de datos
	 * @throws DaoException si ocurre un error durante la operación
	 */
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

	/**
	 * Asocia uno o varios servicios a un servidor.
	 *
	 * <p>
	 * Este método crea registros en la tabla de relación
	 * {@link Servicio#TABLE_RELATION}, que representa la relación muchos-a-muchos
	 * entre servidores y servicios.
	 * </p>
	 *
	 * <p>
	 * Las asociaciones se insertan mediante un {@code batch insert} utilizando el
	 * método {@link #insertBatch(Connection, String, List)}, lo que permite mejorar
	 * el rendimiento cuando se vinculan múltiples servicios en una sola operación.
	 * </p>
	 *
	 * <p>
	 * Si la lista de identificadores de servicios es {@code null} o está vacía, el
	 * método no realiza ninguna operación y devuelve {@code 0}.
	 * </p>
	 *
	 * @param servidorId  identificador del servidor al que se asociarán los
	 *                    servicios
	 * @param servicioIds lista de identificadores de servicios a vincular
	 * @return número de asociaciones creadas
	 * @throws DaoException si ocurre un error durante la inserción en la base de
	 *                      datos
	 */
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

	/**
	 * Elimina la asociación entre un servidor y uno o varios servicios.
	 *
	 * <p>
	 * Este método elimina registros de la tabla de relación
	 * {@link Servicio#TABLE_RELATION} utilizando una cláusula {@code IN} para
	 * permitir eliminar múltiples asociaciones en una sola operación.
	 * </p>
	 *
	 * <p>
	 * Si la lista de servicios es {@code null} o está vacía, el método no realiza
	 * ninguna operación y devuelve {@code 0}.
	 * </p>
	 *
	 * @param servidorId  identificador del servidor
	 * @param servicioIds lista de identificadores de servicios que se desean
	 *                    desvincular del servidor
	 * @return número de asociaciones eliminadas
	 * @throws DaoException si ocurre un error durante la operación en la base de
	 *                      datos
	 */
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

	/**
	 * Recupera un servicio a partir de su identificador.
	 *
	 * <p>
	 * Este método ejecuta una consulta sobre la tabla de servicios y transforma el
	 * resultado en un objeto {@link Servicio} mediante el método
	 * {@link Servicio#mapServicio(ResultSet)}.
	 * </p>
	 *
	 * @param id identificador del servicio
	 * @return objeto {@link Servicio} correspondiente al identificador indicado, o
	 *         {@code null} si no existe
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public Servicio findServicioById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_SRV_BY_ID, ps -> ps.setInt(1, id), Servicio::mapServicio);
		} catch (SQLException e) {
			throw new DaoException("Find Servicio BD falló", e);
		}
	}

	/**
	 * Recupera todos los servicios registrados en el sistema.
	 *
	 * <p>
	 * Los resultados de la consulta se transforman en objetos {@link Servicio}
	 * utilizando el método {@link Servicio#mapServicio(ResultSet)}.
	 * </p>
	 *
	 * @return lista de servicios existentes en el sistema
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<Servicio> findAllServicio() {
		try {
			return findAll(SqlQueries.FIND_FULL_SRV, null, rs -> mapList(rs, Servicio::mapServicio));
		} catch (SQLException e) {
			throw new DaoException("Find Servicios BD falló", e);
		}
	}

	/**
	 * Inserta un nuevo servicio en la base de datos.
	 *
	 * <p>
	 * El servicio se persiste en la tabla {@link Servicio#TABLE} y se devuelve el
	 * identificador generado automáticamente por la base de datos.
	 * </p>
	 *
	 * @param servicio objeto {@link Servicio} que contiene los datos del servicio a
	 *                 insertar
	 * @return identificador generado para el nuevo servicio, o {@code -1} si el
	 *         objeto proporcionado es {@code null}
	 * @throws DaoException si ocurre un error durante la inserción
	 */
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

	/**
	 * Actualiza los campos de un servicio existente.
	 *
	 * <p>
	 * Este método permite realizar una actualización parcial utilizando un conjunto
	 * dinámico de campos. Cada entrada del mapa representa una columna de la base
	 * de datos y su nuevo valor.
	 * </p>
	 *
	 * @param id     identificador del servicio a actualizar
	 * @param fields mapa de columnas y valores a modificar
	 * @return {@code true} si el servicio fue actualizado correctamente,
	 *         {@code false} si no se modificó ningún registro
	 * @throws DaoException si ocurre un error durante la operación
	 */
	public boolean updateServicio(int id, Map<String, Object> fields) {

		try (Connection conn = getDS().getConnection()) {
			return update(conn, Servicio.TABLE, fields, "id = ?", new Object[] { id });
		} catch (SQLException e) {
			throw new DaoException("Update servicio falló en BD", e);
		}
	}

	/**
	 * Elimina un servicio del sistema a partir de su identificador.
	 *
	 * <p>
	 * La operación elimina el registro correspondiente de la tabla
	 * {@link Servicio#TABLE}.
	 * </p>
	 *
	 * @param id identificador del servicio a eliminar
	 * @return {@code true} si el servicio fue eliminado correctamente,
	 *         {@code false} si no existía un registro con ese identificador
	 * @throws DaoException si ocurre un error durante la operación
	 */
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

	/**
	 * Recupera una sección a partir de su identificador.
	 *
	 * <p>
	 * Este método ejecuta una consulta sobre la tabla de secciones y transforma el
	 * resultado en un objeto {@link Seccion} utilizando el método
	 * {@link Seccion#mapSeccion(ResultSet)}.
	 * </p>
	 *
	 * @param id identificador de la sección
	 * @return objeto {@link Seccion} correspondiente al identificador indicado, o
	 *         {@code null} si no existe
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public Seccion findSeccionById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_SECCN_BY_ID, ps -> ps.setInt(1, id), Seccion::mapSeccion);
		} catch (SQLException e) {
			throw new DaoException("Find Sección BD falló", e);
		}
	}

	/**
	 * Recupera todas las secciones registradas en el sistema.
	 *
	 * <p>
	 * Los resultados de la consulta se transforman en objetos {@link Seccion}
	 * mediante el método {@link Seccion#mapSeccion(ResultSet)}.
	 * </p>
	 *
	 * @return lista de secciones existentes en el sistema
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<Seccion> findAllSeccion() {
		try {
			return findAll(SqlQueries.FIND_FULL_SECCN, null, rs -> mapList(rs, Seccion::mapSeccion));
		} catch (SQLException e) {
			throw new DaoException("Find All Secciones BD falló", e);
		}
	}

	/**
	 * Inserta una nueva sección en la base de datos.
	 *
	 * <p>
	 * La sección se almacena en la tabla {@link Seccion#TABLE} y se devuelve el
	 * identificador generado automáticamente por la base de datos.
	 * </p>
	 *
	 * @param seccion objeto {@link Seccion} que contiene los datos de la sección a
	 *                insertar
	 * @return identificador generado para la nueva sección, o {@code -1} si el
	 *         objeto proporcionado es {@code null}
	 * @throws DaoException si ocurre un error durante la inserción
	 */
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

	/**
	 * Actualiza los campos de una sección existente.
	 *
	 * <p>
	 * Este método permite realizar una actualización parcial de la sección,
	 * validando previamente los campos permitidos para modificación.
	 * </p>
	 *
	 * <p>
	 * Los campos válidos para actualización son:
	 * </p>
	 *
	 * <ul>
	 * <li>{@link Seccion#COL_NOMBRE}</li>
	 * <li>{@link Seccion#COL_DESCRIP}</li>
	 * </ul>
	 *
	 * <p>
	 * Si se intenta modificar un campo no permitido, se lanzará una
	 * {@link ValidationException}.
	 * </p>
	 *
	 * @param id     identificador de la sección a actualizar
	 * @param fields mapa de campos y valores a modificar
	 * @return {@code true} si la sección fue actualizada correctamente,
	 *         {@code false} si no se modificó ningún registro
	 * @throws ValidationException si no se proporcionan campos válidos o se intenta
	 *                             modificar un campo no permitido
	 * @throws DaoException        si ocurre un error durante la operación
	 */
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

	/**
	 * Elimina una sección del sistema a partir de su identificador.
	 *
	 * <p>
	 * La operación elimina el registro correspondiente de la tabla
	 * {@link Seccion#TABLE}. Si existen servidores asociados a esta sección, la
	 * base de datos puede impedir la eliminación debido a restricciones de clave
	 * foránea.
	 * </p>
	 *
	 * @param id identificador de la sección a eliminar
	 * @return {@code true} si la sección fue eliminada correctamente, {@code false}
	 *         si no existía un registro con ese identificador
	 * @throws DaoException si ocurre un error durante la operación o si la sección
	 *                      tiene servidores asociados
	 */
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

	/**
	 * Recupera un usuario a partir de su identificador.
	 *
	 * <p>
	 * Este método ejecuta una consulta sobre la tabla de usuarios y transforma el
	 * resultado en un objeto {@link Usuario} mediante el método
	 * {@link Usuario#mapUsuario(ResultSet)}.
	 * </p>
	 *
	 * @param id identificador del usuario
	 * @return objeto {@link Usuario} correspondiente al identificador indicado, o
	 *         {@code null} si no existe
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public Usuario findUsuarioById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_USER_BY_ID, ps -> ps.setInt(1, id), rs -> Usuario.mapUsuario(rs));
		} catch (SQLException e) {
			throw new DaoException("Error al buscar usuario por ID: " + id, e);
		}
	}

	/**
	 * Recupera todos los usuarios registrados en el sistema.
	 *
	 * <p>
	 * Los resultados se transforman en objetos {@link Usuario} mediante el método
	 * {@link Usuario#mapUsuario(ResultSet)}.
	 * </p>
	 *
	 * @return lista de usuarios existentes en el sistema
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<Usuario> findAllUsuarios() {
		try {
			return findAll(SqlQueries.FIND_FULL_USER, null, rs -> mapList(rs, r -> Usuario.mapUsuario(r)));
		} catch (SQLException e) {
			throw new DaoException("Error al recuperar todos los usuarios", e);
		}
	}

	/**
	 * Inserta un único usuario en la base de datos.
	 *
	 * <p>
	 * El usuario se persiste en la tabla {@link Usuario#TABLE} y se devuelve el
	 * identificador generado por la base de datos.
	 * </p>
	 *
	 * @param usuario objeto {@link Usuario} que contiene los datos del usuario a
	 *                insertar
	 * @return identificador generado para el nuevo usuario
	 * @throws SQLException si ocurre un error durante la inserción
	 */
	public int insertSingleUsuario(Usuario usuario) throws SQLException {

		try (Connection conn = getDS().getConnection()) {
			return insert(conn, Usuario.TABLE, usuario.toMap());
		}
	}

	/**
	 * Inserta múltiples usuarios en la base de datos utilizando inserción por lotes
	 * (batch).
	 *
	 * <p>
	 * Este método mejora el rendimiento al permitir insertar múltiples registros en
	 * una sola operación mediante {@link PreparedStatement#executeBatch()}.
	 * </p>
	 *
	 * @param usuarios lista de usuarios a insertar
	 * @return número total de registros insertados
	 * @throws DaoException si ocurre un error durante la operación
	 */
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

	/**
	 * Actualiza los campos de un usuario existente.
	 *
	 * <p>
	 * Permite realizar una actualización parcial mediante un mapa de campos y
	 * valores. Cada entrada del mapa representa una columna de la base de datos y
	 * su nuevo valor.
	 * </p>
	 *
	 * @param id     identificador del usuario a actualizar
	 * @param fields mapa de columnas y valores a modificar
	 * @return {@code true} si el usuario fue actualizado correctamente,
	 *         {@code false} si no se modificó ningún registro
	 * @throws DaoException si ocurre un error durante la operación
	 */
	public boolean updateUsuario(int id, Map<String, Object> fields) {

		try (Connection conn = getDS().getConnection()) {

			return update(conn, Usuario.TABLE, fields, Usuario.COL_ID + " = ?", new Object[] { id });

		} catch (SQLException e) {
			throw new DaoException("Error al actualizar el usuario ID: " + id, e);
		}
	}

	/**
	 * Elimina un usuario del sistema a partir de su identificador.
	 *
	 * @param id identificador del usuario a eliminar
	 * @return {@code true} si el usuario fue eliminado correctamente, {@code false}
	 *         si no existía un registro con ese identificador
	 * @throws SQLException si ocurre un error durante la operación
	 */
	public boolean deleteSingleUsuario(int id) throws SQLException {

		try (Connection conn = getDS().getConnection()) {
			return deleteOne(conn, Usuario.TABLE, Usuario.COL_ID, id);
		}
	}

	/**
	 * Elimina múltiples usuarios a partir de una lista de identificadores.
	 *
	 * <p>
	 * La eliminación se realiza mediante una cláusula {@code IN}, permitiendo
	 * borrar varios registros en una sola operación.
	 * </p>
	 *
	 * @param ids lista de identificadores de usuarios a eliminar
	 * @return número total de usuarios eliminados
	 * @throws DaoException si ocurre un error durante la operación
	 */
	public int deleteUsuarios(List<Integer> ids) {

		try (Connection conn = getDS().getConnection()) {

			return deleteAll(conn, Usuario.TABLE, Usuario.COL_ID, ids);

		} catch (SQLException e) {
			throw new DaoException("Error al eliminar usuarios", e);
		}
	}

	/**
	 * Busca nombres de usuario que comiencen con un prefijo determinado.
	 *
	 * <p>
	 * Este método se utiliza habitualmente para comprobar la disponibilidad de
	 * nombres de usuario o generar variantes únicas durante procesos de registro.
	 * </p>
	 *
	 * @param baseUsername prefijo del nombre de usuario a buscar
	 * @return lista de usernames que coinciden con el patrón
	 * @throws DaoException si ocurre un error durante la consulta
	 */
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

	/**
	 * Recupera un grupo a partir de su identificador junto con sus permisos
	 * asociados.
	 *
	 * <p>
	 * El método realiza las siguientes operaciones:
	 * </p>
	 *
	 * <ol>
	 * <li>Obtiene la información básica del grupo desde la base de datos.</li>
	 * <li>Recupera los identificadores de permisos globales asociados al
	 * grupo.</li>
	 * <li>Recupera los permisos asociados a secciones específicas.</li>
	 * <li>Construye un {@link PermissionMap} que se asigna al objeto
	 * {@link Grupo}.</li>
	 * </ol>
	 *
	 * @param id identificador del grupo
	 * @return objeto {@link Grupo} con sus permisos asociados, o {@code null} si no
	 *         existe un grupo con ese identificador
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public Grupo findGrupoById(int id) {
		try {

			Grupo grupo = findOne(SqlQueries.FIND_FULL_GROUP_BY_ID, ps -> ps.setInt(1, id), rs -> Grupo.mapGrupo(rs));

			if (grupo != null) {

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

	/**
	 * Recupera todos los grupos del sistema junto con sus permisos asociados.
	 *
	 * <p>
	 * El proceso consta de dos fases:
	 * </p>
	 *
	 * <ol>
	 * <li>Recuperar la lista básica de grupos desde la base de datos.</li>
	 * <li>Para cada grupo, cargar los permisos globales y de sección.</li>
	 * </ol>
	 *
	 * @return lista de grupos con sus permisos configurados
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<Grupo> findAllGrupos() {

		try {

			// 1. Obtenemos la lista base de grupos
			List<Grupo> grupos = findAll(SqlQueries.FIND_FULL_GROUP, null, rs -> mapList(rs, r -> Grupo.mapGrupo(r)));

			// 2. Poblamos los permisos para cada grupo
			for (Grupo g : grupos) {

				PermissionMap<Integer> pMap = new PermissionMap<>(new ArrayList<>(), new HashMap<>());

				pMap.setGlobal(getGlobalPermissionIds(g.getId()));
				pMap.setSections(getSectionPermissionIds(g.getId()));

				g.setPermisos(pMap);
			}

			return grupos;

		} catch (SQLException e) {
			throw new DaoException("Error al recuperar todos los grupos con sus permisos", e);
		}
	}

	/**
	 * Inserta un nuevo grupo en el sistema junto con sus permisos asociados.
	 *
	 * <p>
	 * La operación se ejecuta dentro de una transacción para garantizar la
	 * consistencia de los datos.
	 * </p>
	 *
	 * <p>
	 * El proceso consta de:
	 * </p>
	 *
	 * <ol>
	 * <li>Inserción del grupo en la tabla principal.</li>
	 * <li>Inserción de los permisos globales y de sección asociados.</li>
	 * </ol>
	 *
	 * @param grupo objeto {@link Grupo} a insertar
	 * @return identificador generado para el nuevo grupo
	 * @throws SQLException si ocurre un error durante la operación
	 */
	public int insertGrupo(Grupo grupo) throws SQLException {

		Connection conn = null;

		try {

			conn = getDS().getConnection();
			conn.setAutoCommit(false);

			// 1. Insertar grupo
			int grupoId = insert(conn, Grupo.TABLE, grupo.toMap());

			// 2. Insertar permisos
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

				try {
					conn.setAutoCommit(true);
				} catch (SQLException ex) {
				}

				conn.close();
			}
		}
	}

	/**
	 * Actualiza la información de un grupo existente.
	 *
	 * <p>
	 * Permite realizar una actualización parcial de los campos básicos del grupo
	 * (por ejemplo nombre o estado de superadministrador).
	 * </p>
	 *
	 * <p>
	 * Si el objeto contiene información de permisos, estos se actualizarán
	 * eliminando primero las relaciones existentes e insertando las nuevas.
	 * </p>
	 *
	 * <p>
	 * Toda la operación se ejecuta dentro de una transacción.
	 * </p>
	 *
	 * @param id    identificador del grupo a actualizar
	 * @param grupo objeto {@link Grupo} con los nuevos valores
	 * @return {@code true} si la operación se completó correctamente
	 * @throws SQLException si ocurre un error durante la operación
	 */
	public boolean updateGrupo(int id, Grupo grupo) throws SQLException {

		Connection conn = null;

		try {

			conn = getDS().getConnection();
			conn.setAutoCommit(false);

			// --- actualización de campos básicos ---
			Map<String, Object> fields = new LinkedHashMap<>();

			if (grupo.getNombre() != null)
				fields.put(Grupo.COL_NOMBRE, grupo.getNombre());

			if (grupo.isSuperAdmin() != null)
				fields.put(Grupo.COL_SUPER_ADMIN, grupo.isSuperAdmin());

			if (!fields.isEmpty()) {
				update(conn, Grupo.TABLE, fields, Grupo.COL_ID + " = ?", new Object[] { id });
			}

			// --- actualización de permisos ---
			if (grupo.getPermisos() != null) {

				deleteOne(conn, Grupo.TABLE_GLOBAL_PRM, "grupoId", id);
				deleteOne(conn, Grupo.TABLE_SECTION_PRM, "grupoId", id);

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

				try {
					conn.setAutoCommit(true);
				} catch (SQLException ex) {
				}

				conn.close();
			}
		}
	}

	/**
	 * Elimina múltiples grupos a partir de una lista de identificadores.
	 *
	 * @param ids lista de identificadores de grupos
	 * @return número total de grupos eliminados
	 * @throws DaoException si ocurre un error durante la operación
	 */
	public int deleteGrupos(List<Integer> ids) {

		try (Connection conn = getDS().getConnection()) {

			return deleteAll(conn, Grupo.TABLE, Grupo.COL_ID, ids);

		} catch (SQLException e) {
			throw new DaoException("Error al eliminar los grupos seleccionados", e);
		}
	}

	/**
	 * Elimina un grupo del sistema a partir de su identificador.
	 *
	 * @param id identificador del grupo a eliminar
	 * @return {@code true} si el grupo fue eliminado correctamente
	 * @throws SQLException si ocurre un error durante la operación
	 */
	public boolean deleteGrupo(int id) throws SQLException {

		try (Connection conn = getDS().getConnection()) {

			return deleteOne(conn, Grupo.TABLE, Grupo.COL_ID, id);

		}
	}

	// ==========================================
	// MÉTODOS TOKEN
	// ==========================================

	/**
	 * Recupera todos los tokens API asociados a un usuario.
	 *
	 * <p>
	 * Este método consulta la tabla de tokens API y devuelve la lista de tokens
	 * registrados para el usuario indicado.
	 * </p>
	 *
	 * <p>
	 * Los registros obtenidos se transforman en objetos {@link ApiToken} mediante
	 * el método {@link ApiToken#mapToken(ResultSet)}.
	 * </p>
	 *
	 * @param usuarioId identificador del usuario
	 * @return lista de tokens API asociados al usuario
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<ApiToken> findTokensByUsuario(int usuarioId) {

		try {
			return findAll(SqlQueries.FIND_TOKENS_BY_USUARIOID, ps -> ps.setInt(1, usuarioId),
					rs -> mapList(rs, ApiToken::mapToken));
		} catch (SQLException e) {
			throw new DaoException("Error al listar tokens del usuario", e);
		}
	}

	/**
	 * Crea un nuevo token de acceso para un usuario.
	 *
	 * <p>
	 * El token se almacena en la tabla {@link ApiToken#TABLE} con estado activo por
	 * defecto. Este token podrá utilizarse posteriormente para autenticar
	 * peticiones a la API.
	 * </p>
	 *
	 * @param usuarioId  identificador del usuario propietario del token
	 * @param tokenValue valor del token generado
	 * @return identificador generado para el nuevo token
	 * @throws DaoException si ocurre un error durante la inserción
	 */
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

	/**
	 * Actualiza el estado de un token de API.
	 *
	 * <p>
	 * Este método permite activar o desactivar un token existente. Los tokens
	 * desactivados no podrán utilizarse para autenticación en el sistema.
	 * </p>
	 *
	 * @param tokenId identificador del token
	 * @param active  {@code true} para activar el token, {@code false} para
	 *                desactivarlo
	 * @return {@code true} si el estado del token fue actualizado correctamente,
	 *         {@code false} si no se modificó ningún registro
	 * @throws DaoException si ocurre un error durante la operación
	 */
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

	/**
	 * Recupera la sesión de un usuario a partir de un token de autenticación.
	 *
	 * <p>
	 * Este método se utiliza durante el proceso de autenticación de la API. A
	 * partir del token recibido se realiza una consulta que obtiene:
	 * </p>
	 *
	 * <ul>
	 * <li>Información del usuario asociado al token</li>
	 * <li>Información del grupo al que pertenece el usuario</li>
	 * <li>Estado del usuario (activo o no)</li>
	 * </ul>
	 *
	 * <p>
	 * Si el token es válido y el usuario pertenece a un grupo que no es
	 * superadministrador, el sistema carga además los permisos asociados al grupo:
	 * </p>
	 *
	 * <ul>
	 * <li>Permisos globales</li>
	 * <li>Permisos específicos por sección</li>
	 * </ul>
	 *
	 * <p>
	 * Los permisos se encapsulan en un {@link PermissionMap} y se asignan al objeto
	 * {@link Session}. En el caso de grupos con privilegios de superadministrador,
	 * no se cargan permisos explícitos ya que estos tienen acceso completo al
	 * sistema.
	 * </p>
	 *
	 * @param token valor del token de autenticación proporcionado por el cliente
	 * @return objeto {@link Session} con la información del usuario, grupo y
	 *         permisos asociados, o {@code null} si el token no es válido
	 * @throws DaoException si ocurre un error durante la consulta
	 */
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

	/**
	 * Recupera un permiso a partir de su identificador.
	 *
	 * <p>
	 * Este método consulta la base de datos para obtener la información completa de
	 * un permiso junto con su ámbito asociado.
	 * </p>
	 *
	 * <p>
	 * El resultado se transforma en un objeto {@link Permiso} mediante el método
	 * {@link Permiso#mapPermisoToSimpl(ResultSet)}.
	 * </p>
	 *
	 * @param id identificador del permiso
	 * @return objeto {@link Permiso} correspondiente al identificador indicado, o
	 *         {@code null} si no existe
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public Permiso findPermisoById(int id) {

		try {
			return findOne(SqlQueries.FIND_FULL_PRM_BY_ID, ps -> ps.setInt(1, id), rs -> Permiso.mapPermisoToSimpl(rs));
		} catch (SQLException e) {
			throw new DaoException("Error al buscar permiso por ID: " + id, e);
		}
	}

	/**
	 * Recupera el catálogo completo de permisos del sistema.
	 *
	 * <p>
	 * Cada permiso incluye la información del ámbito al que pertenece. Este
	 * catálogo se utiliza principalmente para la gestión de permisos en la
	 * configuración de grupos de usuarios.
	 * </p>
	 *
	 * <p>
	 * Los resultados se transforman en objetos {@link Permiso} mediante el método
	 * {@link Permiso#mapPermisoToSimpl(ResultSet)}.
	 * </p>
	 *
	 * @return lista de permisos disponibles en el sistema
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<Permiso> findAllPermisos() {

		try {
			return findAll(SqlQueries.FIND_FULL_PRM, null, rs -> mapList(rs, r -> Permiso.mapPermisoToSimpl(r)));
		} catch (SQLException e) {
			throw new DaoException("Error al recuperar el catálogo de permisos", e);
		}
	}

	/* --- PARA SESIÓN (STRINGS) --- */

	/**
	 * Recupera los nombres de los permisos globales asociados a un grupo.
	 *
	 * <p>
	 * Este método se utiliza principalmente durante la construcción de una
	 * {@link Session}, donde los permisos se representan como claves legibles en
	 * formato {@code permiso_ambito}.
	 * </p>
	 *
	 * <p>
	 * Estas claves permiten comprobar permisos de forma rápida en la capa de
	 * autorización de la aplicación.
	 * </p>
	 *
	 * @param grupoId identificador del grupo
	 * @return lista de claves de permisos globales del grupo
	 * @throws SQLException si ocurre un error durante la consulta
	 */
	private List<String> getGlobalPermissionNames(int grupoId) throws SQLException {

		return findAll(SqlQueries.FIND_PRM_GLOBAL_BY_GROUPID_NAME, ps -> ps.setInt(1, grupoId),
				rs -> mapList(rs, r -> r.getString("pkey")));
	}

	/**
	 * Recupera los permisos por sección asociados a un grupo utilizando sus nombres
	 * legibles.
	 *
	 * <p>
	 * Los permisos se organizan en un mapa donde:
	 * </p>
	 *
	 * <ul>
	 * <li>La clave representa el identificador de la sección.</li>
	 * <li>El valor es una lista de claves de permisos asociadas a esa sección.</li>
	 * </ul>
	 *
	 * <p>
	 * Este método se utiliza durante la creación de sesiones de usuario para
	 * determinar los permisos efectivos dentro de cada sección.
	 * </p>
	 *
	 * @param grupoId identificador del grupo
	 * @return mapa de permisos por sección
	 * @throws SQLException si ocurre un error durante la consulta
	 */
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

	/**
	 * Recupera los identificadores de los permisos globales asociados a un grupo.
	 *
	 * <p>
	 * A diferencia de {@link #getGlobalPermissionNames(int)}, este método devuelve
	 * los identificadores numéricos de los permisos. Se utiliza principalmente
	 * durante la gestión o edición de grupos en el sistema.
	 * </p>
	 *
	 * @param grupoId identificador del grupo
	 * @return lista de identificadores de permisos globales
	 * @throws SQLException si ocurre un error durante la consulta
	 */
	private List<Integer> getGlobalPermissionIds(int grupoId) throws SQLException {

		return findAll(SqlQueries.FIND_PRM_GLOBAL_BY_GROUPID_ID, ps -> ps.setInt(1, grupoId),
				rs -> mapList(rs, r -> r.getInt("permisoId")));
	}

	/**
	 * Recupera los permisos por sección asociados a un grupo utilizando los
	 * identificadores numéricos de los permisos.
	 *
	 * <p>
	 * El resultado se organiza en un mapa donde:
	 * </p>
	 *
	 * <ul>
	 * <li>La clave es el identificador de la sección.</li>
	 * <li>El valor es la lista de identificadores de permisos asociados.</li>
	 * </ul>
	 *
	 * <p>
	 * Este método se utiliza principalmente en operaciones de gestión de grupos
	 * (por ejemplo edición o configuración de permisos).
	 * </p>
	 *
	 * @param grupoId identificador del grupo
	 * @return mapa de permisos por sección utilizando identificadores
	 * @throws SQLException si ocurre un error durante la consulta
	 */
	private Map<Integer, List<Integer>> getSectionPermissionIds(int grupoId) throws SQLException {

		return findAll(SqlQueries.FIND_PRM_SECCN_BY_GROUPID_ID, ps -> ps.setInt(1, grupoId), rs -> {

			Map<Integer, List<Integer>> map = new HashMap<>();

			while (rs.next()) {
				map.computeIfAbsent(rs.getInt("seccionId"), k -> new ArrayList<>()).add(rs.getInt("permisoId"));
			}

			return map;
		});
	}

	/**
	 * Persiste los permisos asociados a un grupo en la base de datos.
	 *
	 * <p>
	 * Este método inserta las relaciones entre un grupo y sus permisos en las
	 * tablas correspondientes:
	 * </p>
	 *
	 * <ul>
	 * <li>{@code grupo_permiso_global} para los permisos globales.</li>
	 * <li>{@code grupo_seccion} para los permisos asociados a secciones
	 * específicas.</li>
	 * </ul>
	 *
	 * <p>
	 * Los permisos se reciben encapsulados en un {@link PermissionMap}, que
	 * contiene dos estructuras:
	 * </p>
	 *
	 * <ul>
	 * <li>Una lista de permisos globales.</li>
	 * <li>Un mapa de permisos por sección donde la clave es el identificador de la
	 * sección y el valor es la lista de permisos asociados.</li>
	 * </ul>
	 *
	 * <p>
	 * Para mejorar el rendimiento, las inserciones se realizan mediante ejecución
	 * por lotes ({@link PreparedStatement#executeBatch()}), lo que reduce el número
	 * de operaciones enviadas al servidor de base de datos.
	 * </p>
	 *
	 * <p>
	 * Este método está diseñado para ejecutarse dentro de una transacción ya
	 * iniciada, normalmente en operaciones como:
	 * </p>
	 *
	 * <ul>
	 * <li>{@link #insertGrupo(Grupo)}</li>
	 * <li>{@link #updateGrupo(int, Grupo)}</li>
	 * </ul>
	 *
	 * @param conn    conexión JDBC activa dentro de una transacción
	 * @param grupoId identificador del grupo al que se asociarán los permisos
	 * @param pMap    estructura que contiene los permisos globales y por sección
	 * @throws SQLException si ocurre un error durante la inserción de los permisos
	 */
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

	/**
	 * Recupera un ámbito a partir de su identificador.
	 *
	 * <p>
	 * Este método ejecuta una consulta sobre la tabla de ámbitos y transforma el
	 * resultado en un objeto {@link Ambito} mediante el método
	 * {@link Ambito#mapAmbito(ResultSet)}.
	 * </p>
	 *
	 * @param id identificador del ámbito
	 * @return objeto {@link Ambito} correspondiente al identificador indicado, o
	 *         {@code null} si no existe
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public Ambito findAmbitoById(int id) {
		try {
			return findOne(SqlQueries.FIND_FULL_AMBT_BY_ID, ps -> ps.setInt(1, id), rs -> Ambito.mapAmbito(rs));
		} catch (SQLException e) {
			throw new DaoException("Error al buscar ámbito por ID: " + id, e);
		}
	}

	/**
	 * Recupera todos los ámbitos registrados en el sistema.
	 *
	 * <p>
	 * Los ámbitos representan categorías funcionales que agrupan permisos dentro
	 * del sistema de autorización. Cada permiso pertenece a un ámbito específico.
	 * </p>
	 *
	 * <p>
	 * Los resultados de la consulta se transforman en objetos {@link Ambito}
	 * mediante el método {@link Ambito#mapAmbito(ResultSet)}.
	 * </p>
	 *
	 * @return lista de ámbitos disponibles en el sistema
	 * @throws DaoException si ocurre un error durante la consulta
	 */
	public List<Ambito> findAllAmbito() {
		try {
			return findAll(SqlQueries.FIND_FULL_AMBT, null, rs -> mapList(rs, r -> Ambito.mapAmbito(r)));
		} catch (SQLException e) {
			throw new DaoException("Error al recuperar el listado de ámbitos.", e);
		}
	}

	// ==========================================
	// MÉTODOS GENÉRICOS
	// ==========================================
	/**
	 * Ejecuta una consulta SQL que devuelve un único resultado.
	 *
	 * <p>
	 * Este método genérico encapsula el patrón habitual de ejecución de consultas
	 * JDBC:
	 * </p>
	 *
	 * <ol>
	 * <li>Obtención de una conexión desde el {@link DataSource}</li>
	 * <li>Preparación de la sentencia SQL</li>
	 * <li>Asignación de parámetros mediante una función "blinder"</li>
	 * <li>Ejecución de la consulta</li>
	 * <li>Mapeo del resultado mediante una función "mapper"</li>
	 * </ol>
	 *
	 * <p>
	 * El método utiliza interfaces funcionales personalizadas ({@link SQLConsumer}
	 * y {@link SQLFunction}) que permiten trabajar con expresiones lambda que
	 * pueden lanzar {@link SQLException}.
	 * </p>
	 *
	 * <p>
	 * Si la consulta no devuelve resultados, el método retorna {@code null}.
	 * </p>
	 *
	 * @param <R>     tipo del objeto que se devolverá tras mapear el resultado
	 * @param sql     consulta SQL a ejecutar
	 * @param blinder función encargada de asignar los parámetros del
	 *                {@link PreparedStatement}; puede ser {@code null}
	 * @param mapper  función encargada de transformar el {@link ResultSet} en el
	 *                objeto de retorno
	 * @return objeto mapeado desde el {@link ResultSet} o {@code null} si no se
	 *         encuentran resultados
	 * @throws SQLException si ocurre un error durante la ejecución de la consulta
	 */
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

	/**
	 * Ejecuta una consulta SQL que devuelve múltiples resultados.
	 *
	 * <p>
	 * Este método es una variante genérica utilizada para consultas que devuelven
	 * un conjunto de resultados completo. La transformación del {@link ResultSet}
	 * se delega a la función {@code mapper}, que es responsable de recorrer el
	 * conjunto de resultados y construir el objeto final (normalmente una
	 * colección).
	 * </p>
	 *
	 * <p>
	 * A diferencia de {@link #findOne(String, SQLConsumer, SQLFunction)}, este
	 * método no realiza una llamada a {@code rs.next()} previamente, ya que el
	 * control del recorrido del {@link ResultSet} se delega completamente al
	 * mapper.
	 * </p>
	 *
	 * @param <R>     tipo del objeto que se devolverá tras mapear el resultado
	 * @param sql     consulta SQL a ejecutar
	 * @param blinder función encargada de asignar los parámetros del
	 *                {@link PreparedStatement}; puede ser {@code null}
	 * @param mapper  función encargada de transformar el {@link ResultSet} en el
	 *                objeto de retorno
	 * @return objeto resultante del mapeo del {@link ResultSet}
	 * @throws SQLException si ocurre un error durante la ejecución de la consulta
	 */
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

	/**
	 * Ejecuta una consulta SQL simple sin parámetros y delega el procesamiento del
	 * {@link ResultSet} a una función extractora.
	 *
	 * <p>
	 * Este método se utiliza para consultas donde no es necesario enlazar
	 * parámetros en el {@link PreparedStatement}. El resultado de la consulta se
	 * pasa directamente a la función {@code extractor}, que es responsable de
	 * procesar el {@link ResultSet} y construir el objeto de retorno.
	 * </p>
	 *
	 * <p>
	 * El uso de {@link SQLFunction} permite trabajar con expresiones lambda que
	 * pueden lanzar {@link SQLException}, facilitando la implementación de lógica
	 * de extracción de datos.
	 * </p>
	 *
	 * @param <R>       tipo del objeto resultante de la consulta
	 * @param sql       sentencia SQL a ejecutar
	 * @param extractor función encargada de procesar el {@link ResultSet} y
	 *                  construir el resultado final
	 * @return objeto resultante del procesamiento del {@link ResultSet}
	 * @throws SQLException si ocurre un error durante la ejecución de la consulta
	 */
	private <R> R query(String sql, SQLFunction<ResultSet, R> extractor) throws SQLException {
		try (Connection conn = getDS().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			return extractor.apply(rs);
		}
	}

	/**
	 * Inserta un registro en la tabla especificada utilizando un conjunto dinámico
	 * de columnas y valores.
	 *
	 * <p>
	 * El método construye automáticamente la sentencia SQL {@code INSERT} a partir
	 * del contenido del {@link Map} proporcionado, donde:
	 * </p>
	 *
	 * <ul>
	 * <li>La clave del mapa representa el nombre de la columna.</li>
	 * <li>El valor del mapa representa el valor a insertar.</li>
	 * </ul>
	 *
	 * <p>
	 * Tras ejecutar la inserción, se obtiene la clave primaria generada
	 * automáticamente por la base de datos utilizando
	 * {@link Statement#RETURN_GENERATED_KEYS}.
	 * </p>
	 *
	 * <p>
	 * Este método está pensado para ser utilizado dentro de una transacción
	 * existente, por lo que requiere una {@link Connection} ya abierta.
	 * </p>
	 *
	 * @param conn   conexión JDBC activa
	 * @param table  nombre de la tabla donde se realizará la inserción
	 * @param values mapa de columnas y valores a insertar
	 * @return identificador generado para el nuevo registro
	 * @throws SQLException si no hay valores para insertar, si ocurre un error
	 *                      durante la ejecución del {@code INSERT} o si no se
	 *                      genera una clave primaria válida
	 */
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

	/**
	 * Inserta múltiples registros en una tabla utilizando ejecución por lotes
	 * (batch processing).
	 *
	 * <p>
	 * Este método permite realizar inserciones masivas de forma eficiente mediante
	 * {@link PreparedStatement#addBatch()} y
	 * {@link PreparedStatement#executeBatch()}, reduciendo el número de operaciones
	 * enviadas al servidor de base de datos.
	 * </p>
	 *
	 * <p>
	 * Cada fila se representa mediante un {@link Map} donde:
	 * </p>
	 *
	 * <ul>
	 * <li>La clave representa el nombre de la columna.</li>
	 * <li>El valor representa el valor que se insertará en dicha columna.</li>
	 * </ul>
	 *
	 * <p>
	 * La estructura de columnas se determina a partir de la primera fila de la
	 * lista. Todas las filas deben contener exactamente el mismo conjunto de
	 * columnas; de lo contrario se lanzará una {@link SQLException}.
	 * </p>
	 *
	 * <p>
	 * Este método está diseñado para ejecutarse dentro de una transacción
	 * existente, por lo que requiere una {@link Connection} previamente abierta.
	 * </p>
	 *
	 * @param conn  conexión JDBC activa
	 * @param table nombre de la tabla donde se insertarán los registros
	 * @param rows  lista de filas a insertar representadas como mapas columna-valor
	 * @return array con el número de filas afectadas por cada operación del batch
	 * @throws SQLException si ocurre un error durante la ejecución del batch o si
	 *                      las columnas de las filas no son consistentes
	 */
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

	/**
	 * Actualiza registros en una tabla utilizando un conjunto dinámico de columnas
	 * y una condición {@code WHERE}.
	 *
	 * <p>
	 * El método construye dinámicamente la cláusula {@code SET} a partir de las
	 * entradas del {@link Map} {@code values}, donde cada clave representa una
	 * columna y su valor el nuevo valor que se asignará.
	 * </p>
	 *
	 * <p>
	 * La condición {@code WHERE} se proporciona mediante la cadena
	 * {@code whereClause} y los parámetros asociados en {@code whereParams}.
	 * </p>
	 *
	 * <p>
	 * Este método está diseñado para ejecutarse dentro de una transacción
	 * existente, por lo que requiere una {@link Connection} previamente abierta.
	 * </p>
	 *
	 * @param conn        conexión JDBC activa
	 * @param table       nombre de la tabla que se actualizará
	 * @param values      mapa de columnas y valores a actualizar
	 * @param whereClause condición {@code WHERE} de la sentencia SQL
	 * @param whereParams parámetros asociados a la condición {@code WHERE}
	 * @return {@code true} si al menos un registro fue actualizado, {@code false}
	 *         en caso contrario
	 * @throws SQLException si no se proporcionan valores para actualizar o si
	 *                      ocurre un error durante la ejecución de la sentencia SQL
	 */
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

	/**
	 * Elimina un único registro de una tabla en función de su identificador.
	 *
	 * <p>
	 * Este método construye dinámicamente una sentencia {@code DELETE} utilizando
	 * el nombre de la tabla y la columna identificadora proporcionados.
	 * </p>
	 *
	 * <p>
	 * Está diseñado para ejecutarse dentro de una transacción existente, por lo que
	 * requiere una {@link Connection} previamente abierta.
	 * </p>
	 *
	 * @param conn    conexión JDBC activa
	 * @param table   nombre de la tabla de la que se eliminará el registro
	 * @param idColum nombre de la columna identificadora (clave primaria)
	 * @param id      valor del identificador del registro a eliminar
	 * @return {@code true} si se eliminó al menos un registro, {@code false} si no
	 *         se encontró ningún registro con ese id
	 * @throws SQLException si ocurre un error durante la ejecución de la sentencia
	 *                      SQL
	 */
	private boolean deleteOne(Connection conn, String table, String idColum, int id) throws SQLException {
		String sql = String.format("DELETE FROM %s WHERE %s = ?", table, idColum);

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Elimina múltiples registros de una tabla utilizando una lista de
	 * identificadores.
	 *
	 * <p>
	 * El método genera dinámicamente una cláusula {@code IN} en la sentencia
	 * {@code DELETE} para eliminar todos los registros cuyo identificador coincida
	 * con alguno de los valores proporcionados.
	 * </p>
	 *
	 * <p>
	 * Si la lista de identificadores es {@code null} o está vacía, el método no
	 * realiza ninguna operación y devuelve {@code 0}.
	 * </p>
	 *
	 * <p>
	 * Este método debe ejecutarse dentro de una transacción existente, por lo que
	 * requiere una {@link Connection} activa.
	 * </p>
	 *
	 * @param conn    conexión JDBC activa
	 * @param table   nombre de la tabla de la que se eliminarán los registros
	 * @param idColum nombre de la columna identificadora
	 * @param ids     lista de identificadores de los registros a eliminar
	 * @return número de registros eliminados
	 * @throws SQLException si ocurre un error durante la ejecución de la sentencia
	 *                      SQL
	 */
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

	/**
	 * Convierte un {@link ResultSet} en una lista de objetos utilizando una función
	 * de mapeo.
	 *
	 * <p>
	 * Este método recorre todas las filas del {@link ResultSet} y aplica la función
	 * {@code mapper} para transformar cada fila en un objeto del tipo especificado.
	 * </p>
	 *
	 * <p>
	 * Se utiliza comúnmente en combinación con métodos genéricos de consulta como
	 * {@code findAll}, permitiendo desacoplar la lógica de acceso a datos de la
	 * lógica de transformación de resultados.
	 * </p>
	 *
	 * @param <T>    tipo del objeto resultante del mapeo
	 * @param rs     conjunto de resultados obtenido de la consulta SQL
	 * @param mapper función encargada de transformar cada fila del
	 *               {@link ResultSet} en un objeto del tipo {@code T}
	 * @return lista de objetos resultantes del mapeo
	 * @throws SQLException si ocurre un error durante la lectura del
	 *                      {@link ResultSet}
	 */
	private <T> List<T> mapList(ResultSet rs, SQLFunction<ResultSet, T> mapper) throws SQLException {
		List<T> list = new ArrayList<>();
		while (rs.next()) {
			list.add(mapper.apply(rs));
		}
		return list;
	}
}