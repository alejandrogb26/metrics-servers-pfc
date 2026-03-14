package local.alejandrogb.metricsservers.models.usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiToken {
	public static final String TABLE = "api_tokens";
	public static final String COL_ID = "id";
	public static final String COL_USER_ID = "usuarioId";
	public static final String COL_TOKEN = "token";
	public static final String COL_ACTIVE = "active";
	public static final String COL_CREATED_AT = "createdAt";

	private int id, usuarioId;
	private String token;
	private boolean active;
	private LocalDateTime createdAt;

	public ApiToken() {
	}

	public ApiToken(int id, int usuarioId, String token, boolean active, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.usuarioId = usuarioId;
		this.token = token;
		this.active = active;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public static ApiToken mapToken(ResultSet rs) throws SQLException {
		return new ApiToken(rs.getInt(COL_ID), rs.getInt(COL_USER_ID), rs.getString(COL_TOKEN),
				rs.getBoolean(COL_ACTIVE), rs.getTimestamp(COL_CREATED_AT).toLocalDateTime());
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put(COL_USER_ID, id);
		map.put(COL_TOKEN, token);
		map.put(COL_ACTIVE, active);
		map.put(COL_CREATED_AT, createdAt);
		return map;
	}

	@Override
	public String toString() {
		return "ApiToken [id=" + id + ", usuarioId=" + usuarioId + ", token=" + token + ", active=" + active
				+ ", createdAt=" + createdAt + "]";
	}
}
