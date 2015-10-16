package services;

import dao.*;

import com.mongodb.DB;
import com.mongodb.DBObject;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.text.SimpleDateFormat;
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

    /**
     *
     * @param date 日期
     * @return 返回指定日期不同时段的会话分布情况
     */
    public DBObject getSessionDistributionByDate(String date) {
        return sessionDAO.getSessionsByDate(date);
    }

    /**
     *
     * @param date 指定的日期
     * @return 返回指定日期数据的关键指标
     * 返回数据的格式为：{"total":"COUNT", "bounce_rate":"PERCENT", "inquiry_rate":"PERCENT"}
     */
    public JsonObject getSessionCountsAndBounceRateByDate(String date) {
        return Json.createObjectBuilder()
                .add("total", jumpDAO.getSessionCountsByDate(date))
                .add("bounce_rate", jumpDAO.getBounceRateByDate(date))
                .add("inquiry_rate", jumpDAO.getInquiryRateByDate(date))
                .build();
    }

    /**
     *
     * @param date 日期
     * @param limit 返回结果的个数
     * @return 返回指定日期数据的主要搜索引擎贡献
     */
    public JsonArray getTopSearchEnginesByDate(String date, int limit) {
        return sourceDAO.getTopSearchEnginesByDate(date, limit);
    }

    /**
     *
     * @param date 查询的日期，e.g. 2015-06-30
     * @param limit 返回的结果个数
     * @return 返回指定日期会话在类别上的分布
     */
    public JsonArray getTopCategoriesByDate(String date, int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = nodesDAO.getHotCategoriesByDate(date, limit);
        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById((Integer)object.get("_id"));
            builder.add(Json.createObjectBuilder()
                    .add("name", cate)
                    .add("dup", (Integer)object.get("nums")));
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

    public JsonArray getTopCountriesByDate(String date, int limit) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
                List<DBObject> list = countryDAO.getGeoDistributionByDate(date, limit);
        for (DBObject object : list) {
            builder.add(Json.createObjectBuilder()
                            .add("name", (String) object.get("name"))
                            .add("date", (String) object.get("date"))
                            .add("dup", (Integer) object.get("value")));
        }
        return builder.build();
    }

    public JsonArray getMainLandingCategoriesByDate(String date, int limit) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = landsDAO.getMainLandingCategoriesByDate(date, limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById(Integer.parseInt((String)object.get("landID")));

            builder.add(Json.createObjectBuilder()
                    .add("name", cate)
                    .add("date", sdf.format(object.get("date")))
                    .add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

    public JsonArray getMainDropOffCategoriesByDate(String date, int limit) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        JsonArrayBuilder builder = Json.createArrayBuilder();
        List<DBObject> list = leaveDAO.getMainDropoffCategoriesByDate(date, limit);

        for (DBObject object : list) {
            String cate = levelDAO.getCategoryById(Integer.parseInt((String)object.get("leaveID")));

            builder.add(Json.createObjectBuilder()
                    .add("name", cate)
                    .add("date", sdf.format(object.get("date")))
                    .add("dup", (Integer) object.get("sum")));
        }
        return builder.build();
    }

}
