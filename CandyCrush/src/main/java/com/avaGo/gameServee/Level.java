package com.avaGo.gameServee;

import com.avaGo.gameServee.model.LevelModel;
import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by root on 2/10/16.
 */
@WebServlet(value = "/level", name = "LevelModel")
public class Level extends HttpServlet {
    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, "MyGame");
    private MongoCollection<Document> levels = MongoConnector.getCollection(myGame, "Levels");
    private HttpServletRequest request;
    private HttpServletResponse response;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("POST");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;
        main("GET");
    }

    private void main(String from) {
        try {
            String id = Utils.getIDFromRequest(request, response);
            if (id != null) {
                String levelDoc = null;
                levelDoc = LevelModel.getLevelByID(id).toJson();
                assert (levelDoc != null) : "The levelDoc is null";
                Utils.sendMessage(response, levelDoc);
            }
        } catch (IOException e) {
            if (Settings.IS_DEBUG) {
                System.out.println(String.format("ERROR from %s: ", from));
                e.printStackTrace();
            }
        }

    }


}
