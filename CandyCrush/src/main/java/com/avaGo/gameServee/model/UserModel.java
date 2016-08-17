package com.avaGo.gameServee.model;

import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONException;

import static com.mongodb.client.model.Filters.*;

/**
 * Created by artavzd on 8/2/16.
 */
public class UserModel {

    private static final String COLLECTION_NAME = "Users";
    private static final MongoClient mongoClient = MongoConnector.getMongoClient();
    private static final MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private static final MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);

    /** Get user by UID
     * @param uid {@link String}
     * @return {@link Document}
     */
    public static Document getUserByUID(String uid) {
        assert (uid != null):"The uid argument of getUserByUID function is null";
        Document user = collection.find(eq("uid", uid)).first();
        assert (user != null) : "The user is null";
        return user;
    }

    /** Get User Document
     * @param uid {@link String}
     * @return {@link Document}
     */
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
        user.put("coins", 100);
        user.put("volume", 0.5);
        user.put("language", "English");
        user.put("busters", new BasicDBList());
        return user;
    }

    public static void createUser(String uid) {
        Document userDocument = getUserDocument(uid);
        collection.insertOne(userDocument);
    }

    /** Update all user info
     * @param user {@link String}
     * @param uid {@link String}
     * @return {@link Boolean}
     * @throws JSONException
     */
    public static boolean updateDoc(String user, String uid) throws JSONException {
        Document userDoc = Document.parse(user);
        userDoc.remove("_id");
        UpdateResult status = collection.replaceOne(eq("uid", uid), userDoc);
        return status.getMatchedCount() != 0 ? true : false;
    }

    /** Update forever life time
     * @param uid {@link String}
     * @return {@link Boolean}
     */
    public static boolean updateForeverLifeTime(String uid) {
        UpdateResult updateResult = collection.updateOne(and(eq("uid", uid), lt("foreverLifeTime", System.currentTimeMillis())), new Document("$set", new Document("foreverLifeTime", 0l)));
        if (updateResult.getMatchedCount() != 0) {
            return true;
        }
        return false;
    }

    /** Updated Last Visit date
     * @param uid
     * @return {@link Boolean}
     */
    public static boolean updateLastVisitDate(String uid) {
        UpdateResult updateResult = collection.updateOne(eq("uid", uid), new Document("$set", new Document("lastVisitDate", System.currentTimeMillis())));
        if (updateResult.getMatchedCount() != 0) {
            return true;
        }
        return false;
    }

    /** Delete user by UID
     * @param uid {@link String}
     * @return {@link UpdateResult}
     */
    public static DeleteResult deleteUserByUID(String uid) {
        DeleteResult status = collection.deleteOne(eq("uid", uid));
        return status;
    }

    /** add new Level info
     * @param uid {@link String}
     * @param xp {@link Long}
     * @param level {@link Integer}
     * @param stare {@link Integer}
     * @return {@link UpdateResult}
     */
    public static  UpdateResult addNewLevelInfo(String uid, Long xp, Integer level, Integer stare) {
        BasicDBObject levelDoc = new BasicDBObject();
        levelDoc.put("level", level);
        levelDoc.put("stare", stare);
        levelDoc.put("xp", xp);
        levelDoc.put("date", System.currentTimeMillis());
        UpdateResult status = collection.updateOne(eq("uid", uid), new BasicDBObject("$push", new BasicDBObject("currentLevelsXP", levelDoc)));
        return status;
    }

    /** Update Level info
     * @param uid {@link String}
     * @param xp {@link Long}
     * @param level {@link Integer}
     * @param stare {@link Integer}
     * @return {@link UpdateResult}
     */
    public static UpdateResult updateLevelInfo(String uid, Long xp, Integer level, Integer stare) {
        assert (uid != null);
        assert (xp != null);
        assert (level != null);
        assert (stare != null);
        BasicDBObject levelDoc = new BasicDBObject();
        levelDoc.put("level", level);
        levelDoc.put("stare", stare);
        levelDoc.put("xp", xp);
        levelDoc.put("date", System.currentTimeMillis());
        UpdateResult status = collection.updateOne(and(eq("uid", uid), lt("currentLevelsXP.xp", xp)), new BasicDBObject("$set", new BasicDBObject("currentLevelsXP.$", levelDoc)));
        return status;
    }

    /** Check user exists
     * @param uid {@link String}
     * @return {@link Boolean}
     */
    public static boolean isUserExist(String uid) {
        Document doc = collection.find(eq("uid", uid)).first();
        return doc != null ? true : false;
    }

    /** Is user greater coins
     * @param uid {@link String}
     * @param coin {@link Integer}
     * @return
     */
    public static boolean isUserGreaterCoin(String uid, Integer coin) {
        assert (uid != null);
        assert (coin != null);
        return collection.find(and(eq("uid", uid), gte("coins", coin))).first() != null ? true : false;
    }

    /**
     * @param uid {@link String}
     * @param coins {@link Integer}
     * @return {@link UpdateResult}
     */
    public static UpdateResult decrementCoins(String uid, Integer coins) {
        UpdateResult updateResult = collection.updateOne(eq("uid", uid), new BasicDBObject("$inc", new BasicDBObject("coins", -coins)));
        return updateResult;
    }

    /** Add Buster into user
     * @param uid {@link String}
     * @param productDoc {@link Document}
     * @return {@link UpdateResult}
     */
    public static UpdateResult addBuster(String uid, Document productDoc) {
        productDoc.remove("_id");
        productDoc.remove("coin");
        UpdateResult updateResult = collection.updateOne(eq("uid", uid), new BasicDBObject("$push", new BasicDBObject("busters", productDoc)));
        return updateResult;
    }

    /** Remove buster of user
     * @param uid
     * @param busterName
     * @return
     */
    public static UpdateResult removeBuster(String uid, String busterName) {
        UpdateResult updateResult = collection.updateOne(and(eq("uid", uid), eq("busters.name", busterName))
                                               , new BasicDBObject("$pull", new BasicDBObject("busters", new BasicDBObject("name", busterName))));
        return updateResult;
    }

}
