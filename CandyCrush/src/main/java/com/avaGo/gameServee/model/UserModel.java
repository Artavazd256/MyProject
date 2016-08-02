package com.avaGo.gameServee.model;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by artavzd on 8/2/16.
 */
public class UserModel {

    private static final String COLLECTION_NAME = "Users";
    private static final MongoClient mongoClient = MongoConnector.getMongoClient();
    private static final MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private static final MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);

    /** Get user by UID
     * @param uid
     * @return
     */
    public static Document getUserByUID(String uid) {
        assert (uid != null):"The uid argument of getUserByUID function is null";
        Document user = collection.find(eq("uid", uid)).first();
        assert (user != null) : "The user is null";
        return user;
    }

    public static Document getUserDocument(String uid) {
        BasicDBList currentLevelsXP = new BasicDBList();
        BasicDBList friendsEventsUIDS = new BasicDBList();
        Document user = new Document();
        user.put("uid", uid);
        user.put("xp", 0);
        user.put("currentLevelsXP", currentLevelsXP);
        user.put("level", 0);
        user.put("createDate", System.currentTimeMillis());
        user.put("lastVisitDate", System.currentTimeMillis());
        user.put("lifeMax", 5);
        user.put("life", 5);
        user.put("lifeTime", 1800);
        user.put("lifeStartTime", System.currentTimeMillis());
        user.put("foreverLifeTime", 0);
        user.put("friendsEventsUIDS", friendsEventsUIDS);
        return user;
    }

    public static void createUser(String uid) {
        Document userDocument = getUserDocument(uid);
        collection.insertOne(userDocument);
    }



}
