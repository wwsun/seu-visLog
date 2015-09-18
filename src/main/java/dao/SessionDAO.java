package dao;

import com.mongodb.*;

import java.util.ArrayList;
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
}
