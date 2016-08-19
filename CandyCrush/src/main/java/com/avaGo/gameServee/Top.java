package com.avaGo.gameServee;

import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by artavzd on 8/18/16.
 */
@WebServlet(name = "Top", urlPatterns = "/topLevels")
public class Top extends HttpServlet {
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
        FindIterable<Document> topLevel = UserModel.getTopLevel(100);
        try {
            String topLevels = ProtocolsOutput.getTopLevels(topLevel);
            Utils.sendMessage(response, topLevels);
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                System.err.println(String.format("ERROR %s from: %s", from, e.toString()));
                e.printStackTrace();
            }
        }
    }

}
