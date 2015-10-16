package dao;

import com.mongodb.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SourceDAO {
    private DBCollection source;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public SourceDAO(final DB siteDatabase) {
        source = siteDatabase.getCollection("source");
    }

    public JsonArray getTopSearchEnginesByDate(String date, int limit) {
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("type").is(0).and(new BasicDBObject("date", sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = source.find(builder.get(), new BasicDBObject("source", true)
                .append("sum", true)
                .append("date", true)
                .append("_id", false))
                .sort(new BasicDBObject("sum", -1))
                .limit(limit);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("source"))
                    .add("date", sdf.format(object.get("date")))
                    .add("dup", (Integer) object.get("sum")));
        }
        cursor.close();
        return jsonArrayBuilder.build();
    }
}
