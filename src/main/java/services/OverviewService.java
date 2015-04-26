package services;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.sun.javafx.scene.layout.region.BackgroundImage;
import dao.CountryDAO;
import dao.JumpDAO;
import dao.SessionDAO;
import dao.SourceDAO;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.List;

public class OverviewService {

    SessionDAO sessionDAO;
    JumpDAO jumpDAO;
    SourceDAO sourceDAO;
    CountryDAO countryDAO;

    public OverviewService(final DB db) {
        sessionDAO = new SessionDAO(db);
        jumpDAO = new JumpDAO(db);
        sourceDAO = new SourceDAO(db);
        countryDAO = new CountryDAO(db);
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
                            .add("name",(String)object.get("name"))
                            .add("dup", (int)object.get("value")));
        }
        return builder.build();
    }

}
