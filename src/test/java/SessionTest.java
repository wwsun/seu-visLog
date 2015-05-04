import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import dao.SessionDAO;
import entity.SankeyGraph;
import entity.SankeyGraphJsonObj;
import entity.URLNode;
import junit.framework.TestCase;
import services.OverviewService;
import services.PathService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

public class SessionTest extends TestCase {


    final String mongoURI = "mongodb://223.3.80.243:27017";
    final MongoClient mongoClient;
    final DB siteDatabase;
    final OverviewService overviewService;
    final PathService pathService;

    public SessionTest() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        siteDatabase = mongoClient.getDB("jiaodian");
        overviewService = new OverviewService(siteDatabase);
        pathService = new PathService(siteDatabase);
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

    public void testMainDropOff() {
        System.out.println(overviewService.getMainDropOffCategories(10));
    }

    public void testSankeyGraph() throws ParseException, IOException {

        long start = System.currentTimeMillis();
        SankeyGraph sankeyGraph = pathService.getGraph(7, "2014-10-22 0:0:0", "2014-10-23 0:0:0");
        //传入边的权值
        SankeyGraph FiltedGraph = sankeyGraph.FilterByEdgeValue(4.5);  //根据边的权值过滤

        //对数据进一步处理得到
        List<URLNode> highDropPage = sankeyGraph.topKDropPage(10);  //topK 高跳出率的页面
        List<URLNode> topKLandPage = sankeyGraph.topKLandPage(10);   //topK 着陆页

        String result = new SankeyGraphJsonObj(FiltedGraph, highDropPage, topKLandPage).toJson();  //最终结果

        long end = System.currentTimeMillis();
        System.out.println("costTime:  " + (end - start) / 1000 + "s");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("graph1.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);

        bw.write(result);
        bw.close();

    }
}
