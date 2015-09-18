package dao;

import com.mongodb.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.List;

public class LevelDAO {
    private DBCollection level;

    public LevelDAO(final DB siteDatabase) {
        level = siteDatabase.getCollection("level");
    }

    public String getCategoryById(int code) {
        QueryBuilder builder = QueryBuilder.start("LEVEL3_ID").is(code);
        DBObject object = level.findOne(builder.get(), new BasicDBObject("_id", false)
                .append("regex", false));
        String response;
        if (object != null)
            response = (String) object.get("LEVEL3_NAME");
        else
            response = "none";
        return response;
    }

    public String[][] getRegexName() {
        DBObject query = new BasicDBObject();


        DBObject keys = new BasicDBObject();
        keys.put("LEVEL3_NAME", 1);
        keys.put("REGEX", 1);
        keys.put("_id", 0);

        DBCursor cursor = level.find(query, keys);
        List<DBObject> str = cursor.toArray();
        String[][] RegexName = new String[str.size()][2];
        for (int i = 0; i < str.size(); i++) {
            Object regex = str.get(i).get("REGEX");
            Object name = str.get(i).get("LEVEL3_NAME");
            RegexName[i][0] = regex.toString();
            RegexName[i][1] = name.toString();
        }
        return RegexName;
    }


    //传入LEVEL3_NAME，返回LEVEL3_ID
    public int getIDByName(String name) {
        QueryBuilder builder = QueryBuilder.start("LEVEL3_NAME").is(name);
        DBObject object = level.findOne(builder.get(), new BasicDBObject("_id", false)
                .append("LEVEL3_ID", true));
        int response;
        if (object != null)
            response = (int) object.get("LEVEL3_ID");
        else
            response = 0000; //如果查不到，那就返回值，不能是0，因为默认没有类别的话是0
        return response;
    }

    //获得搜索引擎所对应的ID
    public List<Integer> getSearchEngineID(){
        String LEVEL1_NAME="搜索引擎";  //搜索引擎
        int LEVEL1_ID=1000012;//搜索引擎
        //QueryBuilder builder = QueryBuilder.start("LEVEL1_NAME").is(LEVEL1_NAME);
        QueryBuilder builder = QueryBuilder.start("LEVEL1_ID").is(LEVEL1_ID);
        DBCursor cursor = level.find(builder.get(), new BasicDBObject("_id", false)
                .append("LEVEL3_ID", true));

        List<Integer> list=new ArrayList<Integer>();
        while(cursor.hasNext()){
            DBObject item=cursor.next();
            list.add((Integer)item.get("LEVEL3_ID"));
        }
        return list;
    }


    public static void main(String args[]) throws ParseException,IOException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db= mongoClient.getDB("huawei");
       LevelDAO levelDAO = new LevelDAO(db);
        List<Integer> list=levelDAO.getSearchEngineID();
        System.out.println(list.size());
        System.out.println(list);
    }

}
