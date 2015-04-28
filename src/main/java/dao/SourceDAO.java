package dao;

import com.mongodb.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

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
}
