package com.annaniks.gameServee.protocule;

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
        data.put("error", String.valueOf(errorCode));
        data.put("msg", msg);
        result.put("statis", data);
        return result.toString();
    }

    public static String statusOk(String actionName) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(actionName, "ok");
        return result.toString();
    }


}
