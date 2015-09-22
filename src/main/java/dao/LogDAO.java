package dao;

import com.mongodb.*;
import entity.SessionURLNode;
import  entity.URLNode;
import com.google.gson.Gson;

import javax.json.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LogDAO {
    private DBCollection log;

    public LogDAO(final DB db) {
        log = db.getCollection("log");
    }

    public LogDAO(final String mongoURI,final String db) throws UnknownHostException{
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB siteDatabase = mongoClient.getDB(db);
        log=siteDatabase.getCollection("log");
    }

    public List<DBObject> getSessionSequenceBySessionId(String sessionId) {
        QueryBuilder builder = QueryBuilder.start("session").is(sessionId);
        //取出会话序列并排序，
        DBCursor cursor = log.find(builder.get(), new BasicDBObject("_id", false))
                .sort(new BasicDBObject("session_seq", 1));
        List<DBObject> singleSessionSequence = new ArrayList<DBObject>();
        while(cursor.hasNext()) {
            DBObject item = cursor.next();
            singleSessionSequence.add(item);
            //System.out.println(item.get("session_seq" + " | " ));
        }
        cursor.close();
        return singleSessionSequence;
    }

  //根据session来获得个人会话，同时返回ip，用于连接数据
    public String getPathBySession(String session){
        Gson gson=new Gson();
        List<DBObject> dbobjs=this.getSessionSequenceBySessionId(session);
        ArrayList<SessionURLNode> nodesList=new ArrayList<SessionURLNode>();
        for(int i=0;i<dbobjs.size();i++){
            DBObject ob=dbobjs.get(i);
            SessionURLNode node=new SessionURLNode();
            if(i==0){
                SessionURLNode first=new SessionURLNode();
                first.setUrl(ob.get("referer").toString());
                first.setIp(ob.get("ip").toString());
                first.setDatetime("-");
                nodesList.add(first);
            }
            node.setUrl(ob.get("request").toString());
            node.setIp(ob.get("ip").toString());
            node.setDatetime(ob.get("dateTime").toString());
            nodesList.add(node);
        }
        return  gson.toJson(nodesList);
    }

    //获得主要搜索引擎来源
    public List<DBObject> getTopSearchEngines(int limit) {
        //这是从Level表中查到的，这里直接存储在数组里
        int[]  SearchEngineID={73, 1000, 2435, 2436, 2437, 2439, 2440, 6018, 6199, 6302, 6303, 6304, 6305, 6306, 6307, 6308, 6309};
        //match(where)
        BasicDBObject[] arrayCond = {
                new BasicDBObject("refererID", new BasicDBObject("$in",SearchEngineID)),  //搜索引擎
                new BasicDBObject("session_seq", 1),   //第一步
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
       //project(select)
        DBObject fields = new BasicDBObject("refererID", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$refererID");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match,project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = log.aggregate(pipeline, options);
        List<DBObject> list=new ArrayList<DBObject>();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            list.add(object);
        }
        cursor.close();
        return list;
    }
    //主要着陆页页类别
    public List<DBObject> getMainLandingCategories(int limit) {
        //match(where)
        BasicDBObject[] arrayCond = {
                new BasicDBObject("requestID",   new BasicDBObject("$ne","0")),  //0是不能识别的类型
                new BasicDBObject("session_seq", 1),   //第一步
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //project(select)
        DBObject fields = new BasicDBObject("requestID", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$requestID");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match,project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = log.aggregate(pipeline, options);
        List<DBObject> list = new ArrayList<DBObject>();

        while (cursor.hasNext()) {
            DBObject cursorItem = cursor.next();
            DBObject item=new BasicDBObject();
            item.put("landID",cursorItem.get("_id"));
            item.put("sum",cursorItem.get("nums"));
            list.add(item);
        }
        cursor.close();
        return list;
    }

    //搜索引擎来源(0)，直接来源(1)和推荐来源(2)各占session数
    public List<DBObject> getSourceSessionNums(){
        //统计各项指标
        DBObject groupFields = new BasicDBObject("_id", "$refererType");
        groupFields.put("sessions", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort =new BasicDBObject("$sort",new BasicDBObject("sessions",-1));
        List<DBObject> pipeline = Arrays.asList(group,sort);
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        List<DBObject> sources=new ArrayList<>();
        Cursor cursor = log.aggregate(pipeline, options);
        while (cursor.hasNext()){
            sources.add(cursor.next());
        }
        return sources;
    }


    public List<DBObject> getSeperateSessions() {
        DBObject groupFields = new BasicDBObject("_id", "$session");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1));
        List<DBObject> pipeline = Arrays.asList(group, sort);
        AggregationOutput output = log.aggregate(pipeline);
        List<DBObject> sessionList = new ArrayList<DBObject>();
        for (DBObject result : output.results()) {
            sessionList.add(result);
        }
        return sessionList;
    }

    //条件必须符合这种格式，否则查询不好写
    //或者随机选出一些sessionID供前端选择，用于个人访问会话还原
    public List<DBObject> getSessionListByCondition(String condition){
        //读取获得的json数据
        JsonReader reader = Json.createReader(new StringReader(condition));
        JsonObject jsonobj = reader.readObject();
        reader.close();
        //读取filter条件
        Integer category=jsonobj.getInt("category");
        String date=jsonobj.getString("date");
        Integer last=jsonobj.getInt("last");
        Integer pages=jsonobj.getInt("pages");
        String country=jsonobj.getString("country");
        String startTime=date+" 00:00:00";
        String endTime=date+" 23:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BasicDBObject[] arrayCond = new BasicDBObject[0];
        try {
            arrayCond = new BasicDBObject[]{
                    new BasicDBObject("requestID", category),
                    new BasicDBObject("dateTime", new BasicDBObject("$gte", sdf.parse(startTime))),
                    new BasicDBObject("dateTime", new BasicDBObject("$lt", sdf.parse(endTime))),
                    new BasicDBObject("session_last", new BasicDBObject("$gte", last)),
                    new BasicDBObject("session_clicknums", new BasicDBObject("$gte", pages)),
                    new BasicDBObject("country", country),
            };
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //match
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);

        //group
        DBObject _group=new BasicDBObject("session","$session");
//        _group.put("date","$dateTime");
        _group.put("category","$requestID");
        _group.put("last","$session_last");
        _group.put("pages","$session_clicknums");
        _group.put("country","$country");
        _group.put("device","$device");

        DBObject groupFields = new BasicDBObject("_id", _group);
        groupFields.put("date", new BasicDBObject("$min", "$dateTime"));

        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort =new BasicDBObject("$sort",new BasicDBObject("date",1));

        List<DBObject> pipeline = Arrays.asList(match,group,sort);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();

        List<DBObject> sessionList=new ArrayList<>();
        Cursor cursor = log.aggregate(pipeline, options);

        while (cursor.hasNext()){
            sessionList.add(cursor.next());
        }
        return sessionList;
    }

    ////////////////////////////////////////////////////////////////////////////////
   //来源页的类别
   public List<DBObject> getTopSourceCategories(){
       BasicDBObject cond = new BasicDBObject();
       cond.put("session_seq", 1);
       DBObject match = new BasicDBObject("$match", cond);
       //project(select)
       DBObject fields = new BasicDBObject("refererID", 1);
       DBObject project = new BasicDBObject("$project", fields);
       // $group
       DBObject groupFields = new BasicDBObject("_id", "$refererID");
       groupFields.put("nums", new BasicDBObject("$sum", 1));
       DBObject group = new BasicDBObject("$group", groupFields);
       // $sort
       DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
       // $limit
       // DBObject limitObj = new BasicDBObject("$limit", limit);
       List<DBObject> pipeline = Arrays.asList(match, project, group, sort);
       //allowDiskUse
       AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
       Cursor cursor = log.aggregate(pipeline, options);

       List<DBObject> list=new ArrayList<DBObject>();
       while(cursor.hasNext()) {
           DBObject object = cursor.next();
           DBObject item=new BasicDBObject();
           item.put("ID", (String) object.get("_id"));
           item.put("dup", (Integer) object.get("nums"));
           list.add(item);
       }
       cursor.close();
       return list;
   }
    //top来源页
    public JsonArray getTopSourcePages(int limit){
        //match(where)
        BasicDBObject cond = new BasicDBObject();
        cond.put("session_seq", 1); //第一步
        DBObject match = new BasicDBObject("$match", cond);
        //project(select)
        DBObject fields = new BasicDBObject("referer", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$referer");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match, project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = log.aggregate(pipeline, options);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("url", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        cursor.close();
        return jsonArrayBuilder.build();
    }
   //根据某一来源页，获得其去向的类别top
    public List<DBObject> getTopCategoriesBySourcePages(String url,int limit){
        //match(where)
        BasicDBObject[] arrayCond = {
                new BasicDBObject("referer", url),  //来源页
                new BasicDBObject("session_seq", 1),   //第一步
                new BasicDBObject("requestID",   new BasicDBObject("$ne",0))
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //project(select)
        DBObject fields = new BasicDBObject("requestID", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$requestID");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match,project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = log.aggregate(pipeline, options);
        List<DBObject> list = new ArrayList<DBObject>();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject item=new BasicDBObject();
            item.put("level3_id",  object.get("_id"));
            item.put("dup",  object.get("nums"));
            list.add(item);
        }
        cursor.close();
        return list;
    }
    //根据某一来源页，获得其去向的页面top
    public JsonArray getTopPagesBySourcePage(String url, int limit){
        //match(where)
        BasicDBObject[] arrayCond = {
                new BasicDBObject("referer", url),  //来源页
                new BasicDBObject("session_seq", 1),   //第一步
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //project(select)
        DBObject fields = new BasicDBObject("request", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$request");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match,project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = log.aggregate(pipeline, options);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("url", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        cursor.close();
        return jsonArrayBuilder.build();
    }
    //top着陆页
    public JsonArray getTopLandingPages(int limit){
        //match(where)
        BasicDBObject cond = new BasicDBObject();
        cond.put("session_seq", 1);
        DBObject match = new BasicDBObject("$match", cond);
        //project(select)
        DBObject fields = new BasicDBObject("request", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$request");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(match,project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = log.aggregate(pipeline, options);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("url", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        cursor.close();
        return jsonArrayBuilder.build();
    }
    //land页面中某一类别下的top页面
    // 通过传入的category的ID，获得该类别下面的topk页面
    public List<DBObject> getTopLandPagesByCategory(int category,int limit){
        //match(where)
        BasicDBObject[] arrayCond = {
                new BasicDBObject("requestID", category),  //着陆页category
                new BasicDBObject("session_seq", 1),   //第一步
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        // $project
        DBObject fields = new BasicDBObject("request", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$request");
        groupFields.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        //run
        List<DBObject> pipeline = Arrays.asList(match,project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = log.aggregate(pipeline, options);

        List<DBObject> pagesList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            pagesList.add(item);
        }
        return  pagesList;
    }


    public void findOne(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryBuilder builder = QueryBuilder.start("session_seq").is(1);
        DBObject object = log.findOne(builder.get(), new BasicDBObject("_id", false));
        // output
        String t1=sdf.format((Date)object.get("dateTime"));
        System.out.println(t1);

    }
/////////////////////////////////////////////////////////////////////////////////////
    //测试
    public static void main(String args[]) throws ParseException,IOException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db= mongoClient.getDB("huawei");
        LogDAO logDAO = new LogDAO(db);
//        String url="-";
//        List<DBObject> list=logDAO.getTopCategoriesBySourcePages(url, 10);
//        System.out.println(list.size());
//        System.out.println(logDAO.getTopCategoriesBySourcePages(url, 10));
      //  int a=225;
        int b=5000;
      //  double rate =(double)a/(double)b;
       // System.out.println(rate);
        logDAO.findOne();

    }
}
