package com.avaGo.gameServee.model;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;


/**
 * Created by artavzd on 8/4/16.
 */
public class PaymentStatus {

    private static final String COLLECTION_NAME = "PaymentStatus";
    private static MongoClient mongoClient = MongoConnector.getMongoClient();
    private static MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private static MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);


    /**
     * @return
     */
    public static String insertNewRecord(String uid) {
        long pID  = collection.count() + 1;
        collection.insertOne(new Document("pID", String.valueOf(pID)).append("uid", uid).append("status", false));
        return String.valueOf(pID);
    }


    /**
     * @param pID
     * @return
     */
    public static Document getPaymentStatusDocByPID(String pID) {
        Document pID1 = collection.find(eq("pID", pID)).first();
        return pID1;
    }

    /**
     * @param pID
     * @param doc
     */
    public static void updatePaymentStatusByPID(String pID, Document doc) {
        collection.findOneAndReplace(eq("pID", pID), doc);
    }

}
