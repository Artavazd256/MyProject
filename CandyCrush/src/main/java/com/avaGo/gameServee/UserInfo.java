package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.model.UtilsMongo;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

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
            if (Settings.IS_DEBUG) {
                System.err.println( "ERROR from POST: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                System.err.println( "ERROR from GET: " + e.toString());
                e.printStackTrace();
            }
        }
    }


    private void main(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        String uid = request.getParameter("uid");
        if (!Utils.isNull(uid)) {
            if(UtilsMongo.isUserExists(uid, collection)) {
                UserModel.UpdateForeverLifeTime(uid);
                FindIterable<Document> doc = collection.find(eq("uid", uid)).limit(1);
                Document first = doc.first();
                Utils.sendMessage(response, first.toJson());
            } else {
               Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_CODE_USER_NOT_EXISTS));
            }
        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR));
        }
    }


}
