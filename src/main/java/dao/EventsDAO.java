package dao;

import com.mongodb.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/5/18.
 */
public class EventsDAO {
    private DBCollection events;
     boolean  flag=false;  //是否标注的标记位
    public EventsDAO(final DB siteDatabase) {
        events = siteDatabase.getCollection("events");
    }

    //将ReceiveDataResource的数据写入到数据库中
    public void insertEvents(String schema, HashSet<String> hs_log) {
       // String schema = "browser,os,url,ip,loadtime,time,element,id,text,semantics,event,left,top,height,width";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] fields = schema.split(",");
        List<DBObject> eventsList = new ArrayList<>();
        for (String event_log : hs_log) {
           // System.out.println(event_log);
            String[] log_parts = event_log.split(",");
            DBObject document = new BasicDBObject();
            //field
            if (!(fields.length == log_parts.length)) {  //数据ok才插入
                continue;
            }
            for (int i = 0; i < log_parts.length; i++) {
               try{
                if(fields[i].equals("loadtime") ||fields[i].equals("time") ){
                    document.put(fields[i],sdf.parse(log_parts[i]));
                }else {
                    document.put(fields[i], log_parts[i]);
                }
               }catch (ParseException e){

               }
            }
            //在最后添加一个是否已经标注的flag(false，true)
            document.put("flag",flag);
            eventsList.add(document);
        }
        if (!(eventsList.size() == 0)) {  //插入数据不能为空
            this.events.insert(eventsList);
        }
    }

    //给定事件类型下的top页面
    public List<DBObject> getTopURLByEvent(String event, int limit) {
        DBObject match = new BasicDBObject("$match", new BasicDBObject("event", event));
        DBObject groupFields = new BasicDBObject("_id", "$url");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1));
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = events.aggregate(pipeline, options);
        List<DBObject> list = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            DBObject newObj = new BasicDBObject();
            String url = item.get("_id").toString();
            int nums = (int) item.get("nums");
            newObj.put("url", url);
            newObj.put("dup", nums);
            list.add(newObj);
        }
        return list;
    }
    //给定语义下的top页面
    public List<DBObject> getTopURLBySemantic(String semantic, int limit) {
        DBObject match = new BasicDBObject("$match", new BasicDBObject("semantics", semantic));
        DBObject groupFields = new BasicDBObject("_id", "$url");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1));
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = events.aggregate(pipeline, options);
        List<DBObject> list = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            DBObject newObj = new BasicDBObject();
            String url = item.get("_id").toString();
            int nums = (int) item.get("nums");
            newObj.put("url", url);
            newObj.put("dup", nums);
            list.add(newObj);
        }
        return list;
    }

    //获得所有语义的列表
    public List<DBObject> getSemanticsList() {
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$semantics");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1));
        List<DBObject> pipeline = Arrays.asList(group, sort);
        AggregationOutput output = events.aggregate(pipeline);
        // output
        List<DBObject> semanticsList = new ArrayList<DBObject>();
        for (DBObject result : output.results()) {
            String semantic=(String)result.get("_id");
            int num=(Integer)result.get("nums");
           if(!semantic.equals("")) {
               DBObject obj = new BasicDBObject();
               obj.put("semantic", semantic);
               obj.put("dup", num);
               semanticsList.add(obj);
           }
        }
        return semanticsList;
    }

    //给定一个URL下的的语义分布
    public   Map<String, Integer> getSemanticsDistributionByURL(String url) {

        DBObject match = new BasicDBObject("$match", new BasicDBObject("url", url));
        DBObject groupFields = new BasicDBObject("_id", "$semantics");
        //这里改成event的话，会分别聚集三个事件的值，
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline = Arrays.asList(match, group);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = events.aggregate(pipeline, options);
        Map<String, Integer> semanticsMap =new HashMap<String, Integer>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            String semantic =(String) item.get("_id");
            int num=(Integer)item.get("nums");
            semanticsMap.put(semantic, num);
        }
        return   semanticsMap;
    }

    //给定一个URL,某种事件的语义聚合（测试）
    public   Map<String, Integer> getSemanticsNumsByURLEvent(String url,String event ) {
        //将来可以加时间范围
        BasicDBObject[] arrayCond = {
                new BasicDBObject("url", url),
                new BasicDBObject("event",event),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        DBObject groupFields = new BasicDBObject("_id", "$semantics");
        //这里改成event的话，会分别聚集三个事件的值，
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline = Arrays.asList(match, group);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = events.aggregate(pipeline, options);
        Map<String, Integer> semanticsMap =new HashMap<String, Integer>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            String semantic =(String) item.get("_id");
            int num=(Integer)item.get("nums");
            semanticsMap.put(semantic, num);
        }
        return   semanticsMap;
    }

    //给定一个ip,url,dateTime,统计其语义分布
    public   Map<String, Integer> getSemanticsDistribution(String ip, String url, String dateTime, String delayTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BasicDBObject[] arrayCond = {
                new BasicDBObject("ip", ip),
                new BasicDBObject("url", url),
                new BasicDBObject("time", new BasicDBObject("$gte", sdf.parse(dateTime))),
                new BasicDBObject("time", new BasicDBObject("$lt", sdf.parse(delayTime))),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //统计网页中不同语义的内容对应的事件数目
        DBObject groupFields = new BasicDBObject("_id", "$semantics");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline = Arrays.asList(match, group);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = events.aggregate(pipeline, options);
        //获取某个页面对应的语义内容的浏览占比
        Map<String, Integer> semanticsMap = new HashMap<>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            String semantic = item.get("_id").toString();
            int num = (int) item.get("nums");
            semanticsMap.put(semantic, num);
        }
        return semanticsMap;
    }

    //给定一个ip,url,dateTime,具体某个事件,统计其语义分布
    public   Map<String, Integer> getSemanticsDistributionByEvent(String ip, String url, String dateTime, String delayTime,String event) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BasicDBObject[] arrayCond = {
                new BasicDBObject("ip", ip),
                new BasicDBObject("url", url),
                new BasicDBObject("event",event),
                new BasicDBObject("time", new BasicDBObject("$gte", sdf.parse(dateTime))),
                new BasicDBObject("time", new BasicDBObject("$lt", sdf.parse(delayTime))),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //统计网页中不同语义的内容对应的事件数目
        DBObject groupFields = new BasicDBObject("_id", "$semantics");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        List<DBObject> pipeline = Arrays.asList(match, group);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = events.aggregate(pipeline, options);
        //获取某个页面对应的语义内容的浏览占比
        Map<String, Integer> semanticsMap = new HashMap<>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            String semantic = item.get("_id").toString();
            int num = (int) item.get("nums");
            semanticsMap.put(semantic, num);
        }
        return semanticsMap;
    }

    public static void main(String [] args)throws IOException,ParseException{
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        EventsDAO eventsDAO = new  EventsDAO(db);
        DBCollection events = db.getCollection("events");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     String ip="223.3.75.101";
        String url="223.3.68.141:8080/html/HongMiNote.html";
        String dateTime="2015-07-22 18:08:04";
        Date date = sdf.parse(dateTime);
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.SECOND, 10);
        String delayTime = sdf.format(cl.getTime());
//
//       Map<String, Integer> map=eventsDAO.getSemanticsDistribution(ip,url,dateTime,delayTime);
//        System.out.println(map);

       List<DBObject> list= eventsDAO.getSemanticsList();
        System.out.println(list);
    }
}
