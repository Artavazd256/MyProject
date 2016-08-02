package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
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

    private HttpServletRequest request;
    private HttpServletResponse response;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("POST");
    }

    private void main(String from) {
        try {
            String levels = getLevels(response);
            Utils.sendMessage(response, levels);
        } catch (JSONException e) {
            if (Settings.IS_DEBUG) {
                System.err.println(String.format("ERROR from %s: ", from) + e.toString());
                e.printStackTrace();
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("GET");
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
        if (Settings.IS_DEBUG) {
            System.out.println("BBBBBBBBBBBBarev" + allLevels);
        }
        return data.toString();
    }


}
