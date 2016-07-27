package com.annaniks.gameServee;

import com.annaniks.gameServee.model.MongoConnector;
import com.annaniks.gameServee.protocule.ProtocolsOutput;
import com.annaniks.gameServee.setting.Settings;
import com.annaniks.gameServee.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            if(Settings.DEBUG) {
                System.err.println("ERROR: From POST" + e.toString());
                e.printStackTrace();
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            main(request, response);
        } catch (JSONException e) {
            if(Settings.DEBUG) {
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
        String userInfoStr = request.getParameter("userInfo");
        if (!Utils.isNull(userInfoStr)) {
            updateDoc(userInfoStr);
        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "The protocole is incorrect"));
        }
    }

    /**
     * @param user
     */
    private void updateDoc(String user) {
        Document userDoc = Document.parse(user);
        String id = userDoc.getString("_id");
        userDoc.remove("_id");
        collection.findOneAndReplace(eq("_id", new ObjectId(id)), userDoc);
        MongoConnector.closeMongo(mongoClient);
    }

}
