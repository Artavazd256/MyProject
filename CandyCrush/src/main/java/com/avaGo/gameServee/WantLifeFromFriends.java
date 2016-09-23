package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.UserModel;
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
@WebServlet(name = "WantLifeFromFriends", urlPatterns = "/WantLifeFromFriend")
public class WantLifeFromFriends extends HttpServlet {

    private HttpServletRequest request;
    private HttpServletResponse response;

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
        String toUID = request.getParameter("toUID");
        String fromUID = request.getParameter("fromUID");
        if (!Utils.isNull(toUID) && !Utils.isNull(fromUID)) {
            if( UserModel.wantLifeEventToFriend(fromUID, toUID)) {
                Utils.sendMessage(response, ProtocolsOutput.getUserInfo(UserModel.getUserByUID(fromUID)));
            } else {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "Not send life to friend"));
            }

        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR));
        }
    }

}
