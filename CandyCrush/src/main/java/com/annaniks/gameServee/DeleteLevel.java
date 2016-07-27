package com.annaniks.gameServee;

import com.annaniks.gameServee.model.MongoConnector;
import com.annaniks.gameServee.protocule.ProtocolsOutput;
import com.annaniks.gameServee.setting.Settings;
import com.annaniks.gameServee.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jdk.nashorn.internal.runtime.regexp.RegExp;
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
@WebServlet(value = "/deleteLevel", name = "DeleteLevel")
public class DeleteLevel extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        try {
            deleteLevel(id, response);
        } catch (JSONException e) {
            if(Settings.DEBUG) {
                System.err.println("from POST " + e.toString());
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        try {
            deleteLevel(id, response);
        } catch (JSONException e) {
            if(Settings.DEBUG) {
                System.err.println("from GET" + e.toString());
                e.printStackTrace();
            }
        }

    }

    private void deleteLevel(String id, HttpServletResponse response) throws IOException, JSONException {
        if (ObjectId.isValid(id)) {
            MongoClient mongoClient = MongoConnector.getMongoClient();
            MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, "MyGame");
            MongoCollection<Document> levels = MongoConnector.getCollection(myGame, "Levels");
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            levels.deleteOne(query);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(ProtocolsOutput.statusOk("deleted").getBytes());
            outputStream.flush();
            outputStream.close();
        } else {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(String.format("ERROR: invalid  %s_id", id).getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

}
