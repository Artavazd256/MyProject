package com.annaniks.gameServee.utils;

import com.annaniks.gameServee.protocule.ProtocolsOutput;
import com.annaniks.gameServee.setting.Settings;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/17/16.
 */
public class Utils {
    public static String getIDFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = null;
        id = request.getParameter("_id");
        if (!ObjectId.isValid(id)) {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write("ERROR: the _id argument not found".getBytes());
            outputStream.flush();
            outputStream.close();
        }
        return id;
    }

    public static String getParam(HttpServletRequest request, HttpServletResponse response, String key) throws IOException, JSONException {
        String data = request.getParameter("doc");
        if ("".equals(data) || data == null) {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(ProtocolsOutput.errorCode(3, String.format("The %s key is not defined", key)).getBytes());
            outputStream.flush();
            outputStream.close();
        }
        return data;
    }

    public static Document getUserDocument(String uid) {
        Document currentLevelsXP = new Document();
        Document  friendsEventsUIDS = new Document();
        Document user = new Document();
        user.put("uid", uid);
        user.put("xp", 0);
        user.put("currentLevelsXP", currentLevelsXP);
        user.put("level", 0);
        user.put("createDate", System.currentTimeMillis());
        user.put("lastVisitDate", System.currentTimeMillis());
        user.put("lifeMax", 5);
        user.put("life", 5);
        user.put("lifeTime", 1800);
        user.put("lifeStartTime", System.currentTimeMillis());
        user.put("foreverLifeTime", 0);
        user.put("friendsEventsUIDS", friendsEventsUIDS);
        return user;
    }

    public static boolean isNull(Object obj) {
        if (null == obj) {
            return true;
        }
        return false;
    }

    public static void sendMessage(HttpServletResponse response, String msg) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(msg.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            if (Settings.DEBUG) {
                e.printStackTrace();
                System.out.print(String.format("ERROR: %s", e.toString()));
            }
        }
    }
}
