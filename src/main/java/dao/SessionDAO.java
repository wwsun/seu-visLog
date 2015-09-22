package dao;

import com.mongodb.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionDAO {

    private DBCollection sessions;

    public SessionDAO(final DB siteDatabase) {
        sessions = siteDatabase.getCollection("session");
    }

    public DBObject getSessionsByDate(String date) {
        QueryBuilder builder = QueryBuilder.start("date").is(date);
        DBCursor cursor = sessions.find(builder.get(), new BasicDBObject("hour", true)
                .append("sum", true).append("_id", false)).sort(new BasicDBObject("hour", 1));  //ASC

        List<Integer> dupList = new ArrayList<Integer>();
        List<Integer> hourList = new ArrayList<Integer>();

        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer dup = Integer.valueOf(obj.get("sum").toString());
            Integer hour = Integer.valueOf(obj.get("hour").toString());
            dupList.add(dup);
            hourList.add(hour);
        }
        cursor.close();
        DBObject sessionTrends = new BasicDBObject();

        sessionTrends.put("hour", hourList.toArray());
        sessionTrends.put("dup", dupList.toArray());
        return sessionTrends;
    }

    public DBObject getSessionsByDate(String start,String end) {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        BasicDBObject[] arrayCond = {
                new BasicDBObject("date", new BasicDBObject("$gte", start)),
                new BasicDBObject("date", new BasicDBObject("$lt", end)),
        };

        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);

        DBObject fields = new BasicDBObject("hour", 1);
        fields.put("sum", 1);
        //fields.put("date", 1);
        DBObject project = new BasicDBObject("$project", fields);

        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("hour", 1));

        List<DBObject> pipeline = Arrays.asList(match, project, sort);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = sessions.aggregate(pipeline, options);


        List<Integer> dupList = new ArrayList<Integer>();
        List<Integer> hourList = new ArrayList<Integer>();

        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer dup = Integer.valueOf(obj.get("sum").toString());
            Integer hour = Integer.valueOf(obj.get("hour").toString());
            dupList.add(dup);
            hourList.add(hour);
        }
        cursor.close();
        DBObject sessionTrends = new BasicDBObject();

        sessionTrends.put("hour", hourList.toArray());
        sessionTrends.put("dup", dupList.toArray());
        return sessionTrends;
    }
}
