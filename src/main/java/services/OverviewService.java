package services;

import com.mongodb.DB;
import com.mongodb.DBObject;
import dao.JumpDAO;
import dao.SessionDAO;

import javax.json.Json;
import javax.json.JsonObject;

public class OverviewService {

    SessionDAO sessionDAO;
    JumpDAO jumpDAO;

    public OverviewService(final DB db) {
        sessionDAO = new SessionDAO(db);
        jumpDAO = new JumpDAO(db);
    }

    public DBObject getSessionDistributionByDate(String date) {
        return sessionDAO.getSessionsByDate(date);
    }

    /**
     *
     * @return {"total":"COUNT", "onehop":"PERCENT"}
     */
    public JsonObject getSessionCountsAndBounceRate() {
        return Json.createObjectBuilder()
                .add("total", jumpDAO.getSessionCounts())
                .add("onehop", jumpDAO.getBounceRate())
                .build();

    }

}
