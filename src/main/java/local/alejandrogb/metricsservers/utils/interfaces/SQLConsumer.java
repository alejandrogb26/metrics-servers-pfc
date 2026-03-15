package local.alejandrogb.metricsservers.utils.interfaces;

import java.sql.SQLException;

/**
 * Interfaz funcional similar a {@link java.util.function.Consumer}, pero
 * diseñada para permitir que la operación pueda lanzar una
 * {@link SQLException}.
 *
 * <p>
 * Se utiliza principalmente en operaciones relacionadas con acceso a base de
 * datos mediante JDBC, donde es necesario ejecutar lógica que puede producir
 * excepciones SQL dentro de expresiones lambda o referencias a métodos.
 * </p>
 *
 * <p>
 * Esto resulta útil cuando se desea encapsular operaciones que manipulan
 * objetos JDBC como {@code Connection}, {@code PreparedStatement} o
 * {@code ResultSet} sin tener que manejar manualmente las excepciones dentro de
 * cada lambda.
 * </p>
 *
 * <p>
 * Ejemplo de uso:
 * </p>
 *
 * <pre>
 * execute(connection -> {
 * 	PreparedStatement stmt = connection.prepareStatement("SELECT * FROM usuarios");
 * 	ResultSet rs = stmt.executeQuery();
 * });
 * </pre>
 *
 * @param <T> tipo del objeto que será consumido por la operación
 *
 * @author alejandrogb
 */
@FunctionalInterface
public interface SQLConsumer<T> {

	/**
	 * Realiza una operación sobre el objeto proporcionado.
	 *
	 * <p>
	 * Este método puede lanzar una {@link SQLException}, lo que permite su uso en
	 * operaciones JDBC sin necesidad de envolver la excepción en una
	 * {@link RuntimeException}.
	 * </p>
	 *
	 * @param t objeto sobre el que se ejecuta la operación
	 * @throws SQLException si ocurre un error durante la operación SQL
	 */
	void accept(T t) throws SQLException;
}