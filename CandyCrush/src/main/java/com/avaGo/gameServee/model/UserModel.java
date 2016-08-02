package com.avaGo.gameServee.model;

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



}
