package com.avaGo.gameServee.protocule;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

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

}
