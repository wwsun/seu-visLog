package dao;

import com.mongodb.*;

import java.util.List;

public class LevelDAO {
    private DBCollection level;

    public LevelDAO(final DB siteDatabase) {
        level = siteDatabase.getCollection("level");
    }

    public String getCategoryById(int code) {
        QueryBuilder builder = QueryBuilder.start("LEVEL3_ID").is(code);
        DBObject object = level.findOne(builder.get(), new BasicDBObject("_id", false)
                .append("regex", false));
        String response;
        if (object != null)
            response = (String) object.get("LEVEL3_NAME");
        else
            response = "none";
        return response;
    }

    /**
     * author: Xiaocheng TENG
     *
     * @return
     */
    public String[][] getRegexName() {
        DBObject query = new BasicDBObject();

        DBObject keys = new BasicDBObject();
        keys.put("LEVEL3_NAME", 1);
        keys.put("REGEX", 1);
        keys.put("_id", 0);

        DBCursor cursor = level.find(query, keys);
        List<DBObject> str = cursor.toArray();
        String[][] RegexName = new String[str.size()][2];
        for (int i = 0; i < str.size(); i++) {
            Object regex = str.get(i).get("REGEX");
            Object name = str.get(i).get("LEVEL3_NAME");
            RegexName[i][0] = regex.toString();
            RegexName[i][1] = name.toString();
        }
        return RegexName;
    }
}
