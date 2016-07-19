package com.annaniks.gameServee.protocule;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by artavzd on 7/16/16.
 */
public class Protocols {

    public static String getUserInfo(Document userInfo) throws JSONException {
        JSONObject all = new JSONObject();
        JSONObject data = new JSONObject(userInfo);
        all.put("userInfo", data);
        return all.toString();
    }
}
