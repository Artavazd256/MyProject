package com.avaGo.gameServee;

import com.avaGo.gameServee.model.MongoConnector;
import com.avaGo.gameServee.setting.Settings;
import com.avaGo.gameServee.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

import static com.mongodb.client.model.Filters.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by root on 2/10/16.
 */
@WebServlet(value = "/updateLevel", name = "UpdateLevel")
public class UpdateLevel extends HttpServlet {
    private MongoClient mongoClient = MongoConnector.getMongoClient();
    private MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, "MyGame");
    private MongoCollection<Document> levels = MongoConnector.getCollection(myGame, "Levels");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        assert (id != null);
        String doc = null;
        try {
            doc = Utils.getParam(request, response, "doc");
        } catch (JSONException e) {
            if(Settings.DEBUG) {
                System.err.println("from POST" + e.toString());
                e.printStackTrace();
            }
        }
        if(Settings.DEBUG) {
            System.out.println("doc from POST = " + doc);
        }
        assert (doc != null);
        updateDoc(id, doc);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        assert (id != null);
        String doc = null;
        try {
            doc = Utils.getParam(request, response, "doc");
        } catch (JSONException e) {
            if(Settings.DEBUG) {
                System.err.println("from GET " + e.toString());
                e.printStackTrace();
            }
        }
        if(Settings.DEBUG) {
            System.out.println("doc from GET = " + doc);
        }
        assert (doc != null);
        updateDoc(id, doc);
    }

    private void updateDoc(String id, String doc) {
        BasicDBObject query = new BasicDBObject();
        Document insertDoc = Document.parse(doc);
        insertDoc.remove("_id");
        levels.findOneAndReplace(eq("_id", new ObjectId(id)), insertDoc);
        //MongoConnector.closeMongo(mongoClient);
    }


}
