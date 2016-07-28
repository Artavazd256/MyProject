package com.avaGo.gameServee.model;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by root on 2/15/16.
 */
public class MongoConnector {

    public static final String DATA_BASE_NAME = "MyGame";
    public static final String LEVEL_COLLECTION_NAME = "level";
    public static final String USER_COLLECTION_NAME = "user";
    public static final String HOST = "127.0.0.1";
    public static final Integer PORT = 27017;

    public static MongoDatabase getMongoDatabase(MongoClient client, String dbName) {
        assert (client != null);
        assert (dbName != null);
        MongoDatabase db = client.getDatabase(dbName);
        assert (db != null);
        return db;
    }

    public static MongoClient getMongoClient() {
        MongoClient client = new MongoClient(HOST, PORT);
        return client;
    }

    public static MongoCollection<Document> getCollection(MongoDatabase mongoDatabase, String collectionName) {
        try {
            mongoDatabase.createCollection(collectionName);
        }catch (com.mongodb.MongoCommandException e) {

        }
        return mongoDatabase.getCollection(collectionName);
    }

    public static void closeMongo(MongoClient mongoClient) {
       mongoClient.close();
    }

}
