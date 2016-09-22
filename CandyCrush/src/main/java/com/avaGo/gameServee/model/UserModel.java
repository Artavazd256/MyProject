package com.avaGo.gameServee.model;

import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

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
        user.put("xp", 0L);
        user.put("currentLevelsXP", currentLevelsXP);
        user.put("level", 0L);
        user.put("createDate", System.currentTimeMillis());
        user.put("lastVisitDate", System.currentTimeMillis());
        user.put("lifeMax", Settings.MAX_LIFE);
        user.put("life", Settings.MAX_LIFE);
        user.put("lifeTime", Settings.LIFE_TIME );
        user.put("lifeStartTime", System.currentTimeMillis());
        user.put("foreverLifeTime", 0L);
        user.put("friendsEventsUIDS", friendsEventsUIDS);
        user.put("coins", 100L);
        user.put("volume", 0.5);
        user.put("language", "English");
        user.put("busters", new BasicDBList());
        user.put("lastGiveBailyBonusTime", System.currentTimeMillis());
        user.put("dailyBonusDay", 0L); // min 1 max 6
        user.put("dailyBonus", "");
        user.put("isDailyBonusGive", false);
        user.put("MyBonus", new BasicDBList());
        user.put("sale", System.currentTimeMillis() + (Settings.TIME_OF_GET_LIFE_FROM_FRIEND*5));
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

    /** Update all user info
     * @param userDoc {@link String}
     * @param uid {@link String}
     * @return {@link Boolean}
     * @throws JSONException
     */
    public static boolean updateDoc(Document userDoc, String uid) throws JSONException {
        userDoc.remove("_id");
        UpdateResult status = collection.replaceOne(eq("uid", uid), userDoc);
        return status.getMatchedCount() != 0 ? true : false;
    }

    /**  update doc bay custom fields
     * @param uid
     * @param level
     * @param xp
     * @param currentLevel
     * @return
     * @throws JSONException
     */
    public static boolean updateDocCustom(String uid, long level, long xp, Document currentLevel) throws JSONException {
        Document userDoc = new Document();
        userDoc.put("$push", new Document("currentLevelsXP", currentLevel));
        userDoc.put("$inc", new Document("xp", xp));
        userDoc.put("$set", new Document("level", level));
        UpdateResult status = collection.updateOne(eq("uid", uid), userDoc);
        return status.getMatchedCount() != 0 ? true : false;
    }

    /** Update forever life time
     * @param uid {@link String}
     * @return {@link Boolean}
     */
    public static boolean updateForeverLifeTime(String uid) {
        UpdateResult updateResult = collection.updateOne(and(eq("uid", uid), lt("foreverLifeTime", System.currentTimeMillis())), new Document("$set", new Document("foreverLifeTime", 0L)));
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

    /**
     * @param uid {@link String}
     * @param coins {@link Integer}
     * @return {@link UpdateResult}
     */
    public static UpdateResult incrementCoins(String uid, Integer coins) {
        UpdateResult updateResult = collection.updateOne(eq("uid", uid), new BasicDBObject("$inc", new BasicDBObject("coins", coins)));
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


    /** Get Top level
     * @param count {@link String}
     * @return {@link UpdateResult}
     */
    public static FindIterable<Document> getTopLevel(Integer count)  {
       FindIterable<Document> docs = collection.find().projection(Projections.include("xp", "uid")).sort(new BasicDBObject("xp", -1)).limit(count);
       return docs;
    }

    /**
     * Add daily bonus
     * @param uid  user id of FB {@link String}
     */
    public static void addDailyBonus(String uid) {
        assert (uid != null);
        Document user = collection.find(eq("uid", uid)).first();
        assert (user != null);
        Long lastGiveBailyBonusTime = user.getLong("lastGiveBailyBonusTime");
        LocalDate lastDate = Instant.ofEpochMilli(lastGiveBailyBonusTime).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        long between = ChronoUnit.DAYS.between(lastDate, now);
        Long dailyBonusDay = user.getLong("dailyBonusDay");
        Boolean isDailyBonusGive = user.getBoolean("isDailyBonusGive");
        String dailyBonsuName = null;
        if (between == 0 && !isDailyBonusGive) {
            dailyBonsuName = getDailyBonus(dailyBonusDay);
            putDailyBonus(dailyBonsuName, user, dailyBonusDay);
        } else if( between == 1 && !isDailyBonusGive) {
            dailyBonsuName = getDailyBonus(dailyBonusDay);
            putDailyBonus(dailyBonsuName, user, dailyBonusDay);
        } else if (between > 1) { //  If 'between' is greater from 1
            dailyBonsuName = getDailyBonus(1L);
            putDailyBonus(dailyBonsuName, user, dailyBonusDay);
        } else {
            return;
        }
        try {
            updateDoc(user, uid);
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                e.printStackTrace();
                System.err.println(String.format("Error: %s", e.toString()));
            }
        }
    }

    /** put daily bonus
     * @param dailyBonsuName
     * @param user
     * @param dailyBonusDay
     */
    private static void putDailyBonus(String dailyBonsuName, Document user, Long dailyBonusDay) {
        dailyBonusDay++; // increment
        if (dailyBonusDay >= 6) {
            dailyBonusDay = 2L;
        }
        user.put("dailyBonus", dailyBonsuName);
        user.put("dailyBonusDay", dailyBonusDay);
        user.put("isDailyBonusGive", false);
    }

    /** get daily bonus
     * @param dailyBonusDay
     * @return
     */
    private static String getDailyBonus(Long dailyBonusDay) {
        if (dailyBonusDay.equals(1L) || dailyBonusDay.equals(0L)) {
            return Settings.DailyBonus.day1;
        } else if (dailyBonusDay.equals(2L)) {
            return Settings.DailyBonus.day2;
        } else if (dailyBonusDay.equals(3L)) {
            return Settings.DailyBonus.day3;
        } else if (dailyBonusDay.equals(4L)) {
            return Settings.DailyBonus.day4;
        } else if (dailyBonusDay.equals(5L)) {
            return Settings.DailyBonus.day5;
        }
        return "";
    }

    /** give daily bonus
     * @param uid
     */
    public static void giveDailyBonus(String uid) throws JSONException {
        Document user = getUserByUID(uid);
        user.put("isDailyBonusGive", true);
        user.put("lastGiveBailyBonusTime", System.currentTimeMillis());
        Long dailyBonusDay = user.getLong("dailyBonusDay");
        if (dailyBonusDay.equals(1L)) {
            incrementCoins(uid, 10);
        } else if (dailyBonusDay.equals(2L)) {
            incrementCoins(uid, 20);
        } else if (dailyBonusDay.equals(3L)) {
            addBonus(user.getString("dailyBonus"), uid);
        } else if (dailyBonusDay.equals(4L)) {
            Document busterSpoon = MarketModel.getBusterDoc("BusterSpoon", 1, 10);
            addBuster(uid, busterSpoon);
        } else if (dailyBonusDay.equals(5L)) {
            addForeverLifeTime(uid, 1L);
        } else {
            return;
        }
        updateDoc(user, uid);
    }

    public static UpdateResult addForeverLifeTime(String uid, Long hours) {
        long oneHours = TimeUnit.HOURS.toMillis(1);
        long foreverLifeTime = System.currentTimeMillis() + oneHours;
        UpdateResult updateResult = collection.updateOne(eq("uid", uid), new BasicDBObject("$set", new BasicDBObject("foreverLifeTime", foreverLifeTime))); // TODO the case is not full
        return updateResult;
    }

    /**
     * @param bonusName
     * @param uid
     * @return
     */
    private static UpdateResult addBonus(String bonusName, String uid) {
        return collection.updateOne(eq("uid", uid), new BasicDBObject("$push", bonusName) );
    }

    public static boolean CheckForeverLifeTimeStatus(String uid) {
        Document userDoc = collection.find(eq("uid", uid)).first();
        if (userDoc == null) {
            return false;
        }
        Long foreverLifeTime = userDoc.getLong("foreverLifeTime");
        if (foreverLifeTime != 0) {
            return true;
        }
        return false;
    }

    /** Check have user sale
     * @param uid {@link String}
     * @return {@link Boolean}
     */
    public static boolean checkHaveUserSale(String uid) {
        Document user = getUserByUID(uid);
        long saleTime = user.getLong("sale");
        long currentTime = System.currentTimeMillis();
        if (currentTime < saleTime)  {
            return true;
        }
        return false;
    }

    public static boolean checkUserHaveLife(String uid) {
        Document userDoc = collection.find(eq("uid", uid)).first();
        if (userDoc == null) {
            return false;
        }
        long life = userDoc.getLong("life");
        if (life > 0) {
            return true;
        }
        return false;
    }

    /** Decrement life
     * @param uid  {@link String}
     * @return {@link UpdateResult}
     */
    public static UpdateResult decLife(String uid) {
        UpdateResult updateResult = collection.updateOne(eq("uid", uid), new Document("$inc", new Document("life", -1)));
        return updateResult;
    }


    /** increment life
     * @param uid  {@link String}
     * @return {@link UpdateResult}
     */
    public static UpdateResult IncLife(String uid) {
        UpdateResult updateResult = collection.updateOne(eq("uid", uid), new Document("$inc", new Document("life", 1)));
        return updateResult;
    }

    public static void updateLife(String uid) {
        assert (uid != null);
        Document userDoc = collection.find(eq("uid", uid)).first();
        if (userDoc == null) {
            return;
        }
        long lifeStartTime = userDoc.getLong("lifeStartTime");
        long currentTime = System.currentTimeMillis();
        if (lifeStartTime < currentTime) {
             collection.updateOne(eq("uid", uid), new Document("$set", new Document("life", Settings.MAX_LIFE)));
        } else  {
            long l = currentTime - lifeStartTime;
            long count = l / Settings.LIFE_TIME;
            if (count != 0) {
                count = count > Settings.MAX_LIFE ? Settings.MAX_LIFE : count;
                collection.updateOne(eq("uid", uid), new Document("$inc", new Document("life", count)));
                collection.updateOne(eq("uid", uid), new Document("$set", new Document("lifeStartTime", System.currentTimeMillis())));
            }

        }

    }

    /** increment
     * @param uid {@link String}
     */
    public static void incLifeTime(String uid) {
        Document userDoc = collection.find(eq("uid", uid)).first();
        long lifeStartTime = userDoc.getLong("lifeStartTime");
        long currentTime = System.currentTimeMillis();
        if (lifeStartTime < currentTime) {
            long newLifeStartTime = currentTime + Settings.LIFE_TIME;
            collection.updateOne(eq("uid", uid), new Document("$set", new Document("lifeStartTime", newLifeStartTime)));
        } else {
            collection.updateOne(eq("uid", uid), new Document("$inc", new Document("lifeStartTime", Settings.LIFE_TIME)));
        }
    }
}
