package me.wwsun.demo;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;
import com.sun.org.apache.bcel.internal.generic.LNEG;
import me.wwsun.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Weiwei on 12/23/2014.
 */
public class Demo2 {
    public static void main(String[] args) throws UnknownHostException {
        final String mongoURIString = "mongodb://223.3.80.243:27017";
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));

        final DB siteDatabase = mongoClient.getDB("sample");

        LinkDAO linkDAO = new LinkDAO(siteDatabase);
        //DBObject overviewGraph = linkDAO.getLinksByNodeName("www.made-in-china.com/", 2, 100);
        DBObject overviewGraph = linkDAO.getOverviewGraph(2, 100);

        Path mainPage = null;
        try {
            mainPage = Paths.get("./target/classes/public/data/overview-graph.json").toRealPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(mainPage,
                StandardCharsets.UTF_8)) {
            writer.write(JSON.serialize(overviewGraph));
            System.out.println("Successfully output to the target file!");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //LandDAO landDAO = new LandDAO(siteDatabase);
        //landDAO.getTopSearchEngines();

        //KeywordDAO keywordDAO = new KeywordDAO(siteDatabase);
        //keywordDAO.getKeywordList();

        //SessionDAO sessionDAO = new SessionDAO(siteDatabase);
        //JumpDAO jumpDAO = new JumpDAO(siteDatabase);
        //jumpDAO.getTotalSessions();
        //String sessionTrends = sessionDAO.getSessionsByDate("2014-08-10");
        //System.out.println(sessionTrends);
    }
}
