package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.model.PaymentStatus;
import com.avaGo.gameServee.model.ProductModel;
import com.avaGo.gameServee.model.UserModel;
import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.Doc;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

/**
 * Created by Artavazd on 3/20/16.
 */
@WebServlet(name = "payment", urlPatterns = "/payment")
public class Payment extends HttpServlet {

    private static final String COLLECTION_NAME = "Users";
    private static final MongoClient mongoClient = MongoConnector.getMongoClient();
    private static final MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, MongoConnector.DATA_BASE_NAME);
    private static final MongoCollection<Document> collection = MongoConnector.getCollection(myGame, COLLECTION_NAME);

    private final String APPLICATION_ID = "890780287714545"; // Realise
    private final String APPLICATION_SECRET = "3514d75f1fcacd32a92c298cb1a85afb"; // beta

    private final String TOKEN = "315320be42724ccd038f7c68da29a49f"; // beta and develop
    //private final String APPLICATION_ID = "866553290046720";
    //private final String APPLICATION_SECRET = "f89717ae5a82388fbf7ca25a4898b649";
    private final String TOKEN_URL = String.format("https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials",APPLICATION_ID, APPLICATION_SECRET);
    private final String URL_CHECK_PAYMENT_STATUS = "https://graph.facebook.com/%s?fields=actions,application,country,created_time,disputes,fulfillment_status,id,is_from_ad,fraud_status,is_from_page_post,items,payout_foreign_exchange_rate,phone_support_eligible,refundable_amount,request_id,tax,tax_country,test,user&%s";
    private HttpServletRequest request;
    private HttpServletResponse response;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        showArguments(request);
        try {
            JSONObject paymentObject = readContent(request);
            if ( paymentObject != null) {
                System.out.println(paymentObject.toString());
                processingContentAndCheckStatusOfPayment(paymentObject);
            } else {
                if (inputParametersValidatorForPayment(request)) {
                    try {
                        JSONObject data = parseSignedRequest(request.getParameter("signed_request"));
                        assert (data != null);
                        System.out.println("JSONData = " + data.toString()); // TODO need remove this line
                        JSONObject responseData = getResponseData(request);
                        Double amount = responseData.getJSONObject("content").getDouble("amount");
                        writePaymentSessionIntoDataBase(data, amount.floatValue());
                        assert (responseData != null);
                        sendMessage(response, responseData.toString());
                    } catch (JSONException e) {
                        Logger.getLogger("Aratta Payment").severe(e.getMessage());
                        return;
                    }
                }
            }
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                e.printStackTrace();
            }
            return;
        } catch (ClassNotFoundException e) {
            if (Settings.IS_DEBUG) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            if (Settings.IS_DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * processing content and check status of payment
     * @param paymentObject JSONObject
     */
    private void processingContentAndCheckStatusOfPayment(JSONObject paymentObject) throws JSONException, IOException, SQLException, ClassNotFoundException {
        assert (paymentObject != null);
        JSONArray entry = paymentObject.getJSONArray("entry");
        for (int i = 0; i < entry.length(); i++) {
            String paymentID = entry.getJSONObject(i).getString("id");
            assert (paymentID != null);
            JSONObject paymentSessionJSONData = getPaymentSessionJSONData(paymentID);
            assert (paymentSessionJSONData != null);
            changPaymentStatusAndAddProductToUser(paymentSessionJSONData, paymentID);

        }
    }

    /**
     * change
     * @param paymentSessionJSONData
     * @param paymentID
     */
    private void changPaymentStatusAndAddProductToUser(JSONObject paymentSessionJSONData, String paymentID) throws JSONException, SQLException, IOException, ClassNotFoundException {
        assert (paymentSessionJSONData != null);
        JSONArray actions = paymentSessionJSONData.getJSONArray("actions");
        String requestID = paymentSessionJSONData.getString("request_id");
        Document doc = PaymentStatus.getPaymentStatusDocByPID(requestID);
        if (checkPaymentStatus(actions)) {
            doc.put("status", true);
        } else {
            doc.put("status", false);
            PaymentStatus.updatePaymentStatusByPID(requestID, doc);
            return;
        }
        addProductToUser(doc, paymentSessionJSONData);
    }


    private void addProductToUser(Document doc, JSONObject paymentSessionJSONData) throws JSONException, SQLException, IOException, ClassNotFoundException {
        String productURL = paymentSessionJSONData.getJSONArray("items").getJSONObject(0).getString("product");
        String uid = paymentSessionJSONData.getJSONObject("user").getString("id");
        String url = String.format(Settings.URL, request.getScheme() ,request.getServerName(), request.getServerPort(), "" );
        if ((url + "/10_step.jsp").equalsIgnoreCase(productURL)) {
            add10Step(doc, uid);
        } else if (productURL.indexOf("50_coins.jsp") != -1) {
            addCoins(doc, uid);
        } else if (productURL.indexOf("270_coins.jsp") != -1) {
            addCoins(doc, uid);
        } else if (productURL.indexOf("550_coins_forever_life_with_2_hours.jsp") != -1) {
            addCoinsForeverLifeWith2Hours(doc, uid);
        } else if (productURL.indexOf("1150_coins_forever_life_with_2_hours.jsp") != -1) {
            addCoinsForeverLifeWith2Hours(doc, uid);
        } else if (productURL.indexOf("3000_coins_forever_life_with_2_hours.jsp") != -1) {
            addCoinsForeverLifeWith2Hours(doc, uid);
        } else if (productURL.indexOf("6500_coins_forever_life_with_2_hours.jsp") != -1) {
            addCoinsForeverLifeWith2Hours(doc, uid);
        }
    }

    private void addCoinsForeverLifeWith2Hours(Document doc, String uid) throws JSONException {
        List<Document> products = (List<Document>) doc.get("product");
        String coins = products.get(0).getString("coins");
        Long foreverLife = products.get(0).getLong("foreverLife");
        long newForeverLif = System.currentTimeMillis() + ((foreverLife * 7200) * 1000);
        UpdateResult updateResult = collection.replaceOne(and(eq("uid", uid), gt("foreverLife", System.currentTimeMillis())), new Document("$set", new Document("foreverLife", newForeverLif)));
        if (updateResult.getMatchedCount() == 0) {
             newForeverLif = (foreverLife * 7200) * 1000;
            collection.replaceOne(eq("uid", uid), new Document("$inc", new Document("foreverLife", newForeverLif)));
        }
        collection.replaceOne(eq("uid", uid), new Document("$inc", new Document("coins", coins)));
    }


    private void addCoins(Document doc, String uid) {
        List<Document> products = (List<Document>) doc.get("product");
        String coins = products.get(0).getString("coins");
        collection.replaceOne(eq("uid", uid), new Document("$inc", new Document("coins", coins)));
    }

    private void add10Step(Document doc, String uid) {
        List<Document> products = (List<Document>) doc.get("product");
        String step = products.get(0).getString("step");
        collection.replaceOne(eq("uid", uid), new Document("$inc", new Document("step", step)));
    }

    /**
     * Check payment status
     * @param actions
     * @return if status is completed then return  true else false
     * @throws JSONException
     */
    private boolean checkPaymentStatus(JSONArray actions) throws JSONException {
        for (int i = 0; i < actions.length(); i++) {
            String type = actions.getJSONObject(i).getString("type");
            if ("refund".equalsIgnoreCase(type)) {
                return false;
            }
            if ("charge".equalsIgnoreCase(type))  {
                String status = actions.getJSONObject(i).getString("status");
                if ("failed".equalsIgnoreCase(status)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * get access token and payment session content
     * @param paymentID String payment id
     * @return JSONObject payment session info
     */
    private JSONObject getPaymentSessionJSONData(String paymentID) throws IOException, JSONException {
        String accessToken = Utils.getContentOFURL(TOKEN_URL);
        System.out.println("accessToken = " + accessToken);
        String url = String.format(URL_CHECK_PAYMENT_STATUS, paymentID, accessToken);
        String  paymentSessionJSONString = Utils.getContentOFURL(url);
        assert (paymentSessionJSONString != null);
        System.out.println("contentOFURL = " + paymentSessionJSONString);
        JSONObject r = new JSONObject(paymentSessionJSONString);
        return r;
    }



    /**
     * @param request HttpServletRequest
     * @return JSONObject if read content then return JSONObject else null when content not convert to JSONObject
     * @throws IOException when inputs stream is ....
     * @throws JSONException when content not convert to JSONObject
     */
    private JSONObject readContent(HttpServletRequest request) throws IOException, JSONException {
        assert (request != null);
        JSONObject r = null;
        ServletInputStream inputStream = request.getInputStream();
        int b;
        StringBuffer buffer = new StringBuffer();
        while ((b = inputStream.read()) != -1) {
            buffer.append((char)b);
        }
        if (buffer.length() != 0) {
            r = new JSONObject(buffer.toString());
        }
        return r;
    }


    /**
     * Write payment session information into databases by product id
     * @param data JSONObject destruction
     * @param amount float amount of product
     * @throws JSONException
     */
    private void writePaymentSessionIntoDataBase(JSONObject data, float amount ) throws JSONException {
        String request_id = data.getJSONObject("payment").getString("request_id");
        PaymentStatus.updatePaymentStatusByPID(request_id, Document.parse(data.toString()));
    }


    /**
     * Get response data
     * @param request HttpServletRequest
     * @return JSONObject
     * @throws JSONException
     */
    private JSONObject getResponseData(HttpServletRequest request) throws JSONException {
        JSONObject responseData = new JSONObject();
        String product = request.getParameter("product");
        Document doc = ProductModel.getProductByURL(product);
        if(doc != null) {
            responseData = ProtocolsOutput.getPaymentResponse(doc);
        } else {
            if (Settings.IS_DEBUG) {
                System.err.println(String.format("The %s product not exists in database", product));
            }
        }
        return responseData;
    }

    /**
     *  parse Signed request of facebook
     * @param signedRequest String data
     * @return String json
     */
    public JSONObject parseSignedRequest(String signedRequest) throws JSONException {
        Map<String, String> stringStringMap = Utils.splitData(signedRequest);
        String encodedSig = stringStringMap.get("encoded_sig");
        String payload = stringStringMap.get("payload");
        String data = Utils.base64Decode(payload);
        String sig = Utils.base64UrlDecode(encodedSig);
        String expectedSig = Utils.hmacDigest(payload, APPLICATION_SECRET, "HmacSHA256");
        if (!sig.equalsIgnoreCase(expectedSig)) {
            if (Settings.IS_DEBUG) {
                System.err.println("Bad Signed JSON signature!");
            }
            return null;
        }
        return new JSONObject(data);
    }

    /**
     *  Check input parameters is valid
     * @param request HttpServletRequest
     * @return if the parameters is valid then return true else return false
     */
    private boolean inputParametersValidatorForPayment(HttpServletRequest request) {
        String method = request.getParameter("method");
        if ("".equals(method)
            | "null".equalsIgnoreCase(method)
            | !"payments_get_item_price".equalsIgnoreCase(method)) {
            //ArattaLogger.writeLogger(null, Level.SEVERE, "In the method is wrong data");
            return false;
        }
        String signedRequest = request.getParameter("signed_request");
        if ("".equals(signedRequest)
            | "null".equalsIgnoreCase(signedRequest)) {
            //ArattaLogger.writeLogger(null, Level.SEVERE, "In the signed_request is wrong data");
           return false;
        }
        String product = request.getParameter("product");
        if ("".equals(product)
            | "null".equalsIgnoreCase(product)) {
            //ArattaLogger.writeLogger(null, Level.SEVERE, "In the product is wrong data");
            return false;
        }
        String quantity = request.getParameter("quantity");
        if ("".equals(quantity)
            | "null".equalsIgnoreCase(quantity)) {
            //ArattaLogger.writeLogger(null, Level.SEVERE, "In the quantity is wrong data");
            return false;
        }
        String userCurrency = request.getParameter("user_currency");
        if ("".equals(userCurrency)
            | "null".equalsIgnoreCase(userCurrency)) {
            //ArattaLogger.writeLogger(null, Level.SEVERE, "In the user_currency is wrong data");
            return false;
        }
        String requestID = request.getParameter("request_id");
        if ("".equals(requestID)
            | "null".equalsIgnoreCase(requestID)) {
            //ArattaLogger.writeLogger(null, Level.SEVERE, "In the request id is wrong data");
            return false;
        }
        return true;
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        assert (request != null);
        assert (response != null);
        showArguments(request);
        if(inputParametersValidator(request)) {
            String hubChallenge = request.getParameter("hub.challenge");
            sendMessage(response, hubChallenge);
        }
    }

    /**
     * Check input parameters of facebook payments
     * @param request
     * @return
     */
    private boolean inputParametersValidator(HttpServletRequest request) {
        assert (request != null);
        String hubMode = request.getParameter("hub.mode");
        String hubChallenge = request.getParameter("hub.challenge");
        String hubVerifyToken = request.getParameter("hub.verify_token");
        if ("".equalsIgnoreCase(hubMode) || "null".equalsIgnoreCase(hubMode) || !"subscribe".equalsIgnoreCase("subscribe")) {
            return false;
        }
        if ("".equals(hubChallenge) || "null".equalsIgnoreCase(hubChallenge)) {
            return false;
        }
        if ("".equals(hubVerifyToken) || "null".equalsIgnoreCase(hubVerifyToken) || !TOKEN.equalsIgnoreCase(hubVerifyToken)) {
            return false;
        }
        return true;
    }


    /**
     * @param request
     */
    private void showArguments(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        System.out.println("================================start======================================================");
        while (parameterNames.hasMoreElements()) {
            String s = parameterNames.nextElement();
            String value = request.getParameter(s);
            System.out.println(String.format("%s = %s", s, value));
        }
        System.out.println("================================end======================================================");
    }

    /**
     * send message to client
     * @param response HttpServletRequest pointer of client
     * @param msg String data to send
     */
    private void sendMessage(HttpServletResponse response, String msg) {
        assert (response != null);
        assert (msg != null);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(msg.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
