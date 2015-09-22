package dao;

import com.mongodb.*;
import com.mysql.jdbc.PacketTooBigException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    private DBCollection leave;

    public LeaveDAO(final DB db) { leave = db.getCollection("leave"); }

    /**
     * get main drop off categories
     * @param LIMIT is the number of results you want to returned
     * @return the main drop off categories
     */
    public List<DBObject> getMainDropoffCategories(final int LIMIT) {
        QueryBuilder builder = QueryBuilder.start();
        DBCursor cursor = leave.find(builder.get(), new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1)).limit(LIMIT);

        List<DBObject> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }

    public List<DBObject> getMainDropoffCategories(String  start,String end) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        BasicDBObject cond=new BasicDBObject();
        cond.put("$gt",sdf.parse(start));
        cond.put("$lte",sdf.parse(end));
        BasicDBObject composeCode=new BasicDBObject();
        composeCode.put("date",cond);
        DBCursor cursor = leave.find(composeCode, new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1));

        List<DBObject> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }
}
