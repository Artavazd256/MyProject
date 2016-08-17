package com.avaGo.gameServee;

import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by artavzd on 8/17/16.
 */
@WebServlet(name = "Buster", urlPatterns = "/useBuster")
public class Buster extends HttpServlet {
    private HttpServletRequest request;
    private HttpServletResponse response;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("POST");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("GET");

    }

    private void main(String from) {
        String uid = request.getParameter("uid");
        if (!Utils.checkParameter(response, uid, "uid", from)) {
            return;
        }
        String name = request.getParameter("productName");
        if (!Utils.checkParameter(response, name, "productName", from)) {
            return;
        }
        UpdateResult updateResult = UserModel.removeBuster(uid, name);
        if (updateResult.getModifiedCount() != 0) {
            try {
                Document userDoc = UserModel.getUserByUID(uid);
                Utils.sendMessage(response, ProtocolsOutput.getUserInfo(userDoc));
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    e.printStackTrace();
                    System.err.println(String.format("ERROR %s from: %s", from, e.toString()));
                }
            }
        } else {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ACTION_NOT_COMPLETED, "The actions not completed"));
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    e.printStackTrace();
                    System.err.println(String.format("ERROR %s from: %s", from, e.toString()));
                }
            }
        }

    }
}
