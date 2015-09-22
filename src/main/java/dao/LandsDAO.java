package dao;

import com.mongodb.*;
import com.mysql.jdbc.PacketTooBigException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LandsDAO {

    private DBCollection land;

    public LandsDAO(final DB db) { land = db.getCollection("land"); }

    /**
     * get main landing categories
     * @param LIMIT is the number of results you want to returned
     * @return the main landing categories
     */
    public List<DBObject> getMainLandingCategories(final int LIMIT) {
        QueryBuilder builder = QueryBuilder.start();
        DBCursor cursor = land.find(builder.get(), new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1)).limit(LIMIT);

        List<DBObject> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }

    public List<DBObject> getMainLandingCategories(String  start,String end) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        BasicDBObject cond=new BasicDBObject();
        cond.put("$gt",sdf.parse(start));
        cond.put("$lte",sdf.parse(end));
        BasicDBObject composeCod = new BasicDBObject();
        composeCod.put("date", cond);

        //QueryBuilder builder = QueryBuilder.start();
        DBCursor cursor = land.find(composeCod, new BasicDBObject("_id", false))
                .sort(new BasicDBObject("sum", -1));

        List<DBObject> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }

}
