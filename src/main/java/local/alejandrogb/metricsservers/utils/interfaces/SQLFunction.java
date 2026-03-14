package local.alejandrogb.metricsservers.utils.interfaces;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLFunction<T, R> {

	R apply(T t) throws SQLException;
}
