package dao;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

public class CountryDAO {
    private DBCollection geo;

    public CountryDAO(final DB siteDatabase) { geo = siteDatabase.getCollection("country"); }

    public List<DBObject> getGeoDistribution(int limit) {
        QueryBuilder builder = QueryBuilder.start();
        DBCursor cursor = geo.find(builder.get(), new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1))
                .limit(limit);

        List<DBObject> list = new ArrayList<DBObject>();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();
            newObj.put("name", object.get("country"));
            newObj.put("value", object.get("sum"));
            list.add(newObj);
        }
        cursor.close();
        return list;
    }
}
