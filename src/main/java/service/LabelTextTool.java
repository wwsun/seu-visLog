package service;

import com.mongodb.*;
import org.bson.types.ObjectId;
import svm.SemanticAnnotation.Predict;

import java.io.IOException;
import java.util.*;

/**
 * 数据标注，读取数据库中未标注的记录，标注后更新记录
 */

public class LabelTextTool {

    //读取所有未标注的文本，然后调用SVM标注器标注，然后更新这一条记录
    public static void main(String[] args) throws IOException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        //EventsDAO eventsDAO = new  EventsDAO(db);
        DBCollection events = db.getCollection("events");

       //找出未标注的记录
        QueryBuilder builder = QueryBuilder.start("flag").is(false);
        DBCursor cursor = events.find(builder.get(), new BasicDBObject("_id", true).append("text", true).append("element", true));
        Map<ObjectId, String> updateMap = new HashMap<ObjectId, String>();
        while (cursor.hasNext()) {
            DBObject cursorItem = cursor.next();
            String text = (String) cursorItem.get("text");
            ObjectId _id = (ObjectId) cursorItem.get("_id");
            String element = (String) cursorItem.get("element");  //有的为空，暂时用不到
            String semantics = "";
            if (text.equals("") && element.equalsIgnoreCase("img")) {
                semantics = "产品图片";     //增加一类
            }
            //SVM标注
            semantics = Predict.SemanticPredict(text);
            updateMap.put(_id, semantics);
        }
        //批量更新
        Set<Map.Entry<ObjectId, String>> entrySet = updateMap.entrySet();
        for (Map.Entry<ObjectId, String> entry : entrySet) {
            DBObject condition = new BasicDBObject("_id", entry.getKey());
            DBObject setValue = new BasicDBObject();
            setValue.put("flag", true);  //已标注的标记
            setValue.put("semantics", entry.getValue());
            DBObject updateValue = new BasicDBObject("$set", setValue);
            //更新两个字段
            events.update(condition, updateValue);
        }

        System.out.println("Label, DONE");
    }
}
