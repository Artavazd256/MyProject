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
import javax.xml.bind.helpers.AbstractMarshallerImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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

    /**
     *  Get access token from facebook
     * @param url String
     * @return String if get access token return access token else return null
     * @throws IOException
     */
    public static String getContentOFURL(String url) throws IOException {
        String accessToken = null;
        URL u = new URL(url);
        InputStream inputStream = u.openStream();
        StringBuffer buffer = new StringBuffer();
        int b;
        while ((b = inputStream.read()) != -1) {
            buffer.append((char)b);
        }
        if (buffer.length() != 0 ) {
            accessToken = buffer.toString();
        }
        return accessToken;
    }

    /** Check object is null or empty
     * @param uid {@link String}
     * @return {@link Boolean}
     */
    public static boolean isNullOrEmpty(String uid) {
        if ("".equals(uid) || null == uid) {
            return true;
        }
        return false;
    }

    /** Check parameter
     * @param response {@link HttpServletRequest}
     * @param data {@link String}
     * @param paramName {@link String}
     * @param from {@link String}
     * @return
     */
    public static boolean checkParameter(HttpServletResponse response, String data, String paramName, String from) {
        if (isNullOrEmpty(data)) {
            try {
                Utils.sendMessage(response, ProtocolsOutput.errorCode(Settings.PROTOCOL_ERROR, String.format("The %s parameter is null or empty", paramName)));
                return false;
            } catch (JSONException e) {
                if (Settings.IS_DEBUG) {
                    e.printStackTrace();
                    System.err.println(String.format("ERROR %s from: %s", from, e.toString()));
                }
            }
            return false;
        }
        return true;
    }

    /** Calculate  left time
     * @param t {@link Long}
     * @return {@link Long}  seconds
     */
    public static long calculateLeftTime(long t) {
        long leftTime = t - System.currentTimeMillis();
        leftTime = leftTime < 0 ? 0L : leftTime / 1000;
        return leftTime;
    }
}
