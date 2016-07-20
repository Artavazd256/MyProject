package com.annaniks.gameServee;

import com.annaniks.gameServee.model.MongoConnector;
import com.annaniks.gameServee.model.UtilsMongo;
import com.annaniks.gameServee.protocule.ProtocolsInput;
import com.annaniks.gameServee.protocule.ProtocolsOutput;
import com.annaniks.gameServee.setting.Settings;
import com.annaniks.gameServee.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by artavzd on 7/20/16.
 */
@WebServlet(name = "UserInfo", urlPatterns = "/userInfo")
public class UserInfo extends HttpServlet {

    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private MongoCollection<Document> collection = MongoConnector.getCollection(myGame, "Users");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            if (Settings.DEBUG) {
                System.err.println( "ERROR from POST: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            if (Settings.DEBUG) {
                System.err.println( "ERROR from GET: " + e.toString());
                e.printStackTrace();
            }
        }
    }


    private void main(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        String uid = request.getParameter("uid");
        if (!Utils.isNull(uid)) {
            if(UtilsMongo.isUserExists(uid, collection)) {

            } else {
               Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_CODE_USER_NOT_EXISTS));
            }
        } else {
            Utils.sendMessage(response, "The protocol is incorrect");
        }
    }


}
