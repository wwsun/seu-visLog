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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SessionTest extends TestCase {

    final String mongoURI = "mongodb://localhost:27017";
    final MongoClient mongoClient;
    final DB siteDatabase;
    final OverviewService overviewService;
    final PathService pathService;

    public SessionTest() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        siteDatabase = mongoClient.getDB("jiaodian1");
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
        //����ߵ�Ȩֵ
        SankeyGraph FiltedGraph = sankeyGraph.FilterByEdgeValue(4.5);  //��ݱߵ�Ȩֵ����

        //����ݽ�һ������õ�
        List<URLNode> highDropPage = sankeyGraph.topKDropPage(10);  //topK ������ʵ�ҳ��
        List<URLNode> topKLandPage = sankeyGraph.topKLandPage(10);   //topK ��½ҳ

        String result = new SankeyGraphJsonObj(FiltedGraph, highDropPage, topKLandPage).toJson();  //���ս��

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

    public void testDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal1 = new GregorianCalendar(2014,10,25,0,0,0);
        Calendar cal2 = new GregorianCalendar(2014,10,26,0,0,0);
        System.out.println(sdf.format(cal1.getTime()));
        System.out.println(sdf.format(cal2.getTime()));
    }
}
