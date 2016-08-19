package com.avaGo.gameServee.protocule;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by artavzd on 7/16/16.
 */
public class ProtocolsOutput {

    public static String getUserInfo(Document userInfo) throws JSONException {
        JSONObject all = new JSONObject();
        JSONObject data = new JSONObject(userInfo);
        all.put("userInfo", data);
        return all.toString();
    }

    public static String errorCode(int errorCode) throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("error", String.valueOf(errorCode));
        result.put("statis", data);
        return result.toString();
    }

    public static String errorCode(int errorCode, String msg) throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("msg", msg);
        data.put("code", String.valueOf(errorCode));
        result.put("errorStatus", data);
        return result.toString();
    }

    public static String statusOk(String actionName) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(actionName, "ok");
        return result.toString();
    }

    /**
     * @param doc
     * @return
     * @throws JSONException
     */
    public static JSONObject getPaymentResponse(Document doc) throws JSONException {
        JSONObject responseData = new JSONObject();
        JSONObject content = new JSONObject();
        content.put("amount", doc.getString("amount"));
        content.put("product", doc.getString("url"));
        content.put("currency", "USD"); // the value is not set in database
        responseData.put("method", "payments_get_item_price");
        responseData.put("content", content);
        return responseData;
    }

    /** get request id
     * @param requestID Integer
     * @return JSONObject
     * @throws JSONException
     */
    public static String getRequestID(String requestID) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("requestID", requestID);
        return data.toString();
    }

    public static String getOptions(List<Document> market, String language, Double volume) throws JSONException {
        assert (market != null);
        assert (language != null);
        assert (volume != null);
        JSONObject result = new JSONObject();
        JSONArray mA = new JSONArray();
        for (Document doc : market) {
            doc.remove("_id");
            mA.put(new JSONObject(doc.toJson()));
        }
        result.put("market", mA);
        JSONObject settings = new JSONObject();
        settings.put("language", language);
        settings.put("volume", volume);
        result.put("settings", settings);
        return result.toString();
    }

    /** Get buster
     * @param productDoc {@link Document}
     * @return {@link String}
     * @throws JSONException
     */
    public static String buster(Document productDoc) throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject buster = new JSONObject(productDoc.toJson());
        result.put("buster", buster);
        return result.toString();
    }

    /** warning
     * @param warningProductNeedToAddInCode {@link int}
     * @param msg {@link String}
     * @return {@link String}
     * @throws JSONException
     */
    public static String warning(int warningProductNeedToAddInCode, String msg) throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("msg", msg);
        data.put("code", String.valueOf(warningProductNeedToAddInCode));
        result.put("WarningStatus", data);
        return result.toString();
    }

    /** Get top levels
     * @param topLevel {@link FindIterable<Document>}
     * @return {@link String}
     * @throws JSONException
     */
    public static String getTopLevels(FindIterable<Document> topLevel) throws JSONException {
        JSONObject result = new JSONObject();
        JSONArray top100 = new JSONArray();
        for (Document doc : topLevel) {
            JSONObject userDoc = new JSONObject();
            userDoc.put("xp", doc.getInteger("xp"));
            userDoc.put("uid", doc.getString("uid"));
            top100.put(userDoc);
        }
        result.put("topLevels", top100);
        return result.toString();
    }
}
