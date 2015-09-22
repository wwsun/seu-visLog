package dao;

import com.mongodb.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

public class LeaveDAO {

    private DBCollection leave;

    public LeaveDAO(final DB db) { leave = db.getCollection("leave"); }

    /**
     * get main drop off categories
     * @param limit is the number of results you want to returned
     * @return the main drop off categories
     */

    public List<DBObject> getMainDropoffCategories( int limit) {

        //project(select)
        DBObject fields = new BasicDBObject("leaveID", 1);
        fields.put("sum",1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$leaveID");
        groupFields.put("nums", new BasicDBObject("$sum", "$sum"));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        List<DBObject> pipeline = Arrays.asList(project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = leave.aggregate(pipeline, options);

        List<DBObject> list = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject cursorItem = cursor.next();
            DBObject item=new BasicDBObject();
            item.put("leaveID", cursorItem.get("_id"));
            item.put("sum", cursorItem.get("nums"));
            list.add(item);


        }
        cursor.close();
        return list;
    }


    //leave页面下某一类别的top页面
    // 通过传入的category的ID，获得该类别下面的top页面
    //返回 _id:
    //     nums:
    public List<DBObject> getTopLeavePagesByCategory(int category, int limit){
        //match
        BasicDBObject cond = new BasicDBObject();
        cond.put("leaveID", category);
        DBObject match = new BasicDBObject("$match", cond);
        // $project
        DBObject fields = new BasicDBObject("url", 1);
        fields.put("sum", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$url");
        groupFields.put("nums", new BasicDBObject("$sum", "$sum"));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1)); //DEC
        // $limit
        DBObject limitObj = new BasicDBObject("$limit", limit);
        //run
        List<DBObject> pipeline = Arrays.asList(match,project, group, sort, limitObj);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = leave.aggregate(pipeline, options);

        List<DBObject> pagesList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            pagesList.add(item);
        }
        return  pagesList;
    }

    //测试
    public static void main(String args[]) throws ParseException,IOException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db= mongoClient.getDB("huawei");
        LeaveDAO leaveDAO = new LeaveDAO(db);
        List<DBObject> list =leaveDAO.getTopLeavePagesByCategory(2554, 5);
        System.out.println(list);
    }
}
