package me.wwsun.demo;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import me.wwsun.CountryDAO;
import me.wwsun.util.FileUtil;

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Weiwei on 12/28/2014.
 */
public class DemoGeo {
    public static void main(String[] args) throws UnknownHostException {
        final String mongoURIString = "mongodb://223.3.80.243:27017";
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));

        final DB siteDatabase = mongoClient.getDB("sample");
        CountryDAO countryDAO = new CountryDAO(siteDatabase);
        //List<DBObject> list = geoDAO.getGeoAsIp();
        //
        List<DBObject> list = countryDAO.getGeoDistribution();
        FileUtil.outputAsJSON(list, "geo-full");
        System.out.println(list);
    }

}
