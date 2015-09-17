package dao;


import com.mongodb.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CTDAO {
    private DBCollection CT;
   //ct : conversionTarget
    public CTDAO(final DB siteDatabase) { CT = siteDatabase.getCollection("ct"); }

  //  获得一天的目标转化List
    public List<DBObject> getConversionList(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DBObject match = null;
        try {
            match = new BasicDBObject("$match", new BasicDBObject("date", sdf.parse(date)));
        } catch (ParseException e) {

        }
        DBObject groupFields = new BasicDBObject("_id", "$targettype");
        groupFields.put("targetsum", new BasicDBObject("$sum", "$targetsum"));
        groupFields.put("targetdone", new BasicDBObject("$sum", "$targetdone"));

        DBObject group = new BasicDBObject("$group", groupFields);
        //DBObject sort = new BasicDBObject("$sort", new BasicDBObject("sum", 1));

        List<DBObject> pipeline = Arrays.asList(match, group);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = CT.aggregate(pipeline, options);

        List<DBObject> list = new ArrayList<DBObject>();

        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();
            newObj.put("targetType",object.get("_id"));
            newObj.put("targetSum",object.get("targetsum"));
            newObj.put("targetDone", object.get("targetdone"));
            list.add(newObj);
        }
        cursor.close();
        return list;
    }

 //造数据
//    public void  TestInsert() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//      List<DBObject> list=new ArrayList<DBObject>() ;
//       for(int  i=0;i<24;i++){
//            DBObject ob = new BasicDBObject();
//            try {
//                ob.put("date", sdf.parse("2014-08-10"));
//            } catch (ParseException e) {
//            }
//            ob.put("hour",(i+1) );
//            Random random=new Random();
//           int sum =random.nextInt(300)+100;
//            ob.put("targettype","applemobile");
//           ob.put("targetsum",sum);
//           ob.put("targetdone",sum-80);
//        list.add(ob);
//        }
//        for(int  i=0;i<24;i++){
//            DBObject ob = new BasicDBObject();
//            try {
//                ob.put("date", sdf.parse("2014-08-10"));
//            } catch (ParseException e) {
//            }
//            ob.put("hour",(i+1) );
//            Random random=new Random();
//            int sum =random.nextInt(300)+100;
//            ob.put("targettype","huaweimobile");
//            ob.put("targetsum",sum);
//            ob.put("targetdone",sum-80);
//            list.add(ob);
//        }
//        for(int  i=0;i<24;i++){
//            DBObject ob = new BasicDBObject();
//            try {
//                ob.put("date", sdf.parse("2014-08-10"));
//            } catch (ParseException e) {
//            }
//            ob.put("hour",(i+1) );
//            Random random=new Random();
//            int sum =random.nextInt(300)+100;
//            ob.put("targettype","xiaomimobile");
//            ob.put("targetsum",sum);
//            ob.put("targetdone",sum-80);
//            list.add(ob);
//        }
//        for(int  i=0;i<24;i++){
//            DBObject ob = new BasicDBObject();
//            try {
//                ob.put("date", sdf.parse("2014-08-10"));
//            } catch (ParseException e) {
//            }
//            ob.put("hour",(i+1) );
//            Random random=new Random();
//            int sum =random.nextInt(300)+100;
//            ob.put("targettype","-");
//            ob.put("targetsum",sum);
//            ob.put("targetdone",sum-80);
//            list.add(ob);
//        }
//        CT.insert(list);
//        System.out.println("Insert Done");
//    }

    public static void main(String args[]) throws ParseException, IOException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        CTDAO ctDAO = new CTDAO(db);
       // ctDAO.TestInsert();
//        System.out.println(list);
        List<DBObject> list=ctDAO.getConversionList("2014-08-10");
        System.out.println(list);
    }
}
