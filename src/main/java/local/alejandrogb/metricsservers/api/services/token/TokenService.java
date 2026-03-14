package local.alejandrogb.metricsservers.api.services.token;

import java.util.List;
import java.util.UUID;

import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.usuario.ApiToken;

public class TokenService {
	private final DaoApi dao = DaoApi.getInstance();

	public String generateNewToken(int usuarioId) {
		// Generamos un token aleatorio seguro
		String secureToken = UUID.randomUUID().toString().replace("-", "");
		dao.createToken(usuarioId, secureToken);
		return secureToken;
	}

	public List<ApiToken> getUsuarioTokens(int usuarioId) {
		return dao.findTokensByUsuario(usuarioId);
	}

	public boolean revokeToken(int tokenId) {
		return dao.updateTokenStatus(tokenId, false);
	}

	public boolean activateToken(int tokenId) {
		return dao.updateTokenStatus(tokenId, true);
	}
}
