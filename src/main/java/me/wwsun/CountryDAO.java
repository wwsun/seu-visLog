package me.wwsun;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by Weiwei on 12/28/2014.
 */
public class CountryDAO {
    private DBCollection geo;

    public CountryDAO(final DB siteDatabase) { geo = siteDatabase.getCollection("country"); }

    public List<DBObject> getGeoDistribution() {
        QueryBuilder builder = QueryBuilder.start();
        DBCursor cursor = geo.find(builder.get(), new BasicDBObject("_id", false));

        List<DBObject> list = new ArrayList<>();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();
            newObj.put("name", object.get("country"));
            newObj.put("value", object.get("sum"));
            list.add(newObj);
        }
        cursor.close();
        System.out.println("Total: "+ list.size());
        return list;
    }
}
