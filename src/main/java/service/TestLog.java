package service;

import com.mongodb.*;
import org.bson.types.ObjectId;
import svm.SemanticAnnotation.Predict;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 根据前端采集的数据，造日志数据
 */
public class TestLog {
    public static void main(String[] args) throws IOException, ParseException {
        String session = "sessiontest1";

        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        //EventsDAO eventsDAO = new  EventsDAO(db);
        DBCollection events = db.getCollection("events");
        DBCollection log = db.getCollection("log");
        //取数据，构造日志
        QueryBuilder builder = QueryBuilder.start("ip").is("223.3.75.101");
        DBCursor cursor = events.find(builder.get(), new BasicDBObject("_id", false).append("ip", true)
                .append("url", true).append("loadtime", true)).sort(new BasicDBObject("loadtime", 1));



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Set<String> hs = new HashSet<String>();
        String ip = "";
        while (cursor.hasNext()) {
            DBObject cursorItem = cursor.next();
            StringBuilder sbuilder = new StringBuilder();
            ip = (String) cursorItem.get("ip");
            String url = (String) cursorItem.get("url");
            String time = sdf.format(cursorItem.get("loadtime"));  //有的为空，暂时用不到

            //System.out.println(time);
            sbuilder.append(time);
            sbuilder.append("~");
            sbuilder.append(url);
            hs.add(sbuilder.toString());
        }

        Object[] array = hs.toArray();
        Arrays.sort(array);

        int session_last = 0;
        Date dF = sdf.parse(((String) array[0]).split("~")[0]);
        Date dL = sdf.parse(((String) array[array.length - 1]).split("~")[0]);
        session_last = Integer.parseInt("" + (dL.getTime() - dF.getTime()) / 1000);//second
        int session_clicks = array.length;

        List<DBObject> dbList = new ArrayList<DBObject>();


        for (int i = 0; i < array.length; i++) {
            DBObject b = new BasicDBObject();
            String[] str = ((String) array[i]).split("~");
            b.put("session", session);
            b.put("ip", ip);
            b.put("session_seq", (i + 1));
            b.put("session_clicks", session_clicks);
            b.put("session_last", session_last);
            b.put("country", "China");
//           /b.put("dateTime", sdf.parse(str[0]));
            b.put("dateTime", str[0]);
            if (i > 0) {
                b.put("referer", ((String) array[i - 1]).split("~")[1]);
            } else {
                b.put("referer", "-");
            }
            b.put("request", str[1]);
            b.put("refererID", 0000);
            b.put("refererType", 1);
            b.put("requestID", 0000);
            dbList.add(b);
        }
        for(DBObject object:dbList){
            System.out.println(object);
        }
       // log.insert(dbList);
    }
}
