package dao;

import com.mongodb.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CountryDAO {
    private DBCollection geo;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public CountryDAO(final DB siteDatabase) {
        geo = siteDatabase.getCollection("country");
    }

    public List<DBObject> getGeoDistributionByDate(String date, int limit) {
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("date").is(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = geo.find(builder.get(), new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1))
                .limit(limit);

        List<DBObject> list = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();
            newObj.put("name", object.get("country"));
            newObj.put("date", sdf.format(object.get("date")));
            newObj.put("value", object.get("sum"));
            list.add(newObj);
        }
        cursor.close();
        return list;
    }
}