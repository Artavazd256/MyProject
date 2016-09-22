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
 * Created by Artavazd on 9/22/2016.
 */
@WebServlet(name = "StartGame", urlPatterns = "/StartGame")
public class StartGame extends HttpServlet {
    private HttpServletRequest request;
    private HttpServletResponse response;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("fromPOST");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("fromGET");
    }

    void main(String from) {
        String uid = request.getParameter("uid");
        try {
            startGame(uid);
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
                e.printStackTrace();
            }
        }
    }

    private void startGame(String uid) throws JSONException {
        UserModel.updateForeverLifeTime(uid);
        if( UserModel.checkUserHaveLife(uid) || UserModel.CheckForeverLifeTimeStatus(uid)) {
            UserModel.decLife(uid);
            Utils.sendMessage(response, ProtocolsOutput.getUserInfo(UserModel.getUserByUID(uid)));
        } else {
            Utils.sendMessage(response, ProtocolsOutput.getNotLife(uid));
        }
    }
}
