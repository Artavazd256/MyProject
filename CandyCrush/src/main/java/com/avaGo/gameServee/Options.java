package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MarketModel;
import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import org.bson.Document;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by artavzd on 8/14/16.
 */
@WebServlet(name = "Options", urlPatterns = "/options")
public class Options extends HttpServlet {
    private HttpServletRequest request;
    HttpServletResponse response;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.response = response;
        this.request = request;
        main("POST");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.response = response;
        this.request = request;
        main("GET");
    }

    void main(String from) {
        assert (request != null);
        String uid = request.getParameter("uid");
        assert (uid != null);
        if ( "".equals(uid) ) {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "The uid is empty").toString());
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    e.printStackTrace();
                    System.err.println(String.format("Error from %s: %s ", from, e.toString()));
                }
            }
        } else if( Utils.isNull(uid)) {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "The uid is null").toString());
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    e.printStackTrace();
                    System.err.println(String.format("Error from %s: %s ", from, e.toString()));
                }
            }

        } else {
            if (!UserModel.isUserExist(uid)) {
                try {
                    Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_CODE_USER_NOT_EXISTS, String.format("The %s uid not exists", uid)));
                } catch (JSONException e) {
                    if (Settings.IS_DEBUG) {
                        System.err.println(String.format("Error from %s: %s ", from, e.toString()));
                        e.printStackTrace();
                    }
                }
                return;
            }
            List<Document> allMarket = MarketModel.getAllMarket();
            Document userDoc = UserModel.getUserByUID(uid);
            assert (userDoc != null);
            String language = userDoc.getString("language");
            Double volume = userDoc.getDouble("volume");
            try {
                String options = ProtocolsOutput.getOptions(allMarket, language, volume);
                Utils.sendMessage(response, options);
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
                    e.printStackTrace();
                }
            }

        }
    }
}
