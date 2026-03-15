package local.alejandrogb.metricsservers.api.services.login;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotAuthorizedException;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Session;

/**
 * Servicio encargado de gestionar la obtención de sesiones de usuario a partir
 * de tokens de autenticación.
 *
 * <p>
 * Este servicio forma parte del flujo de autenticación de la API y actúa como
 * intermediario entre los recursos REST y la capa de persistencia representada
 * por {@link DaoApi}.
 * </p>
 *
 * <p>
 * Su responsabilidad principal es:
 * </p>
 *
 * <ul>
 * <li>Validar la presencia del token proporcionado por el cliente.</li>
 * <li>Recuperar la sesión asociada al token desde la base de datos.</li>
 * <li>Garantizar que la sesión es válida antes de devolverla.</li>
 * </ul>
 *
 * <p>
 * La verificación inicial del token suele realizarse previamente en un filtro
 * de autenticación de la API. Este servicio actúa como segunda capa de
 * seguridad para evitar condiciones de carrera en las que el token pudiera
 * haber sido desactivado después de la validación inicial.
 * </p>
 */
public class LoginService {

	/**
	 * Instancia del DAO utilizada para acceder a la capa de persistencia.
	 */
	private final DaoApi dao = DaoApi.getInstance();

	/**
	 * Obtiene la sesión asociada a un token de autenticación.
	 *
	 * <p>
	 * Si el token no se proporciona o es inválido, se lanzará una excepción de
	 * validación. En caso de que el token exista pero la sesión ya no sea válida
	 * (por ejemplo, porque el token fue desactivado), se lanzará una excepción de
	 * autorización.
	 * </p>
	 *
	 * @param token valor del token de autenticación
	 * @return objeto {@link Session} con la información del usuario, grupo y
	 *         permisos asociados
	 * @throws ValidationException    si el token no se proporciona
	 * @throws NotAuthorizedException si el token no corresponde a una sesión válida
	 */
	public Session getSession(String token) {

		if (token == null || token.isBlank())
			throw new ValidationException("Token no proporcionado");

		Session session = dao.getSessionByToken(token);

		if (session == null)
			// El filtro de autenticación ya validó el token, pero si llegamos aquí
			// significa que el token se desactivó entre el filtro y el servicio
			throw new NotAuthorizedException("Sesión no válida o expirada");

		return session;
	}
}