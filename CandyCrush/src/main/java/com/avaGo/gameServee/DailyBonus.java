package com.avaGo.gameServee;

import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Artavazd on 8/28/2016.
 */
@WebServlet(name = "DailyBonus", urlPatterns = "/dailyBonus")
public class DailyBonus extends HttpServlet {

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
        try {
            UserModel.giveDailyBonus(uid);
            Utils.sendMessage(response, ProtocolsOutput.getUserInfo(UserModel.getUserByUID(uid)));
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                System.err.println(String.format("Error from %s : %s", from, e.toString()));
                e.printStackTrace();
            }
        }
    }
}
