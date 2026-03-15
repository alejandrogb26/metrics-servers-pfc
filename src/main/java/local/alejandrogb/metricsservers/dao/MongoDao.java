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

/**
 * Objeto de acceso a datos (DAO) encargado de gestionar las operaciones
 * relacionadas con las métricas almacenadas en MongoDB.
 *
 * <p>
 * Esta clase encapsula el acceso a la colección {@code host_metrics},
 * proporcionando métodos para consultar, actualizar y eliminar registros
 * asociados a servidores monitorizados.
 * </p>
 *
 * <p>
 * Utiliza el cliente MongoDB proporcionado por {@link MongoManager} y configura
 * un {@link CodecRegistry} con soporte para POJOs mediante
 * {@link PojoCodecProvider}. Esto permite mapear automáticamente los documentos
 * BSON de MongoDB a objetos Java, como {@link MetricPoint}.
 * </p>
 *
 * <p>
 * La clase forma parte de la capa de persistencia del sistema y se encarga
 * exclusivamente de las operaciones de acceso a datos relacionadas con métricas
 * de servidores.
 * </p>
 *
 * @author Alejandro GB
 */
public class MongoDao {

	/**
	 * Instancia de la base de datos MongoDB utilizada por la aplicación.
	 */
	private final MongoDatabase db = MongoManager.getInstance().getDatabase();

	/**
	 * Registro de codecs utilizado para permitir el mapeo automático entre
	 * documentos BSON y objetos Java (POJOs).
	 */
	private final CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
			MongoClientSettings.getDefaultCodecRegistry(),
			CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

	/**
	 * Actualiza el identificador de un servidor en todos los documentos de métricas
	 * que coincidan con el identificador antiguo.
	 *
	 * <p>
	 * Este método resulta útil cuando se modifica el identificador de un servidor y
	 * es necesario mantener la consistencia de los datos históricos almacenados.
	 * </p>
	 *
	 * @param oldServerId identificador actual del servidor
	 * @param newServerId nuevo identificador que se aplicará a los registros
	 * @return número de documentos modificados
	 */
	public long updateServerId(String oldServerId, String newServerId) {
		return db.getCollection("host_metrics").updateMany(new Document("server_id", oldServerId),
				new Document("$set", new Document("server_id", newServerId))).getModifiedCount();
	}

	/**
	 * Elimina todos los registros de métricas asociados a un servidor.
	 *
	 * <p>
	 * Esta operación se utiliza generalmente cuando un servidor es eliminado del
	 * sistema y se desea limpiar su histórico de métricas.
	 * </p>
	 *
	 * @param serverId identificador del servidor
	 * @return número de documentos eliminados
	 */
	public long deleteByServerId(String serverId) {
		return db.getCollection("host_metrics").deleteMany(new Document("server_id", serverId)).getDeletedCount();
	}

	/**
	 * Recupera los puntos de métricas de un servidor dentro de un intervalo de
	 * tiempo reciente.
	 *
	 * <p>
	 * El método calcula un instante de inicio en función del número de minutos
	 * especificado y devuelve todas las métricas cuyo timestamp sea posterior a
	 * dicho instante.
	 * </p>
	 *
	 * @param serverId    identificador del servidor
	 * @param minutesBack número de minutos hacia atrás desde el momento actual que
	 *                    se utilizarán como límite inferior del intervalo
	 * @return lista de objetos {@link MetricPoint} con las métricas recuperadas
	 */
	public List<MetricPoint> getMetrics(String serverId, Long minutesBack) {

		long since = System.currentTimeMillis() - (minutesBack * 60 * 1000L);

		return getCollectionOrm(MetricPoint.class)
				.find(Filters.and(Filters.eq("server_id", serverId), Filters.gt("ts", new Date(since))))
				.into(new ArrayList<>());
	}

	/**
	 * Obtiene una colección de MongoDB configurada para mapear documentos BSON
	 * directamente a objetos Java del tipo indicado.
	 *
	 * <p>
	 * Este método utiliza el {@link CodecRegistry} configurado para habilitar el
	 * soporte de POJOs mediante {@link PojoCodecProvider}.
	 * </p>
	 *
	 * @param <T>   tipo del modelo Java al que se mapearán los documentos
	 * @param model clase del modelo Java
	 * @return colección MongoDB configurada para trabajar con el tipo indicado
	 */
	private <T> MongoCollection<T> getCollectionOrm(Class<T> model) {
		return db.getCollection("host_metrics", model).withCodecRegistry(pojoCodecRegistry);
	}
}