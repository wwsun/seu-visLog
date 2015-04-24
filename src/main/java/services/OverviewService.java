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
     * @return {"total":"COUNT", "bounceRate":"PERCENT", "inquiryRate":"PERCENT"}
     */
    public JsonObject getSessionCountsAndBounceRate() {
        return Json.createObjectBuilder()
                .add("total", jumpDAO.getSessionCounts())
                .add("bounce_rate", jumpDAO.getBounceRate())
                .add("inquiry_rate", jumpDAO.getInquiryRate())
                .build();
    }

    public JsonArray getTopSearchEngines(int limit) {
        return sourceDAO.getTopSearchEngines(limit);
    }

}
