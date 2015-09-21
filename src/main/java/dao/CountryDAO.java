package dao;

import com.mongodb.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    public List<DBObject> getGeoDistribution(String start,String end) throws ParseException
    {

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BasicDBObject[] arrayCond = {
                new BasicDBObject("date", new BasicDBObject("$gte", sdf.parse(start))),
                new BasicDBObject("date", new BasicDBObject("$lt", sdf.parse(end))),

        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);


        DBObject fields = new BasicDBObject("country", 1);
        fields.put("sum", 1);
        DBObject project = new BasicDBObject("$project", fields);

        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("sum", 1));
        //run
        //DBObject out = new BasicDBObject("$out", "tmp_out");
        List<DBObject> pipeline = Arrays.asList(match, project,sort);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();

        Cursor cursor = geo.aggregate(pipeline, options);
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
