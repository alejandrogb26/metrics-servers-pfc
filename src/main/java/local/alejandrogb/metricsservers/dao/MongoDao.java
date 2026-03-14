package local.alejandrogb.metricsservers.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import local.alejandrogb.metricsservers.models.metrics.MetricPoint;
import local.alejandrogb.metricsservers.utils.MongoManager;

public class MongoDao {
	private final MongoDatabase db = MongoManager.getInstance().getDatabase();
	private final CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
			MongoClientSettings.getDefaultCodecRegistry(),
			CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

	public long updateServerId(String oldServerId, String newServerId) {
		return db.getCollection("host_metrics").updateMany(new Document("server_id", oldServerId),
				new Document("$set", new Document("server_id", newServerId))).getModifiedCount();
	}

	public long deleteByServerId(String serverId) {
		return db.getCollection("host_metrics").deleteMany(new Document("server_id", serverId)).getDeletedCount();
	}

	// Métricas
	public List<MetricPoint> getMetrics(String serverId, Long minutesBack) {
		long since = System.currentTimeMillis() - (minutesBack * 60 * 1000L);

		return getCollectionOrm(MetricPoint.class)
				.find(Filters.and(Filters.eq("server_id", serverId), Filters.gt("ts", new Date(since))))
				.into(new ArrayList<>());
	}

	private <T> MongoCollection<T> getCollectionOrm(Class<T> model) {
		return db.getCollection("host_metrics", model).withCodecRegistry(pojoCodecRegistry);
	}
}
