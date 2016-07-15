package com.annaniks.gameServee;

import com.annaniks.gameServee.model.MongoConnector;
import com.annaniks.gameServee.utils.Utils;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.Document;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by root on 2/6/16.
 */
@WebServlet(value = "/login", name = "Login")
public class Login extends HttpServlet {
    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private MongoCollection<Document> collection = MongoConnector.getCollection(myGame, "Users");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        handler(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        handler(request, response);
    }

    private void handler(HttpServletRequest request, HttpServletResponse response) {
        String uid = request.getParameter("uid");
        if(Utils.isNull(uid)) {
            Utils.sendMessage(response, "The protocol is incorrect");
            return;
        }
        createUser(uid);
    }

    private void createUser(String uid) {
        Document userDocument = Utils.getUserDocument(uid);
        collection.insertOne(userDocument);
    }
}
