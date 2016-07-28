package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.UtilsMongo;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by root on 2/6/16.
 */
@WebServlet(value = "/login", name = "Login")
public class Login extends HttpServlet {
    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private MongoCollection<Document> collection = MongoConnector.getCollection(myGame, "Users");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            handler(request, response);
        } catch (JSONException e) {
            if (Settings.DEBUG) {
                System.out.println("From doGet" + e.toString());
                e.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            handler(request, response);
        } catch (JSONException e) {
            if (Settings.DEBUG) {
                System.out.println("From doPost" + e.toString());
                e.printStackTrace();
            }
        }
    }

    private void handler(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        String uid = request.getParameter("uid");
        if(Utils.isNull(uid)) {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR));
            return;
        }
        if(!UtilsMongo.isUserExists(uid, collection)) {
            createUser(uid);
        }
        sendUserInfo(uid, response);
    }



    private void sendUserInfo(String uid, HttpServletResponse response) throws JSONException {
        BasicDBObject query = new BasicDBObject();
        query.put("uid", uid);
        Document first = collection.find(query).first();
        String userInfo = ProtocolsOutput.getUserInfo(first);
        Utils.sendMessage(response, userInfo);
    }

    private void createUser(String uid) {
        Document userDocument = Utils.getUserDocument(uid);
        collection.insertOne(userDocument);
    }

}
