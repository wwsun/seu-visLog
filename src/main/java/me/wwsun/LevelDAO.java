package me.wwsun;

import com.mongodb.*;

/**
 * Created by Weiwei on 12/27/2014.
 */
public class LevelDAO {
    private DBCollection level;
    public LevelDAO(final DB siteDatabase) { level = siteDatabase.getCollection("level"); }

    public String getCategoryById(int code) {
        QueryBuilder builder = QueryBuilder.start("ID").is(code);
        DBObject object = level.findOne(builder.get(), new BasicDBObject("_id", false)
                .append("regex", false));
        String response;
        if(object!=null)
            response = (String) object.get("name");
        else
            response = "none";
        return response;
    }
}
