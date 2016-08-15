package com.avaGo.gameServee.model;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.BasicBSONList;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by artavzd on 8/14/16.
 */
public class MarketModel {

    private static final String COLLECTION_NAME = "Market";
    private static final MongoClient mongoClient = MongoConnector.getMongoClient();
    private static final MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private static final MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);

    private static Document getBusterDoc(String name, Integer availableLevel, Integer coin) {
        Document buster = new Document();
        buster.put("name", name);
        buster.put("availableLevel", availableLevel);
        buster.put("coin", coin);
        return buster;
    }

    private static List<Document> getContentForBuster() {
        List<Document> root = new ArrayList<>();
        root.add(getBusterDoc("buster1", 16, 10));
        root.add(getBusterDoc("buster2", 26, 20));
        root.add(getBusterDoc("buster3", 36, 30));
        root.add(getBusterDoc("buster4", 46, 40));
        root.add(getBusterDoc("buster5", 56, 50));
        return root;
    }

    /**
     * Init market data to database
     */
    private static void initMarket() {
        if (collection.count() == 0) {
            collection.insertMany(getContentForBuster());
        }
    }

    /** Get all market product
     * @return  {@link List<Document>}
     */
    public static List<Document> getAllMarket() {
        List<Document> market = new ArrayList<>();
        initMarket();
        FindIterable<Document> documents = collection.find();
        for(Document doc : documents) {
            market.add(doc);
        }
        return market;
    }

    /** Get Product by Name
     * @param name {@link String}
     * @return {@link Document}
     */
    public static Document getProductByName(String name) {
        assert (name != null);
        Document product = collection.find(eq("name", name)).first();
        return product;
    }

}
