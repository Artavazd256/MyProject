package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MarketModel;
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
 * Created by artavzd on 8/15/16.
 */
@WebServlet(name = "Market", urlPatterns = "/market")
public class Market extends HttpServlet {
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
        String productName = request.getParameter("productName");
        if (Utils.isNullOrEmpty(uid)) {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "The uid not defined"));
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
                    e.printStackTrace();
                }
            }
            return;
        }
        if (Utils.isNullOrEmpty(productName)) {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, "The productName not defined"));
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
                    e.printStackTrace();
                }
            }
            return;

        }
        Document productDoc = MarketModel.getProductByName(productName);
        if (!Utils.isNull(productDoc)) {
            Integer coin = productDoc.getInteger("coin");
            boolean status = UserModel.isUserGreaterCoin(uid, coin);
            if (status) {
                switch (productName) {
                    case "buster1":
                    case "buster2":
                    case "buster3":
                    case "buster4":
                    case "buster5":
                        addBuster(productName, productDoc, uid, coin, from);
                        break;
                    default:
                        try {
                            Utils.sendMessage(response, ProtocolsOutput.warning(Settings.WARNING_PRODUCT_NEED_TO_ADD_IN_CODE, String.format("The %s product need to add in code of server", productName)));
                        } catch (JSONException e) {
                            if (Settings.IS_DEBUG) {
                                System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
                                e.printStackTrace();
                            }
                        }
                }


            } else {
                try {
                    Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_PRODUCT_COINS, String.format("Your coins not enough", productName)).toString());
                } catch (JSONException e) {
                    if (Settings.IS_DEBUG) {
                        System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
                        e.printStackTrace();
                    }
                }
            }

        } else {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_PRODUCT_DOESNT_EXIST, String.format("The %s product doesn't exist", productName)).toString());
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
                    e.printStackTrace();
                }
            }
        }

    }

    /** Add buster to user
     * @param productName {@link String}
     * @param productDoc {@link  Document }
     * @param uid {@link String}
     * @param coin {@link Integer}
     * @param from {@link String}
     */
    private void addBuster(String productName, Document productDoc, String uid, Integer coin, String from) {
        UserModel.decrementCoins(uid, coin);
        UpdateResult updateResult = UserModel.addBuster(uid, productDoc);
        try {
            if (updateResult.getModifiedCount() != 0) {
                Document userDoc = UserModel.getUserByUID(uid);
                Utils.sendMessage(response, ProtocolsOutput.getUserInfo(userDoc));
            } else {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.ERROR_PRODUCT_DID_NOT_ADD, String.format("The %s product dis not add", productName)));
            }
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                e.printStackTrace();
                System.err.println(String.format("ERROR from %s: %s", from, e.toString()));
            }
        }
    }

}
