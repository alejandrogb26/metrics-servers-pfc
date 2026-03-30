package local.alejandrogb.metricsservers.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
	private static final Dotenv DOTENV = Dotenv.configure().directory("/opt/tomee/config").filename(".env").load();

	public static final String MONGO_URI = require("MONGO_URI");
	public static final String MONGO_DB = require("MONGO_DB");
	
	public static final String LDAP_URL = require("LDAP_URL");
	public static final String LDAP_BASE_DN = require("LDAP_BASE_DN");
	public static final String LDAP_SVC_DN = require("LDAP_SVC_DN");
	public static final String LDAP_SVC_PW = require("LDAP_SVC_PW");
	
	public static final String MINIO_ENDPOINT = require("MINIO_ENDPOINT");
	public static final String MINIO_ACCESS_KEY = require("MINIO_ACCESS_KEY");
	public static final String MINIO_SECRET_KEY = require("MINIO_SECRET_KEY");
	public static final String BUCKET_USERS = require("BUCKET_USERS");
	public static final String BUCKET_SERVIDORES = require("BUCKET_SERVIDORES");
	public static final String BUCKET_SERVICIOS = require("BUCKET_SERVICIOS");
	
	public static final String JWT_SECRET = require("JWT_SECRET");
	
	
	private EnvConfig() {
	}

	private static String require(String key) {
		String value = DOTENV.get(key);
		if (value == null || value.isBlank())
			throw new IllegalStateException("Falta la variable obligatoria: " + key);

		return value;
	}
}
