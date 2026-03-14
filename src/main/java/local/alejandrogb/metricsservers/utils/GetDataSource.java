package local.alejandrogb.metricsservers.utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class GetDataSource {

	private static final DataSource DS = init();

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

	public static DataSource getDataSource() {
		return DS;
	}
}
