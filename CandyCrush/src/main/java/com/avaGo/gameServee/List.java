package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by root on 2/10/16.
 */
@WebServlet(value = "/list",name = "List")
public class List extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            outputStream.write(getLevels(response).getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            outputStream.write(getLevels(response).getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    private String getLevels(HttpServletResponse response) throws JSONException {
        MongoClient mongoClient = MongoConnector.getMongoClient();
        MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, "MyGame");
        MongoCollection<Document> levels = MongoConnector.getCollection(myGame, "Levels");
        JSONArray allLevels = new JSONArray();
        for(Document doc : levels.find()) {
            String id = doc.getObjectId("_id").toString();
            String level = doc.getString("level");
            JSONObject levelInfo  = new JSONObject();
            levelInfo.put("_id", id);
            levelInfo.put("level", level);
            allLevels.put(levelInfo);
        }
        JSONObject data = new JSONObject();
        data.put("levels", allLevels);
        System.out.println("BBBBBBBBBBBBarev" + allLevels);
        return data.toString();
    }


}
