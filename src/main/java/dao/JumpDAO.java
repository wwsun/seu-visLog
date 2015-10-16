package dao;

import com.mongodb.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JumpDAO {
    private DBCollection jumpCollection;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public JumpDAO(final DB siteDatabase) {
        jumpCollection = siteDatabase.getCollection("jump");
    }

    /**
     * get session counts by date
     * count the sessions
     * @return the number of session that are processed
     */
    public Integer getSessionCountsByDate(String date) {
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("type").is("countAll").and(new BasicDBObject("date", sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = jumpCollection.find(builder.get(), new BasicDBObject("_id", false));

        Integer sessionNums = null;
        if (cursor.hasNext()) {
            DBObject obj = cursor.next();
            sessionNums = Integer.valueOf(obj.get("sum").toString());
        }
        cursor.close();
        return sessionNums;
    }

    public double getBounceRateByDate(String date) {
        QueryBuilder builder1 = null;
        QueryBuilder builder2 = null;

        try {
            builder1 = QueryBuilder.start("type").is("countAll").and(new BasicDBObject("date", sdf.parse(date)));
            builder2 = QueryBuilder.start("type").is("onehop").and(new BasicDBObject("date", sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DBObject count = jumpCollection.findOne(builder1.get());
        DBObject oneHops = jumpCollection.findOne(builder2.get());

        double bounceRate = (double)(Integer)oneHops.get("sum")/(double)(Integer)count.get("sum");
        BigDecimal decimal = new BigDecimal(bounceRate);
        double result = decimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result*100;
    }

    public double getInquiryRateByDate(String date) {
        QueryBuilder builder1 = null;
        QueryBuilder builder2 = null;
        try {
            builder1 = QueryBuilder.start("type").is("countAll").and(new BasicDBObject("date", sdf.parse(date)));
            builder2 = QueryBuilder.start("type").is("inquiry").and(new BasicDBObject("date", sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DBObject count = jumpCollection.findOne(builder1.get());
        DBObject inquiry = jumpCollection.findOne(builder2.get());

        System.out.println(count);
        System.out.println(inquiry);

        double inquiryRate = (double)(Integer)inquiry.get("sum")/(double)(Integer)count.get("sum");

        BigDecimal decimal = new BigDecimal(inquiryRate);
        double result = decimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result*100;
    }
}
