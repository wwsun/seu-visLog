package dao;

import com.mongodb.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    private DBCollection leave;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LeaveDAO(final DB db) { leave = db.getCollection("leave"); }

    public List<DBObject> getMainDropoffCategoriesByDate(String date, final int LIMIT) {
        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.start("date").is(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DBCursor cursor = leave.find(builder.get(), new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1)).limit(LIMIT);

        List<DBObject> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }
}
