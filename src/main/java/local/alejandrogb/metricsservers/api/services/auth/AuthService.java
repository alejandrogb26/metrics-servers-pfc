package local.alejandrogb.metricsservers.api.services.auth;

import java.util.List;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotAuthorizedException;
import local.alejandrogb.metricsservers.api.tokens.JwtService;
import local.alejandrogb.metricsservers.dao.DaoApi;
import local.alejandrogb.metricsservers.models.Grupo;
import local.alejandrogb.metricsservers.models.Session;
import local.alejandrogb.metricsservers.api.services.minio.MinioService;
import local.alejandrogb.metricsservers.models.login.LoginRequest;
import local.alejandrogb.metricsservers.models.login.LoginResponse;
import local.alejandrogb.metricsservers.models.usuario.UsuarioApp;

public class AuthService {

	private final LdapAuthService ldapAuthService = new LdapAuthService();
	private final DaoApi dao = DaoApi.getInstance();
	private final JwtService jwtService = new JwtService();
	private final MinioService minioService = new MinioService();

	public LoginResponse login(LoginRequest request) {
		if (request == null || isBlank(request.username()) || isBlank(request.password())) {
			throw new ValidationException("Username y password son obligatorios");
		}

		// Paso 3-4: autenticar contra AD y obtener datos del usuario
		LdapAuthService.AdUser adUser = ldapAuthService.authenticate(request.username(), request.password());

		if (adUser == null) {
			throw new NotAuthorizedException("Credenciales inválidas");
		}

		// Paso 5: cruzar grupos del AD con la BD
		Grupo grupo = resolveGrupo(adUser.memberOf());
		if (grupo == null) {
			throw new NotAuthorizedException("El usuario no pertenece a ningún grupo autorizado");
		}

		// Paso 7: sincronizar perfil local en usuarios_app
		syncUsuarioApp(adUser);

		// Paso 6: construir sesión (permisos + foto)
		Session session = dao.buildSessionFromAdUser(adUser.samAccountName(), adUser.displayName(), adUser.mail(),
				grupo);

		// Resolver URL de la foto (MinioService no pertenece a la capa DAO)
		session.setUrlFoto(minioService.getUrlImagen(MinioService.BUCKET_USUARIOS, session.getFotoPerfil()));

		// Paso 8: generar JWT
		String token = jwtService.generateToken(adUser.samAccountName(), adUser.displayName(), adUser.mail(),
				grupo.getId(), grupo.isSuperAdmin());

		// Paso 9: devolver token + sesión
		return new LoginResponse(token, "Bearer", 28800, session);
	}

	// ── helpers ───────────────────────────────────────────────────────────

	private Grupo resolveGrupo(List<String> dns) {
		if (dns == null || dns.isEmpty()) {
			return null;
		}
		return dao.getGrupoByAnyDn(dns);
	}

	/**
	 * Sincroniza el perfil local del usuario en la tabla {@code usuarios_app}.
	 *
	 * <ul>
	 * <li>Si no existe → inserta un registro nuevo (sin foto de perfil).</li>
	 * <li>Si existe → no sobrescribe la foto; en futuras iteraciones aquí se
	 * podrían sincronizar otros campos que vengan del AD.</li>
	 * </ul>
	 */
	private void syncUsuarioApp(LdapAuthService.AdUser adUser) {
		UsuarioApp existing = dao.getUsuarioAppByUsername(adUser.samAccountName());

		if (existing == null) {
			UsuarioApp nuevo = new UsuarioApp();
			nuevo.setUsername(adUser.samAccountName());
			nuevo.setFotoPerfil(null); // sin foto inicial
			dao.insertUsuarioApp(nuevo);
		}
		// Si ya existe, no hay campos de AD que sincronizar en esta versión.
		// La foto de perfil se gestiona por separado desde el endpoint de upload.
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}