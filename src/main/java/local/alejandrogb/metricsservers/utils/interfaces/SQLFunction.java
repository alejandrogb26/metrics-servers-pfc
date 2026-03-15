package local.alejandrogb.metricsservers.utils.interfaces;

import java.sql.SQLException;

/**
 * Interfaz funcional similar a {@link java.util.function.Function}, pero
 * diseñada para permitir que la operación pueda lanzar una
 * {@link SQLException}.
 *
 * <p>
 * Esta interfaz se utiliza principalmente en operaciones relacionadas con
 * acceso a bases de datos mediante JDBC, donde una función transforma un objeto
 * de entrada en un resultado y puede producir una excepción SQL durante el
 * proceso.
 * </p>
 *
 * <p>
 * Resulta especialmente útil para trabajar con expresiones lambda en métodos
 * que ejecutan consultas SQL y devuelven resultados, evitando tener que
 * capturar o envolver manualmente las excepciones dentro de cada lambda.
 * </p>
 *
 * <p>
 * Ejemplo de uso:
 * </p>
 *
 * <pre>
 * Usuario usuario = execute(connection -> {
 * 	PreparedStatement stmt = connection.prepareStatement("SELECT * FROM usuarios WHERE id = ?");
 * 	stmt.setInt(1, id);
 * 	ResultSet rs = stmt.executeQuery();
 * 	return mapUsuario(rs);
 * });
 * </pre>
 *
 * @param <T> tipo del parámetro de entrada de la función
 * @param <R> tipo del resultado producido por la función
 *
 * @author alejandrogb
 */
@FunctionalInterface
public interface SQLFunction<T, R> {

	/**
	 * Aplica una función al objeto proporcionado y devuelve un resultado.
	 *
	 * <p>
	 * Este método puede lanzar una {@link SQLException}, lo que permite su uso
	 * directo en operaciones JDBC dentro de expresiones lambda.
	 * </p>
	 *
	 * @param t objeto de entrada sobre el que se aplica la función
	 * @return resultado producido por la función
	 * @throws SQLException si ocurre un error durante la operación SQL
	 */
	R apply(T t) throws SQLException;
}