package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.model.UtilsMongo;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
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
    private HttpServletRequest request;
    private HttpServletResponse response;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.request = request;
        this.response = response;
        main("GET");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        main("POST");
    }

    private void main(String from) {
        String uid = request.getParameter("uid");
        try {
            if(Utils.isNull(uid)) {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR));
                return;
            }
            if(!UtilsMongo.isUserExists(uid, collection)) {
                UserModel.createUser(uid);
                UserModel.addDailyBonus(uid);
            } else {
                UserModel.updateForeverLifeTime(uid);
                UserModel.updateLastVisitDate(uid);
                UserModel.addDailyBonus(uid);
                UserModel.updateLife(uid);
            }
            sendUserInfo(uid);
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                System.out.println(String.format("ERROR From %s: ", from) + e.toString());
                e.printStackTrace();
            }
        }
    }


    private void sendUserInfo(String uid) throws JSONException {
        Document user = UserModel.getUserByUID(uid);
        String userInfo = ProtocolsOutput.getUserInfo(user);
        Utils.sendMessage(response, userInfo);
    }

}
