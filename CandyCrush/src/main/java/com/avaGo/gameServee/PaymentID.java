package com.avaGo.gameServee;

import com.avaGo.gameServee.model.PaymentStatus;
import com.avaGo.gameServee.model.ProductModel;
import com.avaGo.gameServee.model.MongoConnector;
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

/**
 * Created by artavzd on 8/4/16.
 */
@WebServlet(name = "PaymentID", urlPatterns = "/PaymentID")
public class PaymentID extends HttpServlet {
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

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

    void main(String from)  {
        String url = String.format(Settings.URL_REAL, request.getScheme(), request.getServerName(), request.getServerPort());
        ProductModel.initMarket(url);
        String uid = request.getParameter("uid");
        if (!Utils.isNull(uid)) {
            String requestID = PaymentStatus.insertNewRecord(uid);
            try {
                Utils.sendMessage(response, ProtocolsOutput.getRequestID(requestID));
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    System.err.println(String.format("ERROR from %s: %s",from, e.toString()));
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "The uid is field is null"));
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    System.err.println(String.format("ERROR from %s: %s",from, e.toString()));
                    e.printStackTrace();
                }
            }
        }
    }
}
