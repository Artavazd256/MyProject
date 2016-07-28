package com.avaGo.gameServee.model;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Created by artavzd on 7/20/16.
 */
public class UtilsMongo {

    public static boolean isUserExists(String uid, MongoCollection<Document> collection ) {
        BasicDBObject query = new BasicDBObject();
        query.put("uid", uid);
        Document first = collection.find(query).first();
        if (null != first) {
            return true;
        }
        return false;
    }
}
