package dao;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2015/5/31.
 */
public class BrowserDAO {
    private DBCollection browser;
    public BrowserDAO(final DB siteDatabase) { browser = siteDatabase.getCollection("browser"); }

    public List<DBObject> getSourceBrowsers(){
        DBObject groupFields = new BasicDBObject("_id", "$browser");
        groupFields.put("sum", new BasicDBObject("$sum", "$sum"));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort =new BasicDBObject("$sort",new BasicDBObject("sum",1));

        List<DBObject> pipeline = Arrays.asList(group, sort);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = browser.aggregate(pipeline, options);

        List<DBObject> list = new ArrayList<DBObject>();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();
            newObj.put("browser", object.get("_id"));
            newObj.put("sum", object.get("sum"));
            list.add(newObj);
        }
        cursor.close();
        return list;
    }
}
