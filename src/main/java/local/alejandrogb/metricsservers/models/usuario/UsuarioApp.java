package local.alejandrogb.metricsservers.models.usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa el perfil local de un usuario autenticado via Active Directory.
 *
 * <p>
 * Esta tabla actúa como extensión local del usuario AD: almacena únicamente los
 * datos propios de la aplicación (foto de perfil, etc.) que no existen en el
 * directorio. El campo {@code adObjectId} se usa opcionalmente para vincular
 * con el {@code objectGUID} del AD; si no está disponible, la clave de búsqueda
 * es el {@code username} (sAMAccountName).
 * </p>
 */
public class UsuarioApp {

	public static final String TABLE = "usuarios_app";
	public static final String COL_ID = "id";
	public static final String COL_AD_OBJECT_ID = "adObjectId";
	public static final String COL_USERNAME = "username";
	public static final String COL_FOTO_PERFIL = "fotoPerfil";

	private int id;
	private String adObjectId;
	private String username;
	private String fotoPerfil;

	public UsuarioApp() {
	}

	public UsuarioApp(int id, String adObjectId, String username, String fotoPerfil) {
		this.id = id;
		this.adObjectId = adObjectId;
		this.username = username;
		this.fotoPerfil = fotoPerfil;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdObjectId() {
		return adObjectId;
	}

	public void setAdObjectId(String adObjectId) {
		this.adObjectId = adObjectId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFotoPerfil() {
		return fotoPerfil;
	}

	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}

	public static UsuarioApp mapUsuarioApp(ResultSet rs) throws SQLException {
		return new UsuarioApp(rs.getInt(COL_ID), rs.getString(COL_AD_OBJECT_ID), rs.getString(COL_USERNAME),
				rs.getString(COL_FOTO_PERFIL));
	}

	public Map<String, Object> toInsertMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		if (adObjectId != null)
			map.put(COL_AD_OBJECT_ID, adObjectId);
		map.put(COL_USERNAME, username);
		map.put(COL_FOTO_PERFIL, fotoPerfil);
		return map;
	}

	@Override
	public String toString() {
		return "UsuarioApp [id=" + id + ", adObjectId=" + adObjectId + ", username=" + username + ", fotoPerfil="
				+ fotoPerfil + "]";
	}
}