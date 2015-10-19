package dao;

import com.mongodb.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LandsDAO {

    private DBCollection land;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LandsDAO(final DB db) {
        land = db.getCollection("land");
    }

    public List<DBObject> getMainLandingCategoriesByDate(String date, final int LIMIT) {
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("date").is(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = land.find(builder.get(), new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1)).limit(LIMIT);

        List<DBObject> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }

}
