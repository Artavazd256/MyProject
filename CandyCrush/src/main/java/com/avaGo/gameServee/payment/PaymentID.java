package com.avaGo.gameServee.payment;

import com.avaGo.gameServee.model.MarketModel;
import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.setting.Settings;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by artavzd on 8/4/16.
 */
@WebServlet(name = "PaymentID", urlPatterns = "/PaymentID")
public class PaymentID extends HttpServlet {
    private static final String COLLECTION_NAME = "PaymentStatus";
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("POST");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("GET");
    }

    void main(String from)  {
        String url = String.format(Settings.URL_REAL, request.getScheme(), request.getServerName(), request.getServerPort());
        MarketModel.initMarket(url);
    }
}
