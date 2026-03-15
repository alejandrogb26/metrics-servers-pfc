package local.alejandrogb.metricsservers.utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Utilidad para obtener una instancia de {@link DataSource} configurada en el
 * contenedor de aplicaciones mediante JNDI.
 *
 * <p>
 * Esta clase intenta localizar el recurso {@code jdbc/MariaDBDS} utilizando
 * diferentes rutas JNDI compatibles con entornos basados en TomEE/OpenEJB. El
 * objetivo es aumentar la compatibilidad entre distintas configuraciones de
 * despliegue del servidor.
 * </p>
 *
 * <p>
 * La instancia del {@link DataSource} se inicializa una única vez durante la
 * carga de la clase y se reutiliza posteriormente (patrón Singleton implícito).
 * </p>
 *
 * <p>
 * Orden de búsqueda del recurso:
 * </p>
 * <ol>
 * <li>{@code java:comp/env/jdbc/MariaDBDS} – Ruta estándar definida mediante
 * <code>resource-ref</code> en <code>web.xml</code>.</li>
 * <li>{@code java:openejb/Resource/jdbc/MariaDBDS} – Ruta global de
 * TomEE/OpenEJB.</li>
 * <li>{@code openejb/Resource/jdbc/MariaDBDS} – Ruta directa utilizada en
 * algunas configuraciones de TomEE.</li>
 * </ol>
 *
 * <p>
 * Si el recurso no puede resolverse tras los distintos intentos, se lanzará una
 * {@link RuntimeException}.
 * </p>
 *
 * @author alejandrogb
 */
public class GetDataSource {

	/**
	 * Instancia única del {@link DataSource} obtenida mediante JNDI. Se inicializa
	 * en el momento de carga de la clase.
	 */
	private static final DataSource DS = init();

	/**
	 * Inicializa el {@link DataSource} buscando el recurso JNDI en distintas
	 * ubicaciones compatibles con configuraciones habituales de TomEE/OpenEJB.
	 *
	 * @return DataSource configurado en el contenedor de aplicaciones
	 * @throws RuntimeException si no se encuentra el recurso JNDI tras varios
	 *                          intentos de búsqueda
	 */
	private static DataSource init() {
		try {
			InitialContext ctx = new InitialContext();

			// Intento 1: Estándar de aplicación (requiere resource-ref en web.xml)
			try {
				return (DataSource) ctx.lookup("java:comp/env/jdbc/MariaDBDS");
			} catch (NamingException e1) {
				// Intento 2: Ruta global de TomEE/OpenEJB
				try {
					return (DataSource) ctx.lookup("java:openejb/Resource/jdbc/MariaDBDS");
				} catch (NamingException e2) {
					// Intento 3: Ruta directa (la que suele funcionar si tomee.xml está bien)
					return (DataSource) ctx.lookup("openejb/Resource/jdbc/MariaDBDS");
				}
			}
		} catch (NamingException e) {
			throw new RuntimeException("DataSource no encontrado tras varios intentos", e);
		}
	}

	/**
	 * Devuelve la instancia única del {@link DataSource} de la aplicación.
	 *
	 * <p>
	 * Este método proporciona acceso centralizado al pool de conexiones configurado
	 * en el contenedor de aplicaciones.
	 * </p>
	 *
	 * @return instancia de {@link DataSource} previamente inicializada
	 */
	public static DataSource getDataSource() {
		return DS;
	}
}