package local.alejandrogb.metricsservers.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoManager {
	private static MongoManager instance;
	private MongoClient client;
	private MongoDatabase database;

	private MongoManager() {
		client = MongoClients.create("mongodb://monitoring_user:abc123.@10.0.1.26/monitoring");
		database = client.getDatabase("monitoring");
	}

	public static MongoManager getInstance() {
		if (instance == null)
			instance = new MongoManager();
		return instance;
	}

	public MongoDatabase getDatabase() {
		return database;
	}
}
