package services;

import com.mongodb.DB;
import com.mongodb.DBObject;
import dao.JumpDAO;
import dao.SessionDAO;
import dao.SourceDAO;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class OverviewService {

    SessionDAO sessionDAO;
    JumpDAO jumpDAO;
    SourceDAO sourceDAO;

    public OverviewService(final DB db) {
        sessionDAO = new SessionDAO(db);
        jumpDAO = new JumpDAO(db);
        sourceDAO = new SourceDAO(db);
    }

    public DBObject getSessionDistributionByDate(String date) {
        return sessionDAO.getSessionsByDate(date);
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

    /**
     *
     * @param limit is the most number of results that returned
     * @return an array of main reference search engines as json format
     */
    public JsonArray getTopSearchEngines(int limit) {
        return sourceDAO.getTopSearchEngines(limit);
    }


    /**
     *
     * @param limit is the most number of results that returned
     * @return an array of main categories that user flow retained
     */
    public JsonArray getTopCategories(int limit) {

        // todo: finish the business logic
        return null;
    }

    /**
     *
     * @param limit is the most number of results that returned
     * @return an array of the most frequent visited pages
     */
    public JsonArray getFrequentVisitedPages(int limit) {

        // todo:
        return null;
    }

}
