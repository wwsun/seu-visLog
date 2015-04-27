package dao;

import com.mongodb.*;

public class LevelDAO {
    private DBCollection level;
    public LevelDAO(final DB siteDatabase) { level = siteDatabase.getCollection("level"); }

    public String getCategoryById(int code) {
        QueryBuilder builder = QueryBuilder.start("LEVEL3_ID").is(code);
        DBObject object = level.findOne(builder.get(), new BasicDBObject("_id", false)
                .append("regex", false));
        String response;
        if(object!=null)
            response = (String) object.get("LEVEL3_NAME");
        else
            response = "none";
        return response;
    }
}
