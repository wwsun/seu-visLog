package me.wwsun.demo;

import com.mongodb.*;
import com.mongodb.util.JSON;
import me.wwsun.JumpDAO;
import me.wwsun.KeywordDAO;
import me.wwsun.SourceDAO;
import me.wwsun.SessionDAO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Weiwei on 12/23/2014.
 */
public class Demo3 {
    public static void main(String[] args) throws UnknownHostException {
        final String mongoURIString = "mongodb://223.3.80.243:27017";
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));

        final DB siteDatabase = mongoClient.getDB("sample");


        JumpDAO jumpDAO = new JumpDAO(siteDatabase);
        SessionDAO sessionDAO = new SessionDAO(siteDatabase);
        KeywordDAO keywordDAO = new KeywordDAO(siteDatabase);
        SourceDAO sourceDAO = new SourceDAO(siteDatabase);

        DBObject overview = new BasicDBObject();

        DBObject sessionTrends = sessionDAO.getSessionsByDate("2014-10-22");
        Integer totalSessions = jumpDAO.getTotalSessions();
        double bounceRate = jumpDAO.getBounceRate();
        List topSearchEngines = sourceDAO.getTopSearchEngines();
        List keywords = keywordDAO.getKeywordList();

        overview.put("totalSessions", totalSessions);
        overview.put("totalBounceRate", bounceRate);
        overview.put("sessionTrends", sessionTrends);
        overview.put("topReferral", "".toCharArray());
        overview.put("topSearchEngine", topSearchEngines);
        overview.put("topKeywords", keywords.toArray());


        System.out.println(JSON.serialize(overview));

        Path mainPage = null;
        try {
            mainPage = Paths.get("./target/classes/public/data/site-overview.json").toRealPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(mainPage,
                StandardCharsets.UTF_8)) {
            writer.write(JSON.serialize(overview));
            System.out.println("Successfully output to the target file!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
