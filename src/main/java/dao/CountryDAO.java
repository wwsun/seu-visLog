package dao;

import com.mongodb.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryDAO {
    private DBCollection geo;

    public CountryDAO(final DB siteDatabase) { geo = siteDatabase.getCollection("country"); }

    public List<DBObject> getGeoDistribution(int limit) {

        DBObject groupFields = new BasicDBObject("_id", "$country");
        groupFields.put("total_session", new BasicDBObject("$sum", "$session_nums"));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort =new BasicDBObject("$sort",new BasicDBObject("total_session",-1));
        DBObject limitObj =new BasicDBObject("$limit",limit);

        List<DBObject> pipeline = Arrays.asList(group,sort,limitObj);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = geo.aggregate(pipeline, options);

        List<DBObject> list = new ArrayList<DBObject>();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();

            newObj.put("name", object.get("_id"));
            newObj.put("value", object.get("total_session"));

            list.add(newObj);
        }
        cursor.close();
        return list;
    }


    public List<DBObject> getGeoDetail(int limit) {
        //统计各项指标
        DBObject groupFields = new BasicDBObject("_id", "$country");
        groupFields.put("total_session", new BasicDBObject("$sum", "$session_nums"));
        groupFields.put("total_last", new BasicDBObject("$sum", "$session_last"));
        groupFields.put("total_pages", new BasicDBObject("$sum", "$pages_nums"));
        groupFields.put("total_jumps", new BasicDBObject("$sum", "$jump_nums"));
        groupFields.put("total_inquiry", new BasicDBObject("$sum", "$inquiry_nums"));

        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort =new BasicDBObject("$sort",new BasicDBObject("total_session",-1));
        DBObject limitObj =new BasicDBObject("$limit",limit);

        List<DBObject> pipeline = Arrays.asList(group,sort,limitObj);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();

        List<DBObject> details=new ArrayList<>();
        Cursor cursor = geo.aggregate(pipeline, options);

        while (cursor.hasNext()){
            details.add(cursor.next());
        }
        return details;
    }

    /*
    * 此处应该传入开始日期和结束日期
    * group by day*/
    public DBObject getSessionDistribution(String country){
        DBObject match = new BasicDBObject("$match", new BasicDBObject("country",country));
        DBObject sort =new BasicDBObject("$sort",new BasicDBObject("hour",1));
        List<DBObject> pipeline = Arrays.asList(match,sort);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();

        Cursor cursor = geo.aggregate(pipeline, options);

        List<Integer> dupList = new ArrayList<Integer>();
        List<Integer> hourList = new ArrayList<Integer>();

        while (cursor.hasNext()){
            DBObject obj=cursor.next();
            dupList.add(Integer.valueOf(obj.get("session_nums").toString()));
            hourList.add(Integer.valueOf(obj.get("hour").toString()));
        }

        DBObject details = new BasicDBObject();
        details.put("hour", hourList.toArray());
        details.put("dup", dupList.toArray());
        return details;
    }

    //按date统计session
    public int getSessionNumsByDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("date").is(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = geo.find(builder.get(), new BasicDBObject("session_nums", true)
                .append("_id", false));  //ASC

        int session_nums_all=0;
        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer num = Integer.valueOf(obj.get("session_nums").toString());
            session_nums_all += num;
        }
        cursor.close();
        return session_nums_all;
    }

    //按date统计inquiry_num
    public int getInquiryNumsByDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("date").is(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = geo.find(builder.get(), new BasicDBObject("inquiry_nums", true)
                .append("_id", false));  //ASC

        int inquiry_nums_all=0;
        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer num = Integer.valueOf(obj.get("inquiry_nums").toString());
            inquiry_nums_all += num;
        }
        cursor.close();
        return inquiry_nums_all;
    }

}
