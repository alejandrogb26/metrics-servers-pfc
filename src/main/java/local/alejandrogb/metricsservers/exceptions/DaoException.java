package local.alejandrogb.metricsservers.exceptions;

/**
 * Excepción específica de la capa de acceso a datos (DAO).
 *
 * <p>
 * Esta excepción se utiliza para encapsular errores producidos durante
 * operaciones de persistencia, como consultas SQL, inserciones, actualizaciones
 * o eliminaciones en la base de datos.
 * </p>
 *
 * <p>
 * Su propósito principal es desacoplar las excepciones técnicas generadas por
 * la infraestructura (por ejemplo {@link java.sql.SQLException}) de las capas
 * superiores de la aplicación. De esta forma, el resto del sistema no depende
 * directamente de detalles específicos del motor de base de datos o de la API
 * JDBC.
 * </p>
 *
 * <p>
 * Normalmente esta excepción es capturada por un {@code ExceptionMapper} global
 * que la transforma en una respuesta de error adecuada para la API.
 * </p>
 *
 * <p>
 * Al extender {@link RuntimeException}, no requiere ser declarada en la firma
 * de los métodos, lo que simplifica la propagación de errores en la capa de
 * persistencia.
 * </p>
 */
public class DaoException extends RuntimeException {

	/**
	 * Identificador de versión para la serialización.
	 */
	private static final long serialVersionUID = -5233773572602699648L;

	public DaoException(String msg) {
		super(msg);
	}

	/**
	 * Crea una nueva excepción de tipo {@link DaoException}.
	 *
	 * @param msg   mensaje descriptivo del error ocurrido
	 * @param cause excepción original que provocó el fallo
	 */
	public DaoException(String msg, Throwable cause) {
		super(msg, cause);
	}
}