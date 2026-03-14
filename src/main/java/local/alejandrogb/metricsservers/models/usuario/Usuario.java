package local.alejandrogb.metricsservers.models.usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Usuario {
	public static final String TABLE = "usuarios";

	public static final String COL_ID = "id";
	public static final String COL_NOMBRE = "nombre";
	public static final String COL_APEL1 = "apel1";
	public static final String COL_APEL2 = "apel2";
	public static final String COL_USERNAME = "username";
	public static final String COL_GRUPO_ID = "grupoId";
	public static final String COL_ACTIVE = "active";
	public static final String COL_FOTO_PERFIL = "fotoPerfil";

	private int id, grupoId;
	private String nombre, apel1, apel2, username, fotoPerfil;
	private String urlFoto; // Este campo NO está en la BD, se llena al vuelo para el cliente.
	private Boolean active;

	public Usuario() {
	}

	public Usuario(int id, String nombre, String apel1, String apel2, String username, int grupoId, Boolean active,
			String fotoPerfil) {
		this.id = id;
		this.grupoId = grupoId;
		this.nombre = nombre;
		this.apel1 = apel1;
		this.apel2 = apel2;
		this.username = username;
		this.active = active;
		this.fotoPerfil = fotoPerfil;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGrupoId() {
		return grupoId;
	}

	public void setGrupoId(int grupoId) {
		this.grupoId = grupoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApel1() {
		return apel1;
	}

	public void setApel1(String apel1) {
		this.apel1 = apel1;
	}

	public String getApel2() {
		return apel2;
	}

	public void setApel2(String apel2) {
		this.apel2 = apel2;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

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

	public static Usuario mapUsuario(ResultSet rs) {
		try {
			return new Usuario(rs.getInt(COL_ID), rs.getString(COL_NOMBRE), rs.getString(COL_APEL1),
					rs.getString(COL_APEL2), rs.getString(COL_USERNAME), rs.getInt(COL_GRUPO_ID),
					rs.getBoolean(COL_ACTIVE), rs.getString(COL_FOTO_PERFIL));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put(COL_NOMBRE, nombre);
		map.put(COL_APEL1, apel1);
		map.put(COL_APEL2, apel2);
		map.put(COL_USERNAME, username);
		map.put(COL_GRUPO_ID, grupoId);
		map.put(COL_ACTIVE, active);
		map.put(COL_FOTO_PERFIL, fotoPerfil);
		return map;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", grupoId=" + grupoId + ", nombre=" + nombre + ", apel1=" + apel1 + ", apel2="
				+ apel2 + ", username=" + username + ", active=" + active + "]";
	}

}
