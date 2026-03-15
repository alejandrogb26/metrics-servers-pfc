package local.alejandrogb.metricsservers.api.services.token;

import java.util.List;
import java.util.UUID;

import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.usuario.ApiToken;

/**
 * Servicio encargado de gestionar los tokens de acceso a la API.
 *
 * <p>
 * Este servicio proporciona las operaciones necesarias para administrar el
 * ciclo de vida de los tokens asociados a los usuarios del sistema. Los tokens
 * permiten autenticar peticiones a la API sin necesidad de utilizar
 * credenciales tradicionales en cada solicitud.
 * </p>
 *
 * <p>
 * Las principales responsabilidades de este servicio son:
 * </p>
 *
 * <ul>
 * <li>Generar nuevos tokens de acceso para usuarios.</li>
 * <li>Consultar los tokens asociados a un usuario.</li>
 * <li>Revocar tokens existentes.</li>
 * <li>Reactivar tokens previamente desactivados.</li>
 * </ul>
 *
 * <p>
 * La persistencia de los tokens se delega en {@link DaoApi}, mientras que este
 * servicio se encarga de generar los valores seguros de los tokens y de
 * orquestar las operaciones de negocio relacionadas.
 * </p>
 */
public class TokenService {

	/**
	 * Instancia del DAO utilizada para acceder a la capa de persistencia.
	 */
	private final DaoApi dao = DaoApi.getInstance();

	/**
	 * Genera un nuevo token de acceso para un usuario.
	 *
	 * <p>
	 * El token se genera utilizando {@link UUID} para garantizar un valor
	 * suficientemente aleatorio y único. El UUID se transforma eliminando los
	 * guiones para obtener una cadena compacta que se almacena en la base de datos.
	 * </p>
	 *
	 * <p>
	 * Una vez generado, el token se persiste mediante {@link DaoApi} y se devuelve
	 * al cliente para su uso en futuras solicitudes a la API.
	 * </p>
	 *
	 * @param usuarioId identificador del usuario al que se asociará el token
	 * @return valor del token generado
	 */
	public String generateNewToken(int usuarioId) {

		// Generación de token aleatorio
		String secureToken = UUID.randomUUID().toString().replace("-", "");

		dao.createToken(usuarioId, secureToken);

		return secureToken;
	}

	/**
	 * Recupera todos los tokens asociados a un usuario.
	 *
	 * @param usuarioId identificador del usuario
	 * @return lista de tokens pertenecientes al usuario
	 */
	public List<ApiToken> getUsuarioTokens(int usuarioId) {
		return dao.findTokensByUsuario(usuarioId);
	}

	/**
	 * Revoca un token existente.
	 *
	 * <p>
	 * La revocación consiste en desactivar el token en la base de datos, impidiendo
	 * su uso en futuras solicitudes de autenticación.
	 * </p>
	 *
	 * @param tokenId identificador del token
	 * @return {@code true} si el token fue desactivado correctamente
	 */
	public boolean revokeToken(int tokenId) {
		return dao.updateTokenStatus(tokenId, false);
	}

	/**
	 * Reactiva un token previamente desactivado.
	 *
	 * <p>
	 * Una vez reactivado, el token vuelve a ser válido para autenticación en la
	 * API.
	 * </p>
	 *
	 * @param tokenId identificador del token
	 * @return {@code true} si el token fue activado correctamente
	 */
	public boolean activateToken(int tokenId) {
		return dao.updateTokenStatus(tokenId, true);
	}
}