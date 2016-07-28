package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
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
@WebServlet(value = "/level", name = "Level")
public class Level extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        if (id != null) {
            String levelDoc = null;
            try {
                levelDoc = getLevelDoc(id);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(levelDoc.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        if (id != null) {
            String levelDoc = null;
            try {
                levelDoc = getLevelDoc(id);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(levelDoc.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**  get level doc by id
     * @param id
     * @return
     */
    private String getLevelDoc(String id) throws JSONException {
        String levelDoc = null;
        MongoClient mongoClient = MongoConnector.getMongoClient();
        MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, "MyGame");
        MongoCollection<Document> levels = MongoConnector.getCollection(myGame, "Levels");
        BasicDBObject basicDBObject = new BasicDBObject();
        if( ObjectId.isValid(id) ) {
            basicDBObject.append("_id", new ObjectId(id));
            Document first = levels.find(basicDBObject).first();
            if (first == null) {
                return String.format("ERROR: the %s _id not exists", id);
            }
            levelDoc = first.toJson();
            MongoConnector.closeMongo(mongoClient);
        } else {
            return "ERROR: invalid _id";
        }
        JSONObject level = new JSONObject();
        level.put("level", new JSONObject(levelDoc.toString()));
        level.getJSONObject("level").put("_id", id);
        return level.toString();
    }

}
