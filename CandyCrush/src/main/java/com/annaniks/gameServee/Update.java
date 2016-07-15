package com.annaniks.gameServee;

import com.annaniks.gameServee.model.MongoConnector;
import com.annaniks.gameServee.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.*;

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
@WebServlet(value = "/update", name = "Update")
public class Update extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        assert (id != null);
        String doc = Utils.getDoc(request, response);
        System.out.println("doc = " + doc);
        assert (doc != null);
        updateDoc(id, doc);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = Utils.getIDFromRequest(request, response);
        assert (id != null);
        String doc = Utils.getDoc(request, response);
        System.out.println("doc = " + doc);
        assert (doc != null);
        updateDoc(id, doc);
    }

    private void updateDoc(String id, String doc) {
        MongoClient mongoClient = MongoConnector.getMongoClient();
        MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, "MyGame");
        MongoCollection<Document> levels = MongoConnector.getCollection(myGame, "Levels");
        BasicDBObject query = new BasicDBObject();
        Document insertDoc = Document.parse(doc);
        insertDoc.remove("_id");
        levels.findOneAndReplace(eq("_id", new ObjectId(id)), insertDoc);
        MongoConnector.closeMongo(mongoClient);
    }


}
