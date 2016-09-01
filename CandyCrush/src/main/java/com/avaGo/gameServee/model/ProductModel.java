package com.avaGo.gameServee.model;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by artavzd on 8/2/16.
 */
public class ProductModel {
    private static final String COLLECTION_NAME = "Products";
    private static MongoClient mongoClient = MongoConnector.getMongoClient();
    private static MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private static MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);

    private static List<Document> getMarketDocs(String url) {
        List<Document> docs = new ArrayList<>();
        Document step10 = new Document();
        // 10 step
        BasicDBList product = new BasicDBList();
        step10.put("amount", "0.79");
        step10.put("url", String.format("%s/10_step.jsp", url));
        product.add(new Document("step", 10));
        step10.put("products", product);
        docs.add(step10);
        // 50 coins
        product = new BasicDBList();
        Document coins50 = new Document();
        coins50.put("amount", "0.99");
        coins50.put("url", String.format("%s/FB/50_coins.jsp", url));
        product.add(new Document("coins", 50));
        coins50.put("products", product);
        docs.add(coins50);
        // 7777 coins
        product = new BasicDBList();
        Document coins7777 = new Document();
        coins7777.put("amount", "9.99");
        coins7777.put("url", String.format("%s/FB/sale.jsp", url));
        product.add(new Document("coins", 7777));
        coins7777.put("products", product);
        docs.add(coins7777);
        // 270 coins
        Document coins270 = new Document();
        coins270.put("amount", "4.99");
        coins270.put("url", String.format("%s/FB/270_coins.jsp", url));
        product.add(new Document("coins", 270));
        coins270.put("products", product);
        docs.add(coins270);
        // 550 coins and forever life with 2 hours
        Document coins550AndForeverLifeWith2Hours = new Document();
        coins550AndForeverLifeWith2Hours.put("amount", "9.99");
        coins550AndForeverLifeWith2Hours.put("url", String.format("%s/FB/550_coins_forever_life_with_2_hours.jsp", url));
        product = new BasicDBList();
        product.add(new Document("coins", "550"));
        product.add(new Document("foreverLife", 2));
        coins550AndForeverLifeWith2Hours.put("products", product);
        docs.add(coins550AndForeverLifeWith2Hours);
        // 1150 coins and forever life with 2 hours
        Document coins1150AndForeverLifeWith2Hours = new Document();
        coins1150AndForeverLifeWith2Hours.put("amount", "19.99");
        product = new BasicDBList();
        product.add(new Document("coins", 1150));
        coins1150AndForeverLifeWith2Hours.put("url", String.format("%s/FB/1150_coins_forever_life_with_2_hours.jsp", url));
        product.add(new Document("foreverLife", 2));
        coins1150AndForeverLifeWith2Hours.put("products", product);
        docs.add(coins1150AndForeverLifeWith2Hours);
        // 1150 coins and forever life with 2 hours
        Document coins3000AndForeverLifeWith2Hours = new Document();
        coins3000AndForeverLifeWith2Hours.put("amount", "49.99");
        product = new BasicDBList();
        product.add(new Document("coins", 3000));
        coins3000AndForeverLifeWith2Hours.put("url", String.format("%s/FB/3000_coins_forever_life_with_2_hours.jsp", url));
        product.add(new Document("foreverLife", 2));
        coins3000AndForeverLifeWith2Hours.put("products", product);
        docs.add(coins3000AndForeverLifeWith2Hours);
        // 6500 coins and forever life with 2 hours
        Document coins6500AndForeverLifeWith2Hours = new Document();
        coins6500AndForeverLifeWith2Hours.put("amount", "99.99");
        coins6500AndForeverLifeWith2Hours.put("url", String.format("%s/FB/6500_coins_forever_life_with_2_hours.jsp", url));
        product = new BasicDBList();
        product.add(new Document("coins", 6500));
        product.add(new Document("foreverLife", 2));
        coins6500AndForeverLifeWith2Hours.put("products", product);
        docs.add(coins6500AndForeverLifeWith2Hours);
        return docs;
    }

    /**
     * @param productURL
     * @return
     */
    public static Document getProductByURL(String productURL) {
        Document doc = collection.find(eq("url", productURL)).first();
        return doc;
    }

    public static void initMarket(String url) {
        MongoCollection<Document> col = myGame.getCollection(COLLECTION_NAME);
        if (col.count() == 0) {
            collection.insertMany(getMarketDocs(url));
        }
    }




}
