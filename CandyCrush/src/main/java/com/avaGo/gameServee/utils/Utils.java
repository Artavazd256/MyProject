package com.avaGo.gameServee.utils;

import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.mongodb.BasicDBList;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            if (Settings.IS_DEBUG) {
                e.printStackTrace();
                System.out.print(String.format("ERROR: %s", e.toString()));
            }
        }
    }
}
