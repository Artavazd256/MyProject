package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.UtilsMongo;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;

/**
 * Created by artavzd on 7/28/16.
 */
@WebServlet(name = "FriendsLife", urlPatterns = "/getLifeFromFriend")
public class FriendsLife extends HttpServlet {

    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private MongoCollection<Document> collection = MongoConnector.getCollection(myGame, "Users");
    private HttpServletRequest request;
    HttpServletResponse response;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.request = request;
            this.response = response;
            main(request, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.request = request;
            this.response = response;
            main(request, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void main(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        String uid = request.getParameter("uid");
        String uidFrom = request.getParameter("uidFrom");
        if (!Utils.isNull(uid) && !Utils.isNull(uidFrom)) {
            if (UtilsMongo.isUserExists(uid, collection)) {
                if (isAddLife(uid, uidFrom)) {
                    addLife(uid, uidFrom);
                    Utils.sendMessage(response, ProtocolsOutput.getUserInfo(UtilsMongo.getUserByUID(uid, collection)));
                } else {
                    Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_CODE_NOT_ADD_LIFE, "Not add new life before you get the life"));
                }
            } else {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_CODE_USER_NOT_EXISTS, "The user not exists"));
            }
        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR));
        }
    }

    private boolean isAddLife(String uid, String fromUID) {
        long time = System.currentTimeMillis();
        Document first = collection.find(and(eq("uid", uid), eq("friendsEventsUIDS.uid", fromUID))).first();
        if (Utils.isNull(first)) {
            return true;
        }
        first = collection.find(and(eq("uid", uid), eq("friendsEventsUIDS.uid", fromUID), lte("friendsEventsUIDS.date", time))).first();
        return !Utils.isNull(first);
    }

    private void addLife(String uid, String uidFrom) {
        long date = System.currentTimeMillis() + Settings.TIME_OF_GET_LIFE_FROM_FRIEND;
        UpdateResult updateResult = collection.updateOne(and(eq("uid", uid),
                eq("friendsEventsUIDS.uid", uidFrom)),
                new BasicDBObject("$set"
                        , new BasicDBObject("friendsEventsUIDS.$"
                        , new BasicDBObject()
                        .append("uid", uidFrom)
                        .append("date", date))));
        if( !Utils.isNull(updateResult) && updateResult.getMatchedCount() == 0) {
            collection.updateOne(eq("uid", uid), new BasicDBObject("$push", new BasicDBObject("friendsEventsUIDS", new BasicDBObject().append("uid", uidFrom).append("date", date))));
        }
        collection.updateOne(lt("life", Settings.MAX_LIFE), new BasicDBObject("$inc", new BasicDBObject("life", 1)));

    }
}
