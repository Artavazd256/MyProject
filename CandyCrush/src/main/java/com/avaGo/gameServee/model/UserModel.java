package com.avaGo.gameServee.model;

import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

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
        user.put("coins", 0);
        return user;
    }

    public static void createUser(String uid) {
        Document userDocument = getUserDocument(uid);
        collection.insertOne(userDocument);
    }

    /** Update all user info
     *
     * @param user
     * @param uid
     * @return
     * @throws JSONException
     */
    public static boolean updateDoc(String user, String uid) throws JSONException {
        Document userDoc = Document.parse(user);
        userDoc.remove("_id");
        UpdateResult status = collection.replaceOne(eq("uid", uid), userDoc);
        return status.getMatchedCount() != 0 ? true : false;
    }

    public static boolean UpdateForeverLifeTime(String uid) {
        UpdateResult updateResult = collection.replaceOne(and(eq("uid", uid), gt("foreverLife", System.currentTimeMillis())), new Document("$set", new Document("foreverLife", 0l)));
        if (updateResult.getMatchedCount() != 0) {
            return true;
        }
        return false;
    }



}
