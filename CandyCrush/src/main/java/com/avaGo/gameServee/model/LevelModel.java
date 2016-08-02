package com.avaGo.gameServee.model;

import com.avaGo.gameServee.setting.Settings;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by artavzd on 8/2/16.
 */
public class LevelModel {

    private static final String COLLECTION_NAME = "Levels";
    private static final MongoClient mongoClient = MongoConnector.getMongoClient();
    private static final MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private static final MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);


    public static Document getLevelByID(String id) {
        Document level = null;
        if( ObjectId.isValid(id) ) {
            level = collection.find(eq("_id", new ObjectId(id))).first();
        } else {
            if (Settings.IS_DEBUG) {
                System.err.println("The id is not valid of level ");
            }
        }
        return level;
    }

}
