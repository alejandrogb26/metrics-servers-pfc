package local.alejandrogb.metricsservers.api.services.auth;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import local.alejandrogb.metricsservers.utils.EnvConfig;

public class LdapAuthService {
	private final String ldapUrl = EnvConfig.LDAP_URL;
	private final String baseDn = EnvConfig.LDAP_BASE_DN;

	private final String serviceAccountDn = EnvConfig.LDAP_SVC_DN;
	private final String serviceAccountPassword = EnvConfig.LDAP_SVC_PW;

	public AdUser authenticate(String username, String password) {
		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			return null;
		}

		String principal = buildUserPrincipalName(username);

		if (!canBind(principal, password)) {
			return null;
		}

		return loadUserByUsername(username.trim());
	}

	private boolean canBind(String principal, String password) {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, password);

		LdapContext ctx = null;

		try {
			ctx = new InitialLdapContext(env, null);
			return true;
		} catch (Exception e) {
			System.out.println("[LDAP] Bind KO con principal: " + principal + " -> " + e.getMessage());
			return false;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	private AdUser loadUserByUsername(String username) {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, serviceAccountDn);
		env.put(Context.SECURITY_CREDENTIALS, serviceAccountPassword);

		LdapContext ctx = null;

		try {
			ctx = new InitialLdapContext(env, null);

			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			sc.setReturningAttributes(
					new String[] { "sAMAccountName", "userPrincipalName", "displayName", "mail", "memberOf" });

			String filter = "(&(objectCategory=person)(objectClass=user)(sAMAccountName=" + escapeLdap(username.trim())
					+ "))";

			NamingEnumeration<SearchResult> results = ctx.search(baseDn, filter, sc);

			if (!results.hasMore()) {
				return null;
			}

			SearchResult sr = results.next();
			System.out.println("[LDAP] Usuario encontrado: " + sr.getNameInNamespace());

			Attributes attrs = sr.getAttributes();

			String samAccountName = getString(attrs, "sAMAccountName");
			String upn = getString(attrs, "userPrincipalName");
			String displayName = getString(attrs, "displayName");
			String mail = getString(attrs, "mail");
			List<String> memberOf = getMultiString(attrs, "memberOf");

			return new AdUser(samAccountName, upn, displayName, mail, memberOf);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error consultando Active Directory", e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	private String buildUserPrincipalName(String username) {
		String normalizedUsername = username.trim();

		if (normalizedUsername.contains("@")) {
			return normalizedUsername;
		}

		return normalizedUsername + "@metrics.local";
	}

	private String getString(Attributes attrs, String name) throws Exception {
		if (attrs.get(name) == null) {
			return null;
		}
		Object value = attrs.get(name).get();
		return value != null ? value.toString() : null;
	}

	private List<String> getMultiString(Attributes attrs, String name) throws Exception {
		List<String> values = new ArrayList<>();
		if (attrs.get(name) == null) {
			return values;
		}

		NamingEnumeration<?> en = attrs.get(name).getAll();
		while (en.hasMore()) {
			Object value = en.next();
			if (value != null) {
				values.add(value.toString());
			}
		}
		return values;
	}

	private String escapeLdap(String input) {
		return input.replace("\\", "\\5c").replace("*", "\\2a").replace("(", "\\28").replace(")", "\\29")
				.replace("\u0000", "\\00");
	}

	public record AdUser(String samAccountName, String userPrincipalName, String displayName, String mail,
			List<String> memberOf) {
	}
}