package me.wwsun.demo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.sun.org.apache.bcel.internal.generic.LNEG;
import me.wwsun.JumpDAO;
import me.wwsun.KeywordDAO;
import me.wwsun.LandDAO;
import me.wwsun.SessionDAO;

import java.net.UnknownHostException;

/**
 * Created by Weiwei on 12/23/2014.
 */
public class Demo2 {
    public static void main(String[] args) throws UnknownHostException {
        final String mongoURIString = "mongodb://223.3.75.101:27017";
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));

        final DB siteDatabase = mongoClient.getDB("sample");

        LandDAO landDAO = new LandDAO(siteDatabase);
        landDAO.getTopSearchEngines();

        //KeywordDAO keywordDAO = new KeywordDAO(siteDatabase);
        //keywordDAO.getKeywordList();

        //SessionDAO sessionDAO = new SessionDAO(siteDatabase);
        //JumpDAO jumpDAO = new JumpDAO(siteDatabase);
        //jumpDAO.getTotalSessions();
        //String sessionTrends = sessionDAO.getSessionsByDate("2014-08-10");
        //System.out.println(sessionTrends);
    }
}
