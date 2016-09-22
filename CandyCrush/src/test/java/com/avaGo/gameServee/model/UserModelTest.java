package com.avaGo.gameServee.model;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by artavzd on 8/12/16.
 */
public class UserModelTest {
    private static final String uid = "8888";

    @Test
    public void addDailyBonus() throws Exception {
        System.out.println(TimeUnit.HOURS.toMillis(1));
    }


    @Test
    public void getTopLevel() throws Exception {
        UserModel.createUser(uid);
        UserModel.addNewLevelInfo(uid, 10l, 1, 1);
        UserModel.addNewLevelInfo(uid, 42l, 2, 2);
        UserModel.addNewLevelInfo(uid, 82l, 3, 4);

        UserModel.createUser("9999");
        UserModel.addNewLevelInfo("9999", 10l, 1, 1);
        UserModel.addNewLevelInfo("9999", 92l, 2, 2);
        UserModel.addNewLevelInfo("9999", 10l, 3, 1);

        FindIterable<Document> topLevel = UserModel.getTopLevel(100);
        for (Document doc : topLevel) {
            System.out.println(doc.toJson());
        }
        UserModel.deleteUserByUID(uid);
        UserModel.deleteUserByUID("9999");

    }

    @Test
    public void removeBuster() throws Exception {
        Document doc = new Document();
        UserModel.createUser(uid);
        doc.put("name", "buster1");
        doc.put("availableLevel", 16);
        UserModel.addBuster(uid, doc);
        doc.put("name", "buster2");
        doc.put("availableLevel", 18);
        UserModel.addBuster(uid, doc);
        UserModel.removeBuster(uid, "buster1");
        List r = (List) UserModel.getUserByUID(uid).get("busters");
        UserModel.deleteUserByUID(uid);
        assertTrue(r.size() == 1);
    }

    @Test
    public void addBuster() throws Exception {
        Document doc = new Document();
        doc.put("name", "buster1");
        doc.put("availableLevel", 16);
        UserModel.createUser(uid);
        UserModel.addBuster(uid, doc);
        UserModel.addBuster(uid, doc);
        Document userByUID = UserModel.getUserByUID(uid);
        List r = (List) userByUID.get("busters");
        UserModel.deleteUserByUID(uid);
        assertTrue(r.size() == 2);
    }


    @Test
    public void decrementCoins() throws Exception {
        UserModel.createUser(uid);
        UserModel.decrementCoins(uid, 5);
        Document userDoc = UserModel.getUserByUID(uid);
        Integer coins = userDoc.getInteger("coins");
        assertTrue(coins == 95);
        UserModel.deleteUserByUID(uid);
    }


    @Test
    public void isUserExist() throws Exception {
        UserModel.createUser(uid);
        boolean status = UserModel.isUserExist(uid);
        UserModel.deleteUserByUID(uid);
        assertTrue(status);
    }

    @Test
    public void isUserGreaterCoin() throws Exception {
        UserModel.createUser(uid);
        boolean status = UserModel.isUserGreaterCoin(uid, 10);
        assertTrue(status);
        status = UserModel.isUserGreaterCoin(uid, 100);
        assertTrue(status);
        status = UserModel.isUserGreaterCoin(uid, 101);
        assertFalse(status);
        UserModel.deleteUserByUID(uid);

    }


    @Test
    public void getUserByUID() throws Exception {
        UserModel.createUser(uid);
        Document doc = UserModel.getUserByUID(uid);
        UserModel.deleteUserByUID(uid);
        assertNotNull(doc);
    }

    @Test
    public void getUserDocument() throws Exception {
        Document userDocument = UserModel.getUserDocument(uid);
        assertNotNull(userDocument);
    }

    @Test
    public void createUser() throws Exception {
        UserModel.createUser(uid);
        Document doc = UserModel.getUserByUID(uid);
        UserModel.deleteUserByUID(uid);
        assertNotNull(doc);
    }

    /**
     * @throws Exception
     */
    @Test
    public void updateDoc() throws Exception {
        UserModel.createUser(uid);
        Document currentLevel = new Document();
        currentLevel.put("xp", 5);
        currentLevel.put("stare", 3);
        currentLevel.put("level", 2);
        UserModel.updateDocCustom(uid, 10, 30, currentLevel);
        Document userDoc = UserModel.getUserByUID(uid);
        UserModel.deleteUserByUID(uid);
        assertTrue(userDoc.getLong("xp") == 30L);
        assertTrue(userDoc.getLong("level") == 10L);
    }

    @Test
    public void updateForeverLifeTime() throws Exception {
        UserModel.createUser(uid);
        Document userDoc = UserModel.getUserByUID(uid);
        userDoc.put("foreverLifeTime", System.currentTimeMillis());
        UserModel.updateDoc(userDoc.toJson(), uid);
        boolean b = UserModel.updateForeverLifeTime(uid);
        UserModel.deleteUserByUID(uid);
        assertTrue(b);
    }

    @Test
    public void updateLastVisitDate() throws Exception {
        UserModel.createUser(uid);
        boolean b = UserModel.updateLastVisitDate(uid);
        UserModel.deleteUserByUID(uid);
        assertTrue(b);
    }

    @Test
    public void addNewLevelInfo() throws Exception {
        UserModel.createUser(uid);
        UserModel.addNewLevelInfo(uid, 10l, 1, 3 );
        UserModel.addNewLevelInfo(uid, 10l, 2, 5 );
        Document userDoc = UserModel.getUserByUID(uid);
        List<BasicDBObject> levels = (List<BasicDBObject>) userDoc.get("currentLevelsXP");
        UserModel.deleteUserByUID(uid);
        assertEquals(levels.size(), 2);
    }

    @Test
    public void updateLevelInfo() throws Exception {
        UserModel.createUser(uid);
        UserModel.addNewLevelInfo(uid, 10l, 4, 3 );
        UserModel.addNewLevelInfo(uid, 10l, 5, 3 );
        UserModel.updateLevelInfo(uid, 18l, 4, 5 );
        Document userDoc = UserModel.getUserByUID(uid);
        List<Document> levels = (List<Document>) userDoc.get("currentLevelsXP");
        assertTrue(levels.get(0).getLong("xp") == 18 ? true : false);
        UserModel.deleteUserByUID(uid);
    }

}