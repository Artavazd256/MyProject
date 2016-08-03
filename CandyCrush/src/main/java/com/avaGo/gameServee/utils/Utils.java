package com.avaGo.gameServee.utils;

import com.avaGo.gameServee.protocule.ProtocolsOutput;
import com.avaGo.gameServee.setting.Settings;
import com.mongodb.BasicDBList;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

    /** split data to encoded_sig  and payload parts
     * @param data
     * @return
     */
    public static Map<String,String> splitData(String data) {
        Map<String, String> r = new HashMap<String,String>();
        String[] split = data.split("\\.");
        String  encodedSig = split[0];
        String  payload =  split[1];
        r.put("encoded_sig", encodedSig);
        r.put("payload", payload);
        return r;
    }

    /** encode payment data
     * @param data String
     * @return String decode data
     */
    public static String base64UrlDecode(String data) {
        byte[] decode = Base64.getUrlDecoder().decode(data.getBytes());
        String r = DatatypeConverter.printHexBinary(decode);
        return r;
    }

    /**
     * base64 decode
     * @param data String
     * @return String
     */
    public static String base64Decode(String data) {
        byte[] decode = Base64.getDecoder().decode(data.getBytes());
        String r = new  String(decode);
        return r;
    }

    /**
     * Hash-based message authentication code
     * https://en.wikipedia.org/wiki/Hash-based_message_authentication_code
     * @param msg String message
     * @param keyString String key
     * @param algorithm String HMACMD5, HMACSHA1, HMACSHA256
     * @return String hex number or null when algorithm type is incorrect
     */
    public static String hmacDigest(String msg, String keyString, String algorithm) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }

    /**
     * String to hex String
     * @param msg String data
     * @return String hexString or null
     */
    public static String StringToHexString(String msg) {
        String r = DatatypeConverter.printHexBinary(msg.getBytes());
        return r;
    }


}
