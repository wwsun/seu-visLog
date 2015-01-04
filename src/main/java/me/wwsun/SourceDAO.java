package me.wwsun;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Weiwei on 12/23/2014.
 */
public class SourceDAO {

    private DBCollection source;

    public SourceDAO(final DB siteDatabase) {
        source = siteDatabase.getCollection("source");
    }

    public List getTopSearchEngines() {
        QueryBuilder builder = QueryBuilder.start("type").is(0);
        DBCursor cursor = source.find(builder.get(), new BasicDBObject("source",true)
                .append("sum", true).append("_id", false))
                .sort(new BasicDBObject("sum", -1))
                .limit(10);

        List list = new ArrayList();
        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            DBObject seItem = new BasicDBObject();
            seItem.put("name", obj.get("source"));
            seItem.put("dup", obj.get("sum"));
            list.add(seItem);

        }
        cursor.close();

        return list;
        //System.out.println(JSON.serialize(list));
    }
}
