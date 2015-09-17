package service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import dao.LevelDAO;
import dao.LogDAO;
import javax.json.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class LogService {
    LogDAO logDAO;
    LevelDAO levelDAO;

    public LogService(final DB siteDatabase) {
        levelDAO=new LevelDAO(siteDatabase);
        logDAO = new LogDAO(siteDatabase); }

    //用于按条件返回sessionList，
    public JsonArray getSessionListByCondition(String condition){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        //根据前端传回条件，获得符合条件的sessionList
        //最好前段
        List<DBObject> list=logDAO.getSessionListByCondition(condition);
        for(DBObject item:list){
            DBObject obj=(BasicDBObject)item.get("_id");
            String category=levelDAO.getCategoryById((Integer) obj.get("category"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date=sdf.format((Date)item.get("date"));
            builder.add(Json.createObjectBuilder()
                    .add("session", obj.get("session").toString())
                    .add("date", date)
                    .add("category", category)
                    .add("last", (Integer) obj.get("last"))
                    .add("pages", (Integer) obj.get("pages"))
                    .add("country", obj.get("country").toString()));
        }
        return builder.build();
    }
}
