package local.alejandrogb.metricsservers.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ValidationException;
import local.alejandrogb.metricsservers.models.usuario.UsuarioAuth;

@Schema(name = "Session", description = "Información de sesión del usuario autenticado, incluyendo sus datos básicos, grupo y permisos")
public class Session {
	@Schema(description = "Datos básicos del usuario autenticado")
	private UsuarioAuth usuario;

	@Schema(description = "Grupo al que pertenece el usuario")
	private Grupo grupo;

	@Schema(description = "Mapa de permisos del usuario (globales y por sección)")
	private PermissionMap<String> permisos;

	public Session() {
	}

	public Session(UsuarioAuth usuario, Grupo grupo, PermissionMap<String> permisos) {
		super();
		this.usuario = usuario;
		this.grupo = grupo;
		this.permisos = permisos;
	}

	public UsuarioAuth getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioAuth usuario) {
		this.usuario = usuario;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	public PermissionMap<String> getPermisos() {
		return permisos;
	}

	public void setPermisos(PermissionMap<String> permisos) {
		this.permisos = permisos;
	}

	public static Session mapSession(ResultSet rs) {
		try {
			if (!rs.getBoolean("active"))
				throw new ValidationException("La cuenta de usuario está desactivada.");

			return new Session(UsuarioAuth.mapUsuarioAuth(rs), Grupo.mapGrupoBySession(rs),
					new PermissionMap<String>());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "Session [usuario=" + usuario + ", grupo=" + grupo + ", permisos=" + permisos + "]";
	}
}
