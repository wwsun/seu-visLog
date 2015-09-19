package dao;

import com.mongodb.*;
import sun.tools.jar.Main;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.util.List;

/**
 * @author Minglu SHAO
 */
public class PathDAO {

    private DBCollection path;

    public PathDAO(DB db) {
        path = db.getCollection("path");
    }

    /**
     * ��Ҫ����ʱ�����
     * ����һ�η���
     *
     * @param step
     * @param nextStep
     * @param startTime
     * @param endTime
     * @return
     * @throws java.text.ParseException
     */
    public List<DBObject> groupByFour(int step, int nextStep, String startTime, String endTime) throws ParseException {
        String start = "P" + step;
        String end = "P" + nextStep;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //match
        //start & end !="null"

        BasicDBObject[] arrayCond = {
                new BasicDBObject("time", new BasicDBObject("$gte", sdf.parse(startTime))),
                new BasicDBObject("time", new BasicDBObject("$lt", sdf.parse(endTime))),
                new BasicDBObject(start, new BasicDBObject("$ne", "null")),
                new BasicDBObject(end, new BasicDBObject("$ne", "null")),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //$project
        DBObject fields = new BasicDBObject(start, 1);
        fields.put(end, 1);
        fields.put("session", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject _group = new BasicDBObject(start, "$" + start);
        _group.put(end, "$" + end);
        _group.put("session", "$session");
        //_group
        DBObject groupFields = new BasicDBObject("_id", _group);
        groupFields.put("nums", new BasicDBObject("$sum", 1));  //
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_group", 1));
        //run
        DBObject out = new BasicDBObject("$out", "tmp_out");
        List<DBObject> pipeline = Arrays.asList(match, project, group, sort, out);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = path.aggregate(pipeline, options);
        List<DBObject> pathGroupList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            pathGroupList.add(item);
        }
        cursor.close();
        return pathGroupList;
    }


    public List<DBObject> groupBySessionID(int step, int nextStep, String sessionID) throws ParseException {
        String start = "P" + step;
        String end = "P" + nextStep;
        //match
        //start & end !="null"
        BasicDBObject[] arrayCond = {
                new BasicDBObject("session",sessionID),
                new BasicDBObject(start, new BasicDBObject("$ne", "null")),
                new BasicDBObject(end, new BasicDBObject("$ne", "null")),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //$project


        DBObject fields = new BasicDBObject(start, 1);
        fields.put(end, 1);
        fields.put("session", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject _group = new BasicDBObject(start, "$" + start);
        _group.put(end, "$" + end);
        _group.put("session", "$session");

        //_group
        DBObject groupFields = new BasicDBObject("_id", _group);
        groupFields.put("nums", new BasicDBObject("$sum", 1));  //��Ϊ1
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_group", 1));
        //run
        DBObject out = new BasicDBObject("$out", "tmp_out");
        List<DBObject> pipeline = Arrays.asList(match, project, group, sort, out);


        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();

        Cursor cursor = path.aggregate(pipeline, options);
        List<DBObject> pathGroupList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            pathGroupList.add(item);
        }
        return pathGroupList;
    }

    /**
<<<<<<< HEAD
     * 获得P1的所有节点
=======
     * ��Ҫ����ʱ�����
     * ���P1��URL���ڼ������
>>>>>>> seuvislogwws/master
     *
     * @param startTime
     * @param endTime
     * @return
<<<<<<< HEAD
     * @throws java.text.ParseException
=======
     * @throws ParseException
>>>>>>> seuvislogwws/master
     */
    public List<DBObject> getDepth0(String startTime, String endTime) throws ParseException {
        String start = "P1";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //match
        //start!="null"

        BasicDBObject[] arrayCond = {
                new BasicDBObject("time", new BasicDBObject("$gte", sdf.parse(startTime))),
                new BasicDBObject("time", new BasicDBObject("$lt", sdf.parse(endTime))),
                new BasicDBObject(start, new BasicDBObject("$ne", "null")),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //project
        DBObject fields = new BasicDBObject(start, 1);
        fields.put("session", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject _group = new BasicDBObject(start, "$" + start);
        _group.put("session", "$session");
        //_group
        DBObject groupFields = new BasicDBObject("_id", _group);
        groupFields.put("nums", new BasicDBObject("$sum", 1));  //��Ϊ1
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_group", 1));
        //run
        DBObject out = new BasicDBObject("$out", "tmp_out");
        List<DBObject> pipeline = Arrays.asList(match, project, group, sort, out);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = path.aggregate(pipeline, options);
        List<DBObject> pathGroupList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            pathGroupList.add(item);
        }
        return pathGroupList;
    }

    public List<DBObject> getDepth0(String sessionID) throws ParseException {
        String start = "P1";
        //match
        //start !="null"
        BasicDBObject[] arrayCond = {
                new BasicDBObject("session",sessionID),
                new BasicDBObject(start, new BasicDBObject("$ne", "null")),
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);
        //$project

        DBObject fields = new BasicDBObject(start, 1);
        fields.put("session", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject _group = new BasicDBObject(start, "$" + start);
        _group.put("session", "$session");

        //_group
        DBObject groupFields = new BasicDBObject("_id", _group);
        groupFields.put("nums", new BasicDBObject("$sum", 1));

        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_group", 1));
        //run
        DBObject out = new BasicDBObject("$out", "tmp_out");
        List<DBObject> pipeline = Arrays.asList(match, project, group, sort, out);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = path.aggregate(pipeline, options);
        List<DBObject> pathGroupList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            pathGroupList.add(item);
        }
        return pathGroupList;
    }




    public  List<DBObject> getStateNums(int state){



       // DBObject notNull = new BasicDBObject("$ne", "null");


        //DBObject match = new BasicDBObject("$match",new BasicDBObject("P" + state, notNull));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String beginTime = "2014/08/09 00:00:00";
        String endTime = "2014/08/09 23:59:59";
        Date begin = new Date();
        Date end = new Date();
        try {
            begin = dateFormat.parse(beginTime);
            end = dateFormat.parse(endTime);
            //System.out.println(begin);


        }catch (Exception e){}



        String p = "P" + state;
        DBObject notNull = new BasicDBObject("$ne", "null");

        BasicDBObject pNotNull = new BasicDBObject("P" + state, notNull);
        // sessionGroup.put("")

        BasicDBObject[] arrayCond = {
                pNotNull,
                new BasicDBObject("time", new BasicDBObject("$gt", begin)),
                new BasicDBObject("time", new BasicDBObject("$lt", end))
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);


        DBObject sessionGroup = new BasicDBObject("_id","$P" + state);
        sessionGroup.put("nums", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", sessionGroup);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1));
        DBObject limit = new BasicDBObject("$limit", 10);

        List<DBObject> pipeline = Arrays.asList(match, group, sort, limit);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();



        Cursor cursor = path.aggregate(pipeline, options);

        List<DBObject> lists = new ArrayList<>();

        while(cursor.hasNext()){

            lists.add(cursor.next());
        }




        return lists;
       // return lists;

    }

    public  List<DBObject> getStateInfo(int state){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String beginTime = "2014/08/09 00:00:00";
        String endTime = "2014/08/09 23:59:59";
        Date begin = new Date();
        Date end = new Date();
        try {
            begin = dateFormat.parse(beginTime);
            end = dateFormat.parse(endTime);
            //System.out.println(begin);


        }catch (Exception e){}



        String p = "P" + state;
        DBObject notNull = new BasicDBObject("$ne", "null");

        BasicDBObject pNotNull = new BasicDBObject("P" + state, notNull);
        // sessionGroup.put("")

        BasicDBObject[] arrayCond = {
                pNotNull,
                new BasicDBObject("time", new BasicDBObject("$gt", begin)),
                new BasicDBObject("time", new BasicDBObject("$lt", end))
        };
        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", arrayCond);
        DBObject match = new BasicDBObject("$match", cond);

        // sessionGroup.put("")

        //DBObject match = new BasicDBObject("$match",new BasicDBObject(p, notNull));

        DBObject dbObject = new BasicDBObject("session", 1);
        dbObject.put(p, 1);
        dbObject.put("_id", 0);
        DBObject project = new BasicDBObject("$project",dbObject);


        List<DBObject> pipeline = Arrays.asList(match, project);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();

        Cursor cursor = path.aggregate(pipeline, options);
        List<DBObject> lists = new ArrayList<>();

        while(cursor.hasNext()){

            lists.add(cursor.next());
        }
        //System.out.println(lists);
        return lists;

    }


}
