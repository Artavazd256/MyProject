package com.annaniks.gameServee;

import com.annaniks.gameServee.model.MongoConnector;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BasicBSONObject;
import org.bson.Document;
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
@WebServlet(value = "/newlevel", name = "Newlevel")
public class Newlevel extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String level = getLevel(request, response);
        if(level != null) {
            insertNewDocument(level, response);
        }
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String level = getLevel(request, response);
        if(level != null) {
            insertNewDocument(level, response);
        }
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private String getLevel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String level = request.getParameter("level");
        if ("".equals(level) || null == level) {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write("ERROR: level is empyt".getBytes());
            outputStream.flush();
            outputStream.close();
        }
        return level;
    }

    /**
     * @param data
     * @param response
     * @throws IOException
     */
    private void insertNewDocument(String data, HttpServletResponse response) throws IOException {
        Document  doc = Document.parse(data);
        doc.remove("_id");
        assert (doc != null);
        MongoClient mongoClient = MongoConnector.getMongoClient();
        MongoDatabase myGame = MongoConnector.getMongoDatabase(mongoClient, "MyGame");
        MongoCollection<Document> collection = MongoConnector.getCollection(myGame, "Levels");
        if (!checkLevelExists(doc, collection)) {
            collection.insertOne(doc);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write("{\"newLevel\":\"created\"}".getBytes());
        } else {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(String.format("ERROR: The %s level exists", doc.getString("level") ).getBytes());
        }
        MongoConnector.closeMongo(mongoClient);
    }

    /**
     * @param doc
     * @param collection
     * @return
     */
    private boolean checkLevelExists(Document doc, MongoCollection<Document> collection) {
        boolean status = false;
        String level  = doc.getString("level");
        BasicDBObject query = new BasicDBObject();
        query.append("level", level);
        Document first = collection.find(query).first();
        if (first != null) {
            status = true;
        }
        return status;
    }
}
