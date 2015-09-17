package dao;

import com.mongodb.*;
import com.sun.org.apache.bcel.internal.classfile.FieldOrMethod;
import service.CustomerSourceService;

import java.io.IOException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/5/18.
 */
public class FormDAO {
    private DBCollection form;

    public FormDAO(final DB siteDatabase) {
        form = siteDatabase.getCollection("form");
    }

    //将ReceiveDataResource的数据写入到数据库中
    public void insertForm(String schema, String hs_log) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] fields = schema.split(",");
        String[] log_parts = hs_log.split(",");
        DBObject document = new BasicDBObject();
        //field
        if (!(fields.length == log_parts.length)) {  //数据ok才插入
            return;
        }
        for (int i = 0; i < log_parts.length; i++) {
            try {
                if (fields[i].equals("loadTime") || fields[i].equals("focusTime") || fields[i].equals("blurTime")) {
                    if (!log_parts[i].equals(""))
                        document.put(fields[i], sdf.parse(log_parts[i]));
                } else {
                    if (fields[i].equals("costTime")) {

                        if (log_parts[i].equals("")) {
                            document.put(fields[i], 0);

                        } else {
                            document.put(fields[i], Float.parseFloat(log_parts[i]));
                        }
                    } else {
                        document.put(fields[i], log_parts[i]);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //在最后添加一个是否已经标注的flag(false，true)
        document.put("flag", false);
        this.form.insert(document);

    }

    public List getList() {
        List<DBObject> lists = new ArrayList<>();
        DBObject dbObject = new BasicDBObject("type", "text");
        DBCursor collections = form.find(dbObject);
        collections.next();
        for (DBObject obj : collections) {

            lists.add(obj);
        }
        return lists;
    }

    /**
     * example:所有用户填写email项花费的平均时间
     * <p/>
     * {
     * "_id": "email",
     * "averageTime": 7.24399995803833
     * }
     *
     * @return 用户填写各项所花费的平均时间
     */
    public List getAvgTime() {

        DBObject match = new BasicDBObject("$match", new BasicDBObject("type", "text"));
        DBObject groupFileds = new BasicDBObject("_id", "$name");
        groupFileds.put("averageTime", new BasicDBObject("$avg", "$costTime"));

        DBObject group = new BasicDBObject("$group", groupFileds);
        List<DBObject> pipeline = Arrays.asList(match, group);
        AggregationOutput output = form.aggregate(pipeline);

        List<DBObject> lists = new ArrayList<>();
        for (DBObject obj : output.results()) {

            lists.add(obj);
        }
        return lists;

    }

    /**
     * 将各个项目未填的次数放入map中返回
     * example:
     * {ideal-button=2, zip=2, phone=2, username=1, website=1, email=1, reset=2, date=2, password=1, comments=2}
     *
     * @return 统计各个项未被填写的次数
     */
    public Map<String, Integer> getNotWriteNum() {
        DBObject query = new BasicDBObject("type", "submit");
        DBObject fieds = new BasicDBObject("text", 1);
        fieds.put("type", 1);
        DBCursor cursor = form.find(query, fieds);
        Map<String, Integer> map = new HashMap<>();
        for (DBObject dbObject : cursor) {

            String[] str = dbObject.get("text").toString().split(" ");

            for (String s : str) {
                if (map.get(s) != null)
                    map.put(s, map.get(s) + 1);
                else
                    map.put(s, 1);
            }
        }

        // System.out.println(map);
        return map;
    }

    /**
     * 计算每个填写项被用户改变的次数
     *
     * @return 再用户所做的填写中，各项被改变的次数
     */
    public List getChangeNums() {

        DBObject obj = new BasicDBObject("_id", "$name");
        obj.put("nums", new BasicDBObject("$sum", 1));

        DBObject group = new BasicDBObject("$group", obj);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1));
        List<DBObject> pipeline = Arrays.asList(group, sort);
        AggregationOutput output = form.aggregate(pipeline);
        List<DBObject> lists = new ArrayList<>();

        for (DBObject ob : output.results()) {
            lists.add(ob);
        }
        return lists;
    }


    /**
     * @return 总共做了多少次提交
     */
    public Integer getSubmitNum() {

        DBObject obj = new BasicDBObject("type", "submit");
        return form.find(obj).length();
    }

    public static void main(String[] args) throws IOException, ParseException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        FormDAO formDAO = new FormDAO(db);

        Map map = formDAO.getNotWriteNum();



    }
}
