package local.alejandrogb.metricsservers.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Session {
	private String username;
	private String displayName;
	private String email;
	private Grupo grupo;
	private PermissionMap<String> permisos;
	@JsonIgnore
	private String fotoPerfil;
	private String urlFoto;

	public Session() {
	}

	public Session(String username, String displayName, String email, Grupo grupo, PermissionMap<String> permisos,
			String fotoPerfil, String urlFoto) {
		this.username = username;
		this.displayName = displayName;
		this.email = email;
		this.grupo = grupo;
		this.permisos = permisos;
		this.fotoPerfil = fotoPerfil;
		this.urlFoto = urlFoto;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@JsonIgnore
	public String getFotoPerfil() {
		return fotoPerfil;
	}

	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}

	@Override
	public String toString() {
		return "Session [username=" + username + ", displayName=" + displayName + ", email=" + email + ", grupo="
				+ grupo + ", permisos=" + permisos + ", fotoPerfil=" + fotoPerfil + ", urlFoto=" + urlFoto + "]";
	}
}