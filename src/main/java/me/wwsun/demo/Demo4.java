package me.wwsun.demo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import me.wwsun.LinkDAO;

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Weiwei on 12/27/2014.
 */
public class Demo4 {
    public static void main(String[] args) throws UnknownHostException {
        final String mongoURIString = "mongodb://223.3.80.243:27017";
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));

        final DB siteDatabase = mongoClient.getDB("sample");

        final String URL = "www.made-in-china.com/";

        LinkDAO linkDAO = new LinkDAO(siteDatabase);
        List topRefs = linkDAO.getTopReferers(URL, 10);
        List topTars = linkDAO.getTopTargets(URL, 10);
        System.out.println("Top referers: "+topRefs);
        System.out.println("Top targets: "+topTars);
    }
}
