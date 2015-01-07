package me.wwsun.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import me.wwsun.JumpDAO;
import me.wwsun.KeywordDAO;
import me.wwsun.SessionDAO;
import me.wwsun.SourceDAO;

import java.util.List;

/**
 * Created by Weiwei on 1/7/2015.
 */
public class OverviewService {

    JumpDAO jumpDAO;
    SessionDAO sessionDAO;
    KeywordDAO keywordDAO;
    SourceDAO sourceDAO;

    public OverviewService(final DB siteDatabase) {
        jumpDAO = new JumpDAO(siteDatabase);
        sessionDAO = new SessionDAO(siteDatabase);
        keywordDAO = new KeywordDAO(siteDatabase);
        sourceDAO = new SourceDAO(siteDatabase);
    }

    public DBObject getOverviewData(String date) {
        DBObject overview = new BasicDBObject();

        DBObject sessionTrends = sessionDAO.getSessionsByDate(date);
        Integer totalSessions = jumpDAO.getTotalSessions();
        double bounceRate = jumpDAO.getBounceRate();
        List topSearchEngines = sourceDAO.getTopSearchEngines();
        List keywords = keywordDAO.getKeywordList();

        overview.put("totalSessions", totalSessions);
        overview.put("totalBounceRate", bounceRate);
        overview.put("sessionTrends", sessionTrends);
        overview.put("topReferral", "".toCharArray());
        overview.put("topSearchEngine", topSearchEngines);
        overview.put("topKeywords", keywords.toArray());

        return overview;
    }
}
