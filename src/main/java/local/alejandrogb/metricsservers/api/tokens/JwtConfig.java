package local.alejandrogb.metricsservers.api.tokens;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;
import local.alejandrogb.metricsservers.utils.EnvConfig;

public class JwtConfig {
	private static final String JWT_SECRET = EnvConfig.JWT_SECRET;

	public static final long JWT_EXPIRATION_MS = 1000L * 60 * 60 * 8;

	private JwtConfig() {
	}

	public static SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
	}
}
