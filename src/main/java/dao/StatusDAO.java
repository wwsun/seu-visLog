package dao;

import com.mongodb.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusDAO {
    DecimalFormat df = new DecimalFormat("#.00");
    private DBCollection status;

    public StatusDAO(final DB siteDatabase) {
        status = siteDatabase.getCollection("status");
    }

    //按date把所有status不是200的加起来,都认为是不正常的访问
    public int getNumsByDate(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("date").is(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = status.find(builder.get(), new BasicDBObject("status", true)
                .append("_id", false));
        int error_request_nums = 0;
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer num = Integer.valueOf(obj.get("status").toString());
            if (status.equals("200")) {
                error_request_nums += 0;
            } else {
                error_request_nums += num;
            }
        }
        cursor.close();
        return error_request_nums;
    }

//////////////////////////
    //StatusCode列表
    public List<DBObject> getStatusListOneDayByDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DBObject match=null ;
        try {
            match = new BasicDBObject("$match", new BasicDBObject("date", sdf.parse(date)));

        }catch(ParseException e){
            //do nothing
            // e.printStackTrace();
        }
        DBObject field = new BasicDBObject("date", 1);
        field.put("status", 1);
        field.put("sum", 1);
        DBObject project = new BasicDBObject("$project", field);

        DBObject groupFields = new BasicDBObject("_id", "$status");
        groupFields.put("nums", new BasicDBObject("$sum", "$sum"));
        DBObject group = new BasicDBObject("$group", groupFields);

        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
        // DBObject out = new BasicDBObject("$out", "tmp_out");
        List<DBObject> pipeline = Arrays.asList(match, project, group, sort);

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = status.aggregate(pipeline, options);

        List<DBObject> statusList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            String status = (obj.get("_id").toString());
            Integer dup = Integer.valueOf(obj.get("nums").toString());
           DBObject item=new BasicDBObject();
            item.put("statuscode",status);
            item.put("num",dup);
            statusList.add(item);
        }
        cursor.close();
      return  statusList;
    }

    //一天的吞吐量，所有页面的大小(也可以按每个小时去细分),返回  数值+单位
    public DBObject getThruputOneDayByDate(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("date").is(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = status.find(builder.get(), new BasicDBObject("page_size", true)
                .append("_id", false));
        Double pages_size = 0.00;
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Double size = Double.valueOf(obj.get("page_size").toString());
            pages_size += size;
        }
        cursor.close();
        //MB
        int tmp = 1024;
        String result = "O MB";
        if (pages_size - tmp > 0) { //大于1GB
            result = df.format(pages_size / 1024) + " GB";
        } else {
            result = df.format(pages_size) + " MB";
        }
        DBObject thruput=new BasicDBObject("thruput" ,result );
        return  thruput;
    }

    public DBObject getClickTrendsOneDayByDate(String date)  {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DBObject match=null ;
        try {
            match = new BasicDBObject("$match", new BasicDBObject("date", sdf.parse(date)));
       }catch(ParseException e){
       }

        DBObject field = new BasicDBObject("date", 1);
        field.put("hour", 1);
        field.put("sum", 1);
        DBObject project = new BasicDBObject("$project", field);

        DBObject groupFields = new BasicDBObject("_id", "$hour");
        groupFields.put("click_nums", new BasicDBObject("$sum", "$sum"));
        DBObject group = new BasicDBObject("$group", groupFields);

        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
        // DBObject out = new BasicDBObject("$out", "tmp_out");
        List<DBObject> pipeline = Arrays.asList(match, project, group, sort);

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = status.aggregate(pipeline, options);

        List<Integer> dupList = new ArrayList<Integer>();
        List<Integer> hourList = new ArrayList<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer hour = Integer.valueOf(obj.get("_id").toString());
            Integer dup = Integer.valueOf(obj.get("click_nums").toString());
            dupList.add(dup);
            hourList.add(hour);
        }
        cursor.close();
        DBObject clicksTrends = new BasicDBObject();
        clicksTrends.put("hour", hourList.toArray());
        clicksTrends.put("dup", dupList.toArray());
        return clicksTrends;

    }

//////////////////////

    public static void main(String args[]) throws ParseException, IOException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        StatusDAO statusDAO = new StatusDAO(db);
        List<DBObject>  list=statusDAO. getStatusListOneDayByDate("2014-08-10");
         System.out.println(list);
    }
}
