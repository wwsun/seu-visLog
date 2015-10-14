package services;

import dao.*;

import com.mongodb.DB;
import com.mongodb.DBObject;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.text.ParseException;
import java.util.List;

public class OverviewService {

    SessionDAO sessionDAO;
    JumpDAO jumpDAO;
    SourceDAO sourceDAO;
    CountryDAO countryDAO;
    NodesDAO nodesDAO;
    LevelDAO levelDAO;
    LandsDAO landsDAO;
    LeaveDAO leaveDAO;

    public OverviewService(final DB db) {
        sessionDAO = new SessionDAO(db);
        jumpDAO = new JumpDAO(db);
        sourceDAO = new SourceDAO(db);
        countryDAO = new CountryDAO(db);
        nodesDAO = new NodesDAO(db);
        levelDAO = new LevelDAO(db);
        landsDAO = new LandsDAO(db);
        leaveDAO = new LeaveDAO(db);
    }

    public DBObject getSessionDistributionByDate(String date) {
        return sessionDAO.getSessionsByDate(date);
    }

    public DBObject getSessionDistributionByDate(String start,String end) throws ParseException{
        return sessionDAO.getSessionsByDate(start,end);
    }

    /**
     *
     * @return {"total":"COUNT", "bounce_rate":"PERCENT", "inquiry_rate":"PERCENT"}
     */
    public JsonObject getSessionCountsAndBounceRate() {
        return Json.createObjectBuilder()
                .add("total", jumpDAO.getSessionCounts())
                .add("bounce_rate", jumpDAO.getBounceRate())
                .add("inquiry_rate", jumpDAO.getInquiryRate())
                .build();
    }

    public JsonObject getSessionCountsAndBounceRate(String start, String end) throws  ParseException{
        return Json.createObjectBuilder()
                .add("total", jumpDAO.getSessionCounts(start,end))
                .add("bounce_rate", jumpDAO.getBounceRate(start,end))
                .add("inquiry_rate", jumpDAO.getInquiryRate(start,end))
                .build();
    }

    /**
     *
     * @param limit is the most number of results that returned
     * @return an array of main reference search engines as json format
     */
    public JsonArray getTopSearchEngines(int limit) {
        return sourceDAO.getTopSearchEngines(limit);
    }

    public JsonArray getTopSearchEngines(String start,String end,int limit) throws ParseException{
        return sourceDAO.getTopSearchEngines(start,end,limit);
    }

    /**
     *
     * @param limit is the most number of results that returned
     * @return an array of main categories that user flow retained
     */
    public JsonArray getTopCategories(int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = nodesDAO.getHotCategory(limit);
        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer)object.get("_id"));
            builder.add(Json.createObjectBuilder()
                            .add("name", cate)
                            .add("dup", (Integer)object.get("nums")));
        }
        return builder.build();
    }

    public JsonArray getTopCategories(String start, String end,int limit) throws ParseException{
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = nodesDAO.getHotCategory(start, end,limit);
        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer)object.get("_id"));
            builder.add(Json.createObjectBuilder()
                    .add("name", cate)
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }

    /**
     *
     * @param limit is the most number of results that returned
     * @return an array of the most frequent visited pages
     */
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

    public JsonArray getFrequentVisitedPages(String start,String end,int limit) throws ParseException{
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = nodesDAO.getHotPages(start,end,limit);
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("_id"))
                    .add("dup", (Integer) object.get("nums")));
        }
        return builder.build();
    }

    /**
     *
     * @param limit is the result you want to returned
     * @return [{name:"XXX", dup:"XXX"}]
     */
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

    public JsonArray getTopCountriesFlow(String start,String end,int limit) throws Exception {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = countryDAO.getGeoDistribution(start,end,limit);
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                    .add("name", (String) object.get("name"))
                    .add("dup", (Integer) object.get("value")));
        }
        return builder.build();
    }

    public JsonArray getMainLandingCategories(int limit) {

        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = landsDAO.getMainLandingCategories(limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById(Integer.parseInt((String)object.get("landID")));

            builder.add(Json.createObjectBuilder()
                .add("name", cate).add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

    public JsonArray getMainLandingCategories(String start,String end,int limit) throws ParseException{

        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = landsDAO.getMainLandingCategories(start,end,limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById(Integer.parseInt((String)object.get("landID")));

            builder.add(Json.createObjectBuilder()
                    .add("name", cate).add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

    public JsonArray getMainDropOffCategories(int limit) {

        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = leaveDAO.getMainDropoffCategories(limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById(Integer.parseInt((String)object.get("leaveID")));

            builder.add(Json.createObjectBuilder()
                    .add("name", cate).add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

    public JsonArray getMainDropOffCategories(String start,String end,int limit) throws ParseException{

        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = leaveDAO.getMainDropoffCategories(start, end,limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById(Integer.parseInt((String)object.get("leaveID")));

            builder.add(Json.createObjectBuilder()
                    .add("name", cate).add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

}
