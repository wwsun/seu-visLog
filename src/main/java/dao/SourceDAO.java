package dao;

import com.mongodb.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class SourceDAO {
    private DBCollection source;

    public SourceDAO(final DB siteDatabase) {
        source = siteDatabase.getCollection("source");
    }

    public JsonArray getTopSearchEngines(int limit) {
        QueryBuilder builder = QueryBuilder.start("type").is(0);
        DBCursor cursor = source.find(builder.get(), new BasicDBObject("source",true)
                .append("sum", true).append("_id", false))
                .sort(new BasicDBObject("sum", -1))
                .limit(limit);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("source"))
                    .add("dup", (Integer) object.get("sum")));
        }
        cursor.close();
        return jsonArrayBuilder.build();
    }

    public JsonArray getTopSearchEngines(String start, String end) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        BasicDBObject[] arrayCond={
                new BasicDBObject("date",new BasicDBObject("$gt",sdf.parse(start))),
                new BasicDBObject("date", new BasicDBObject("$lte", sdf.parse(end))),
                new BasicDBObject("type",new BasicDBObject("$eq",0))
        };
        BasicDBObject cond=new BasicDBObject();
        cond.put("$and",arrayCond);
        DBObject match = new BasicDBObject("$match", cond);

        DBObject fields = new BasicDBObject("source", 1);
        fields.put("sum", 1);
        DBObject project = new BasicDBObject("$project", fields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("sum", -1));

        List<DBObject> pipeline = Arrays.asList(match, project, sort);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).batchSize(10000).build();
        Cursor cursor = source.aggregate(pipeline, options);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        while(cursor.hasNext()) {
            DBObject object = cursor.next();
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("source"))
                    .add("dup", (Integer) object.get("sum")));
        }
        cursor.close();
        return jsonArrayBuilder.build();
    }
}
