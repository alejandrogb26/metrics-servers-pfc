package local.alejandrogb.metricsservers.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Gestor de conexión para la base de datos MongoDB utilizada por la aplicación.
 *
 * <p>
 * Esta clase implementa el patrón de diseño <b>Singleton</b> para garantizar
 * que exista una única instancia de {@link MongoClient} durante todo el ciclo
 * de vida de la aplicación. De esta forma se evita la creación innecesaria de
 * múltiples conexiones al servidor MongoDB.
 * </p>
 *
 * <p>
 * La conexión se inicializa utilizando la cadena de conexión definida en el
 * constructor privado y permite acceder a la base de datos {@code monitoring}.
 * </p>
 *
 * <p>
 * El acceso a la base de datos se realiza mediante el método
 * {@link #getDatabase()}, que devuelve una instancia de {@link MongoDatabase}
 * lista para realizar operaciones sobre las colecciones.
 * </p>
 *
 * <p>
 * Ejemplo de uso:
 * </p>
 *
 * <pre>
 * MongoDatabase db = MongoManager.getInstance().getDatabase();
 * </pre>
 *
 * @author alejandrogb
 */
public class MongoManager {

	/**
	 * Instancia única del gestor de MongoDB.
	 */
	private static MongoManager instance;

	/**
	 * Cliente de conexión al servidor MongoDB.
	 */
	private MongoClient client;

	/**
	 * Referencia a la base de datos utilizada por la aplicación.
	 */
	private MongoDatabase database;

	/**
	 * Constructor privado que inicializa la conexión con MongoDB y obtiene la base
	 * de datos principal de la aplicación.
	 */
	private MongoManager() {
		String uri = EnvConfig.MONGO_URI;
		client = MongoClients.create(uri);
		String dbName = EnvConfig.MONGO_DB;
		database = client.getDatabase(dbName);
	}

	/**
	 * Devuelve la instancia única de {@link MongoManager}.
	 *
	 * <p>
	 * Si la instancia aún no ha sido creada, se inicializa en el momento de la
	 * primera llamada.
	 * </p>
	 *
	 * @return instancia singleton de {@link MongoManager}
	 */
	public static MongoManager getInstance() {
		if (instance == null)
			instance = new MongoManager();
		return instance;
	}

	/**
	 * Obtiene la referencia a la base de datos MongoDB utilizada por la aplicación.
	 *
	 * @return instancia de {@link MongoDatabase} correspondiente a la base de datos
	 *         {@code monitoring}
	 */
	public MongoDatabase getDatabase() {
		return database;
	}
}