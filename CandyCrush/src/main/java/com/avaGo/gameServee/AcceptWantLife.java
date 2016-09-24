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
 * Created by Artavazd on 9/24/2016.
 */
@WebServlet(name = "AcceptWantLife", urlPatterns = "/AcceptWantLife")
public class AcceptWantLife extends HttpServlet {
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


    /** start accept logic
     * @param from {@link String}
     */
    private void main(String from) {
        String fromUID = request.getParameter("fromUID");
        String toUID = request.getParameter("toUID");
        try {
            if( UserModel.acceptWantLife(fromUID, toUID)) {
                Utils.sendMessage(response, ProtocolsOutput.getUserInfo(UserModel.getUserByUID(fromUID)));
            } else {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "Not accept 'want life'"));
            }
        } catch (JSONException e) {
            if(Settings.IS_DEBUG) {
                System.err.println(String.format("ERROR from %s : %s", from, e.toString()));
                e.printStackTrace();
            }
        }
    }
}
