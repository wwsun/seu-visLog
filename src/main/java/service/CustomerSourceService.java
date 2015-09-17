package service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import dao.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 整合：For  CustomerDataResource
 * 国家维度细分数据
 */
public class CustomerSourceService {
    static DecimalFormat df = new DecimalFormat("#.00");
    CountryDAO countryDAO;
    BrowserDAO browserDAO;
    DeviceDAO deviceDAO;
    CFDAO cfDAO;
    CTDAO ctDAO;
    FormDAO formDAO;
    public CustomerSourceService(final DB db) {
        countryDAO = new CountryDAO(db);
        browserDAO = new BrowserDAO(db);
        deviceDAO = new DeviceDAO(db);
        formDAO = new FormDAO(db);
        cfDAO = new CFDAO(db);
        ctDAO = new CTDAO(db);
    }

    //国家维度去细分数据
    public JsonArray getGeoDetail(int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> details = countryDAO.getGeoDetail(limit);
        for (DBObject obj : details) {
            String country = obj.get("_id").toString();
            Integer sessions = (Integer) obj.get("total_session");
            Integer last = (Integer) obj.get("total_last");
            Integer pages = (Integer) obj.get("total_pages");
            Integer jumps = (Integer) obj.get("total_jumps");
            Integer inquiries = (Integer) obj.get("total_inquiry");

            float bounceRate = (float) jumps / sessions;
            float avgPages = (float) pages / sessions;
            float avgDuratino = (float) last / sessions;
            float conversionRate = (float) inquiries / sessions;

            builder.add(Json.createObjectBuilder().add("country", country)
                    .add("sessions", sessions)
                    .add("bounceRate", bounceRate)
                    .add("avgPages", avgPages)
                    .add("avgDuration", avgDuratino)
                    .add("goalConversion", conversionRate));
        }
        return builder.build();
    }

    public DBObject getGeoSessionDistribution(String country) {
        return countryDAO.getSessionDistribution(country);
    }

    public List<DBObject> getConversionFunnel(String date) {
            return cfDAO.getConversionFunnel(date);
    }

    public List<DBObject> getConversionList(String date) {

        List<DBObject> list = ctDAO.getConversionList(date);
        List<DBObject> listReturn =new ArrayList<DBObject>();
        //获得所有seesion个数
        int totalSession = 0;
        for (DBObject ob : list) {
            totalSession += Integer.parseInt(ob.get("targetSum").toString());
        }
        for (DBObject ob : list) {
            if (ob.get("targetType").equals("-")) {//去掉无法识别的产品类别
                continue;
            } else {
                //add
                ob.put("totalAll", totalSession);
                ob.put("doneOfSum", Double.parseDouble(df.format((double) (Integer.parseInt(ob.get("targetDone").toString())) / (double) (Integer.parseInt(ob.get("targetSum").toString())))));
                ob.put("doneOfAll", Double.parseDouble(df.format((double) (Integer.parseInt(ob.get("targetDone").toString())) / (double) totalSession)));
                listReturn.add(ob);
            }
        }
        list.clear();
        return listReturn;
    }

    public JsonArray getSourceBrowsers() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> browsers = browserDAO.getSourceBrowsers();
        for (DBObject obj : browsers) {
            String browser = obj.get("browser").toString();
            Integer sum = (Integer) obj.get("sum");
            builder.add(Json.createObjectBuilder().add("browser", browser)
                    .add("sum", sum));
        }
        return builder.build();
    }

    public JsonArray getSourceDevices() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> devices = deviceDAO.getSourceBrowsers();
        for (DBObject obj : devices) {
            String browser = obj.get("device").toString();
            Integer sum = (Integer) obj.get("sum");
            builder.add(Json.createObjectBuilder().add("device", browser)
                    .add("sum", sum));
        }
        return builder.build();
    }


    public JsonArray getChangeNums(){

        JsonArrayBuilder jab = Json.createArrayBuilder();
        List<DBObject>  items = formDAO.getChangeNums();
        for(DBObject obj : items){
            if(((String)obj.get("_id")).equals("")) continue;
            String type =getMatchType((String) obj.get("_id"));
            if (type.equals("")) continue;
            Integer changeNums = (Integer)obj.get("nums");
            jab.add(Json.createObjectBuilder().add("type", type).add("nums",changeNums));
        }
        return jab.build();
    }

    public JsonArray getNotWriteNums(){

        JsonArrayBuilder jab = Json.createArrayBuilder();
        Map<String,Integer> maps = formDAO.getNotWriteNum();

        for(Map.Entry<String, Integer> entry : maps.entrySet()){
            String type =entry.getKey();
            if(type.equals("")) continue;
            type =getMatchType(type);
            if (type.equals("")) continue;
            Integer notWriteNums = entry.getValue();
            jab.add(Json.createObjectBuilder().add("type", type).add("nums",notWriteNums));
        }
        return jab.build();
    }

    public JsonArray getAvgTime(){

        JsonArrayBuilder jab = Json.createArrayBuilder();
        List<DBObject> items = formDAO.getAvgTime();
        for(DBObject obj : items){

            if(((String)obj.get("_id")).equals("")) continue;
            String type =getMatchType((String) obj.get("_id"));
            if (type.equals("")) continue;
            Double time = (Double)obj.get("averageTime");
            time = Double.parseDouble(String.format("%.2f",time));
            jab.add(Json.createObjectBuilder().add("type", type).add("averageTime",time));
        }
        return jab.build();
    }

    public String getMatchType(String type){
        String str = "";
        switch (type){
            case "username": str = "用户名";break;
            case "zip": str="邮政编码";break;
            case "phone":str = "电话";break;
            case "website":str="个人主页";break;
            case "date": str="出生日期";break;
            case "password": str="密码";break;
            case "comments": str="个人描述";break;
            case "states": str="国籍"; break;
            case"langs":str="语言";break;
            case "skill":str="精通语言";break;
            case "email": str="邮箱";break;
        }

        return str;

    }

}
