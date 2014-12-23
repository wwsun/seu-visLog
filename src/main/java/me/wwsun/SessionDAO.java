package me.wwsun;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Weiwei on 12/23/2014.
 */
public class SessionDAO {
    private DBCollection sessions;

    public SessionDAO(final DB siteDatabase) {
        sessions = siteDatabase.getCollection("session");
    }

    /**
     *
     * @param date yyyy-mm-dd
     * @return 24-hours session trends
     */
    public DBObject getSessionsByDate(String date) {
        QueryBuilder builder = QueryBuilder.start("date").is(date);
        DBCursor cursor = sessions.find(builder.get(), new BasicDBObject("hour", true)
                .append("num", true).append("_id", false)).sort(new BasicDBObject("hour", 1));  //ASC

        List<Integer> dupList = new ArrayList<>();
        List<Integer> hourList = new ArrayList<>();

        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer dup = Integer.valueOf(obj.get("num").toString());
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