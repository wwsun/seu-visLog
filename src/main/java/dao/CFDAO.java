package dao;


import com.mongodb.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CFDAO {
    private DBCollection CF;
   //cf : conversionfunnel
    public CFDAO(final DB siteDatabase) { CF = siteDatabase.getCollection("cf"); }

    //获得一天的转换漏斗
    public List<DBObject> getConversionFunnel(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DBObject match = null;
        try {
            match = new BasicDBObject("$match", new BasicDBObject("date", sdf.parse(date)));
        } catch (ParseException e) {

        }
        DBObject groupFields = new BasicDBObject("_id", "");
        groupFields.put("step1sum", new BasicDBObject("$sum", "$step1"));
        groupFields.put("step2sum", new BasicDBObject("$sum", "$step2"));
        groupFields.put("step3sum", new BasicDBObject("$sum", "$step3"));

        DBObject group = new BasicDBObject("$group", groupFields);
        //DBObject sort = new BasicDBObject("$sort", new BasicDBObject("sum", 1));

        List<DBObject> pipeline = Arrays.asList(match, group);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = CF.aggregate(pipeline, options);

        List<DBObject> list = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();
//            DBObject _id = (DBObject)object.get("_id");
//            newObj.put("date",sdf.format(_id.get("$date")));
            DBObject newObj1 = new BasicDBObject();
            newObj1.put("step","访问本站");
            newObj1.put("num",object.get("step1sum"));
            list.add(newObj1);
            DBObject newObj2 = new BasicDBObject();
            newObj2.put("step","浏览商品");
            newObj2.put("num",object.get("step2sum"));
            list.add(newObj2);

            DBObject newObj3 = new BasicDBObject();
            newObj3.put("step","完成交易");
            newObj3.put("num",object.get("step3sum"));
            list.add(newObj3);

        }
        cursor.close();
        return list;
    }

    public static void main(String args[]) throws ParseException, IOException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        CFDAO cfDAO = new CFDAO(db);
        List<DBObject> list = cfDAO.getConversionFunnel("2014-08-10");
        System.out.println(list);
    }
}
