package com.avaGo.gameServee.model;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by artavzd on 8/12/16.
 */
public class UserModelTest {

    private static final String uid = "8888";

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

    @Test
    public void updateDoc() throws Exception {
        UserModel.createUser(uid);
        Document userDoc = UserModel.getUserByUID(uid);
        userDoc.put("xp", "5");
        UserModel.updateDoc(userDoc.toJson(), uid);
        userDoc = UserModel.getUserByUID(uid);
        UserModel.deleteUserByUID(uid);
        assertEquals(userDoc.getString("xp"), "5");
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