package dao;

import com.mongodb.*;

import javax.ws.rs.PathParam;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

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

    public Integer getSessionCounts(String start,String end) throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BasicDBObject[] arrayCond = {
                new BasicDBObject("date", new BasicDBObject("$gt", sdf.parse(start))),
                new BasicDBObject("date", new BasicDBObject("$lte", sdf.parse(end))),
                new BasicDBObject("type", new BasicDBObject("$eq", "countAll")),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);

        DBObject fields = new BasicDBObject("type", 1);
        fields.put("sum", 1);
        DBObject project = new BasicDBObject("$project", fields);

        List<DBObject> pipeline = Arrays.asList(match, project);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = jumpCollection.aggregate(pipeline, options);

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

    public double getBounceRate(String start,String end) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BasicDBObject[] arrayCond = {
                new BasicDBObject("date", new BasicDBObject("$gt", sdf.parse(start))),
                new BasicDBObject("date", new BasicDBObject("$lte", sdf.parse(end))),
                new BasicDBObject("type", new BasicDBObject("$eq", "countAll")),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);

        DBObject fields = new BasicDBObject("type", 1);
        fields.put("sum", 1);
        DBObject project = new BasicDBObject("$project", fields);

        List<DBObject> pipeline = Arrays.asList(match, project);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = jumpCollection.aggregate(pipeline, options);

        Integer count = null;
        if (cursor.hasNext()) {
            DBObject obj = cursor.next();
            count = Integer.valueOf(obj.get("sum").toString());
        }
        cursor.close();

        BasicDBObject[] arrayCond1 = {
                new BasicDBObject("date", new BasicDBObject("$gt", sdf.parse(start))),
                new BasicDBObject("date", new BasicDBObject("$lte", sdf.parse(end))),
                new BasicDBObject("type", new BasicDBObject("$eq", "onehop")),
        };
        BasicDBObject cond1 = new BasicDBObject();
        cond1.put("$and", arrayCond1);
        DBObject match1 = new BasicDBObject("$match", cond1);
        DBObject fields1 = new BasicDBObject("type", 1);
        fields1.put("sum", 1);
        DBObject project1 = new BasicDBObject("$project", fields1);

        List<DBObject> pipeline1 = Arrays.asList(match1, project1);
        //allowDiskUse
        AggregationOptions options1 = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor1 = jumpCollection.aggregate(pipeline1, options1);

        Integer oneHops = null;
        if (cursor1.hasNext()) {
            DBObject obj = cursor1.next();
            oneHops = Integer.valueOf(obj.get("sum").toString());
        }
        cursor1.close();

        double bounceRate = (double)(Integer)oneHops/(double)(Integer)count;
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


    public double getInquiryRate(String start, String  end) throws  ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BasicDBObject[] arrayCond = {
                new BasicDBObject("date", new BasicDBObject("$gt", sdf.parse(start))),
                new BasicDBObject("date", new BasicDBObject("$lte", sdf.parse(end))),
                new BasicDBObject("type", new BasicDBObject("$eq", "countAll")),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);

        DBObject fields = new BasicDBObject("type", 1);
        fields.put("sum", 1);
        DBObject project = new BasicDBObject("$project", fields);

        List<DBObject> pipeline = Arrays.asList(match, project);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = jumpCollection.aggregate(pipeline, options);

        Integer count = null;
        if (cursor.hasNext()) {
            DBObject obj = cursor.next();
            count = Integer.valueOf(obj.get("sum").toString());
        }
        cursor.close();

        BasicDBObject[] arrayCond1 = {
                new BasicDBObject("date", new BasicDBObject("$gt", sdf.parse(start))),
                new BasicDBObject("date", new BasicDBObject("$lte", sdf.parse(end))),
                new BasicDBObject("type", new BasicDBObject("$eq", "inquiry")),
        };
        BasicDBObject cond1 = new BasicDBObject();
        cond1.put("$and", arrayCond1);
        DBObject match1 = new BasicDBObject("$match", cond1);
        DBObject fields1 = new BasicDBObject("type", 1);
        fields1.put("sum", 1);
        DBObject project1 = new BasicDBObject("$project", fields1);

        List<DBObject> pipeline1 = Arrays.asList(match1, project1);
        //allowDiskUse
        AggregationOptions options1 = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor1 = jumpCollection.aggregate(pipeline1, options1);

        Integer inquiry = null;
        if (cursor1.hasNext()) {
            DBObject obj = cursor1.next();
            inquiry = Integer.valueOf(obj.get("sum").toString());
        }
        cursor1.close();
        double bounceRate = (double)(Integer)inquiry/(double)(Integer)count;
        BigDecimal decimal = new BigDecimal(bounceRate);
        double result = decimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result*100;
    }
}
