package dao;

import com.mongodb.*;

import java.math.BigDecimal;

public class JumpDAO {
    private DBCollection jumpCollection;

    public JumpDAO(final DB siteDatabase) {
        jumpCollection = siteDatabase.getCollection("jump");
    }

    /**
     * todo get session counts by date
     * count the sessions
     * @return the number of session that are processed
     */
    public Integer getSessionCounts() {
        QueryBuilder builder = QueryBuilder.start("type").is("countAll");
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
        QueryBuilder builder1 = QueryBuilder.start("type").is("countAll");
        DBObject count = jumpCollection.findOne(builder1.get());

        QueryBuilder builder2 = QueryBuilder.start("type").is("onehop");
        DBObject oneHops = jumpCollection.findOne(builder2.get());

        double bounceRate = (double)(Integer)oneHops.get("sum")/(double)(Integer)count.get("sum");
        BigDecimal decimal = new BigDecimal(bounceRate);
        double result = decimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result*100;
    }

    public double getInquiryRate() {
        QueryBuilder builder1 = QueryBuilder.start("type").is("countAll");
        DBObject count = jumpCollection.findOne(builder1.get());

        QueryBuilder builder2 = QueryBuilder.start("type").is("inquiry");
        DBObject inquiry = jumpCollection.findOne(builder2.get());

        double bounceRate = (double)(Integer)inquiry.get("sum")/(double)(Integer)count.get("sum");
        BigDecimal decimal = new BigDecimal(bounceRate);
        double result = decimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result*100;
    }
}
