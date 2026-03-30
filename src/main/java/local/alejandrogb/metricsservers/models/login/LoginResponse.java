package local.alejandrogb.metricsservers.models.login;

import local.alejandrogb.metricsservers.models.Session;

public record LoginResponse(String token, String tokenType, long expiresIn, Session session) {

}
