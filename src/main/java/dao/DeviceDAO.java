package dao;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2015/5/31.
 */
public class DeviceDAO {
    private DBCollection device;
    public DeviceDAO(final DB siteDatabase) { device = siteDatabase.getCollection("device"); }

    public List<DBObject> getSourceBrowsers(){

        DBObject groupFields = new BasicDBObject("_id", "$device");
        groupFields.put("sum", new BasicDBObject("$sum", "$sum"));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort =new BasicDBObject("$sort",new BasicDBObject("sum",1));

        List<DBObject> pipeline = Arrays.asList(group, sort);

        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = device.aggregate(pipeline, options);

        List<DBObject> list = new ArrayList<DBObject>();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            DBObject newObj = new BasicDBObject();
            newObj.put("device", object.get("_id"));
            newObj.put("sum", object.get("sum"));
            list.add(newObj);
        }
        cursor.close();
        return list;
    }
}
