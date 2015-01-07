package me.wwsun;

import com.mongodb.*;

import java.math.BigDecimal;

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
            sessionNums = Integer.valueOf(obj.get("sum").toString());
        }
        cursor.close();
        return sessionNums;
    }

    public double getBounceRate() {
        QueryBuilder builder1 = QueryBuilder.start("type").is("count");
        DBObject count = jumpCollection.findOne(builder1.get());

        QueryBuilder builder2 = QueryBuilder.start("type").is("active");
        DBObject active = jumpCollection.findOne(builder2.get());

        double bounceRate = (double)(int)active.get("sum")/(double)(int)count.get("sum");
        BigDecimal decimal = new BigDecimal(bounceRate);
        double result = decimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result*100;
    }
}
