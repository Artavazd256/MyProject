package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.UserModel;
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

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by artavzd on 7/27/16.
 */
@WebServlet(name = "UpdateUser", urlPatterns = "/updateUser")
public class UpdateUser extends HttpServlet {

    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private MongoCollection<Document> collection = MongoConnector.getCollection(myGame, "Users");
    private HttpServletRequest request;
    private HttpServletResponse response;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            if(Settings.IS_DEBUG) {
                System.err.println("ERROR: From POST" + e.toString());
                e.printStackTrace();
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            if(Settings.IS_DEBUG) {
                System.err.println("ERROR: From GET" + e.toString());
                e.printStackTrace();
            }
        }
    }


    /**
     * @param request
     * @param response
     * @throws JSONException
     */
    private void main(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        this.request = request;
        this.response = response;
        String uid = request.getParameter("uid");
        Long xp = Long.valueOf(request.getParameter("xp"));
        Long level =  Long.valueOf(request.getParameter("level"));
        Document currentLevel =  Document.parse(request.getParameter("currentLevel"));
        if (!Utils.isNull(currentLevel)
            && !Utils.isNull(xp)
            && !Utils.isNull(level)
            ) {
            updateDoc(uid, level, xp, currentLevel);
        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "The protocol is incorrect"));
        }
    }

    /** Update user information
     * @param uid
     * @param level
     * @param xp
     * @param currentLevel
     * @throws JSONException
     */
    private void updateDoc(String uid, Long level, Long xp, Document currentLevel) throws JSONException {
        boolean status = UserModel.updateDocCustom(uid, level, xp, currentLevel);
        if (status) {
            Utils.sendMessage(response, ProtocolsOutput.statusOk("UserUpdate"));
        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(5, String.format("User %s user data not updated", uid)));
        }
    }

}
