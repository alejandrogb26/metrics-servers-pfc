package local.alejandrogb.metricsservers.models.usuario;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioAuth {
	private String username, nombre, apel1, apel2;

	public UsuarioAuth(String username, String nombre, String apel1, String apel2) {
		super();
		this.username = username;
		this.nombre = nombre;
		this.apel1 = apel1;
		this.apel2 = apel2;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public static UsuarioAuth mapUsuarioAuth(ResultSet rs) {
		try {
			return new UsuarioAuth(rs.getString(Usuario.COL_USERNAME), rs.getString(Usuario.COL_NOMBRE),
					rs.getString(Usuario.COL_APEL1), rs.getString(Usuario.COL_APEL2));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "UsuarioAuth [username=" + username + ", nombre=" + nombre + ", apel1=" + apel1 + ", apel2=" + apel2
				+ "]";
	}
}
