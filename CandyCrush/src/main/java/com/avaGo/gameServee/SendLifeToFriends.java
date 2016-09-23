package com.avaGo.gameServee;

import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by artavzd on 7/28/16.
 */
@WebServlet(name = "sendLifeToFriend", urlPatterns = "/SendLifeToFriend")

public class SendLifeToFriends extends HttpServlet {

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
            if( UserModel.sendLifeEventToFriend(fromUID, toUID)) {
                Utils.sendMessage(response, ProtocolsOutput.getUserInfo(UserModel.getUserByUID(fromUID)));
            } else {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "Not send life to friend"));
            }

        } else {
            Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR));
        }
    }

}
