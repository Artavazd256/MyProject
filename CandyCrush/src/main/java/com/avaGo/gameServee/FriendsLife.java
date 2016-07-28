package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.UtilsMongo;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.MongoClient;
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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

/**
 * Created by artavzd on 7/28/16.
 */
@WebServlet(name = "FriendsLife", urlPatterns = "/getLifeFromFriend")
public class FriendsLife extends HttpServlet {

    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private MongoCollection<Document> collection = MongoConnector.getCollection(myGame, "Users");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void main(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        String uid = request.getParameter("uid");
        String fromUID = request.getParameter("fromUID");
        if (!Utils.isNull(uid) && !Utils.isNull(fromUID)) {
            if (UtilsMongo.isUserExists(uid, collection)) {
                Document userByUID = UtilsMongo.getUserByUID(uid, collection);
                if (isAddLife(uid, fromUID)) {
                    addLife(uid);
                } else {
                    Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_CODE_NOT_ADD_LIFE));
                }
            } else {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_CODE_USER_NOT_EXISTS));
            }
        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR));
        }
    }

    private boolean isAddLife(String uid, String fromUID) {
        Document first = collection.find(and(eq("uid", uid), ne("friendsEventsUIDS", fromUID))).first();
        return Utils.isNull(first);
    }

    private void addLife(String uid) {

    }
}
