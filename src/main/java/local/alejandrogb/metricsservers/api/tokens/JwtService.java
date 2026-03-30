package local.alejandrogb.metricsservers.api.tokens;

import java.util.Date;

import io.jsonwebtoken.Jwts;

public class JwtService {
	public String generateToken(String username, String displayName, String mail, int grupoId, boolean superAdmin) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + JwtConfig.JWT_EXPIRATION_MS);

		return Jwts.builder().subject(username).claim("username", username).claim("displayName", displayName)
				.claim("mail", mail).claim("grupoId", grupoId).claim("superadmin", superAdmin).issuedAt(now)
				.expiration(exp).signWith(JwtConfig.getSigningKey()).compact();
	}
}
