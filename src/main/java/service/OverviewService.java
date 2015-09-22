package service;

import com.mongodb.*;
import dao.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.List;

/**
 * 数据整合: For OverviewDataResource
 */
public class OverviewService {

    SessionDAO sessionDAO;
    CountryDAO countryDAO;
    NodesDAO nodesDAO;
    LevelDAO levelDAO;
    LeaveDAO leaveDAO;
    StatusDAO statusDAO;
    LogDAO logDAO;
    PathServiceTool pathServiceTool;

    public OverviewService(final DB db) {
        sessionDAO = new SessionDAO(db);

        countryDAO = new CountryDAO(db);
        nodesDAO = new NodesDAO(db);
        levelDAO = new LevelDAO(db);

        leaveDAO = new LeaveDAO(db);
        statusDAO = new StatusDAO(db);
        logDAO = new LogDAO(db);
        pathServiceTool = new PathServiceTool(db);
    }

    public DBObject getSessionDistributionByDate(String date) {
        return sessionDAO.getSessionsByDate(date);
    }

    // 某天的会话总数，出错请求数，询盘会话占比
    public DBObject getCountErrorsInquiryByDate(String date) {
        //整合数据
        DBObject dataObject = new BasicDBObject();
        //从表country得到会话总数
        //从表country得到询盘会话数
        //然后计算询盘会话占比
        int session_nums_all = countryDAO.getSessionNumsByDate(date);
        int inquiry_nums_all = countryDAO.getInquiryNumsByDate(date);
        double InquiryPercent = (double) inquiry_nums_all / (double) session_nums_all;
        //从status中得到status=200其他的请求数目，全部认为是出错的
        int error_request_nums = statusDAO.getNumsByDate(date);
        dataObject.put("Count", session_nums_all);
        dataObject.put("Inquiry", InquiryPercent);
        dataObject.put("Errors", error_request_nums);
        return dataObject;
    }

    public DBObject getClicksTrendsByDate(String date) {
        return statusDAO.getClickTrendsOneDayByDate(date);
    }

    public DBObject getThruputOneDayByDate(String date) {
        return statusDAO.getThruputOneDayByDate(date);
    }

    public List<DBObject> getStatusListOneDayByDate(String date) {
        return statusDAO.getStatusListOneDayByDate(date);
    }

    public JsonArray getTopSearchEngines(int limit) {

        //return sourceDAO.getTopSearchEngines(limit);
        List<DBObject> list = logDAO.getTopSearchEngines(limit);
        JsonArrayBuilder builder = Json.createArrayBuilder();

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer) object.get("_id"));
            builder.add(Json.createObjectBuilder()
                    .add("name", cate)
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }

    //频繁访问类别
    public JsonArray getTopCategories(int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = nodesDAO.getHotCategory(limit);
        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer) object.get("_id"));
            builder.add(Json.createObjectBuilder()
                    .add("name", cate)
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }

    //频繁访问类别中某一类别下面的top页面 [{name:"XXX", dup:"XXX"}]
    public JsonArray getTopPagesByCategory(String category, int limit) {
        //首先根据传入的category获得对应的ID
        int level3_id = levelDAO.getIDByName(category);
        List<DBObject> list = nodesDAO.getTopPagesByCategory(level3_id, limit);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }

    //频繁访问页面
    public JsonArray getFrequentVisitedPages(int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = nodesDAO.getHotPages(limit);
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }

    //主要国家来源
    public JsonArray getTopCountriesFlow(int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = countryDAO.getGeoDistribution(limit);
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("name"))
                    .add("dup", (Integer) object.get("value")));
        }
        return builder.build();
    }

    public JsonArray getMainLandingCategories(int limit) {

        JsonArrayBuilder builder = Json.createArrayBuilder();
        //List<DBObject> list = landsDAO.getMainLandingCategories(limit);
        List<DBObject> list = logDAO.getMainLandingCategories(limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer) object.get("landID"));

            builder.add(Json.createObjectBuilder()
                    .add("name", cate).add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

    //主要跳离页的类别
    public JsonArray getMainDropOffCategories(int limit) {

        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = leaveDAO.getMainDropoffCategories(limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer) object.get("leaveID"));
            builder.add(Json.createObjectBuilder()
                    .add("name", cate).add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

    public JsonArray getSourceSessionNums() {
        List<DBObject> sources = logDAO.getSourceSessionNums();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (DBObject object : sources) {
            builder.add(Json.createObjectBuilder()
                    .add("category", (Integer) object.get("_id"))
                    .add("dup", (Integer) object.get("sessions")));
        }
        return builder.build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    public JsonArray getMainLandingPages(int limit) {
        return logDAO.getTopLandingPages(limit);
    }

    //log表中某一类别下面的着陆页（land）的top页面:[{name:"XXX", dup:"XXX"}]
    public JsonArray getTopLandingPagesByCategory(String category, int limit) {
        //首先根据传入的category获得对应的ID
        int level3_id = levelDAO.getIDByName(category);
        List<DBObject> list = logDAO.getTopLandPagesByCategory(level3_id, limit);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }

    public JsonArray getTopSourcePages(int limit) {
        return logDAO.getTopSourcePages(limit);
    }

    //根据某一来源页URL,获得其去向的类别top
    public JsonArray getTopCategoriesBySourcePage(String url, int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = logDAO.getTopCategoriesBySourcePages(url, limit);
        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer) object.get("level3_id"));

            builder.add(Json.createObjectBuilder()
                    .add("name", cate).add("dup", (Integer) object.get("dup")));
        }
        return builder.build();
    }

    //根据某一来源页URL，获得其去向的页面top
    public JsonArray getTopPagesBySourcePage(String url, int limit) {
        return logDAO.getTopPagesBySourcePage(url, limit);
    }

    //跳离页某一类别下面的top页面
    // [{name:"XXX", dup:"XXX"}]
    public JsonArray getTopDropOffPagesByCategory(String category, int limit) {
        //首先根据传入的category获得对应的ID
        int level3_id = levelDAO.getIDByName(category);

        List<DBObject> list = leaveDAO.getTopLeavePagesByCategory(level3_id, limit);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }
/////////////////////////////////////////////////////////////////////////////////////////////



}
