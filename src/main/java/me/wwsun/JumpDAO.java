package me.wwsun;

import com.mongodb.*;

/**
 * Created by Weiwei on 12/23/2014.
 */
public class JumpDAO {
    private DBCollection jumpCollection;

    public JumpDAO(final DB siteDatabase) {
        jumpCollection = siteDatabase.getCollection("jump");
    }

    public Integer getTotalSessions() {
        QueryBuilder builder = QueryBuilder.start("type").is("count");
        DBCursor cursor = jumpCollection.find(builder.get(), new BasicDBObject("_id", false));

        Integer sessionNums = null;
        if (cursor.hasNext()) {
            DBObject obj = cursor.next();
            sessionNums = Integer.valueOf(obj.get("num").toString());
        }
        return sessionNums;
    }
}
