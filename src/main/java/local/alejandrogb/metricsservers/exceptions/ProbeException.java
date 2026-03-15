package local.alejandrogb.metricsservers.exceptions;

/**
 * Excepción específica para errores ocurridos durante el proceso de
 * obtención de información de servidores remotos.
 *
 * <p>
 * Esta excepción se utiliza en la capa de servicios encargada de
 * consultar servidores mediante conexiones SSH, concretamente en
 * {@code ServidorProbeService}. Permite encapsular errores producidos
 * durante operaciones de red, autenticación o ejecución de comandos
 * remotos.
 * </p>
 *
 * <p>
 * Su objetivo es desacoplar los errores técnicos generados por la
 * librería SSH o por problemas de comunicación de red de las capas
 * superiores de la aplicación.
 * </p>
 *
 * <p>
 * Al extender {@link RuntimeException}, esta excepción puede propagarse
 * libremente hasta ser capturada por los mecanismos globales de manejo
 * de errores de la API (por ejemplo, un {@code ExceptionMapper}).
 * </p>
 */
public class ProbeException extends RuntimeException {

	/**
	 * Identificador de versión para la serialización.
	 */
	private static final long serialVersionUID = 1557490670333451322L;

	/**
	 * Crea una nueva excepción {@link ProbeException}.
	 *
	 * @param msg mensaje descriptivo del error ocurrido
	 * @param cause excepción original que provocó el fallo
	 */
	public ProbeException(String msg, Throwable cause) {
		super(msg, cause);
	}
}