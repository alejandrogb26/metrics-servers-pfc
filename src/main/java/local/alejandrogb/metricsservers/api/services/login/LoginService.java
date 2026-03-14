package local.alejandrogb.metricsservers.api.services.login;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotAuthorizedException;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Session;

public class LoginService {
	private final DaoApi dao = DaoApi.getInstance();

	public Session getSession(String token) {
		if (token == null || token.isBlank())
			throw new ValidationException("Token no proporcionado");

		Session session = dao.getSessionByToken(token);

		if (session == null)
			// El filter ya validó el token, pero si llegamos aquí y es null,
			// es que el token se desactivó justo entre el filtro y el servicio
			throw new NotAuthorizedException("Sesión no válida o expirada");

		return session;
	}
}