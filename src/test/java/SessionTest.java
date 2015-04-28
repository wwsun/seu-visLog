import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import dao.SessionDAO;
import junit.framework.TestCase;
import services.OverviewService;

import java.net.UnknownHostException;

public class SessionTest extends TestCase {


    final String mongoURI = "mongodb://223.3.80.243:27017";
    final MongoClient mongoClient;
    final DB siteDatabase;
    final OverviewService overviewService;

    public SessionTest() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        siteDatabase = mongoClient.getDB("jiaodian");
        overviewService = new OverviewService(siteDatabase);
    }

    public void testSessionTrends() {
        SessionDAO sessionDAO = new SessionDAO(siteDatabase);
        DBObject result = sessionDAO.getSessionsByDate("2014-10-22");
        System.out.println(result.toString());
    }

    public void testSessionCounts() {
        System.out.println(overviewService.getSessionCountsAndBounceRate());
    }

    public void testSearchEngineRefers() {
        System.out.println(overviewService.getTopSearchEngines(10));
    }

    public void testTopCountaries() {
        overviewService.getTopCountriesFlow(10);
    }

    public void testHotPages() {
        System.out.println(overviewService.getFrequentVisitedPages(10));
    }

    public void testHotCategories() {
        System.out.println(overviewService.getTopCategories(7));
    }

    public void testMainLandings() {
        System.out.println(overviewService.getMainLandingCategories(10));
    }
}
