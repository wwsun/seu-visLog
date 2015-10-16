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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 测试时间：2015-10-15
 * 本次测试用例的数据范围：2015-06-30, 07-01, 07-02, 07-03, 07-04
 */
public class SessionTest extends TestCase {

    final String mongoURI = "mongodb://localhost:27017";
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

    /**
     * 测试指定日期会话的关键指标（当日会话数、当日首次访问跳出率、当日询盘率）
     * 测试涞源：Jump表
     * 测试结果：全部通过
     */
    public void testKeyIndexByDate() {
        System.out.println(overviewService.getSessionCountsAndBounceRateByDate("2015-06-30"));
//        System.out.println(overviewService.getSessionCountsAndBounceRateByDate("2015-07-01"));
//        System.out.println(overviewService.getSessionCountsAndBounceRateByDate("2015-07-02"));
//        System.out.println(overviewService.getSessionCountsAndBounceRateByDate("2015-07-03"));
//        System.out.println(overviewService.getSessionCountsAndBounceRateByDate("2015-07-04"));
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

    /**
     * 测试指定日期会话在不同时段的分布情况(24h)
     * 数据来源：Nodes表
     * 测试结果：全部通过
     */
    public void testSessionDistributionByDate() {
        String jsonString = overviewService.getSessionDistributionByDate("2015-07-03").toString();
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject json = reader.readObject();
        reader.close();
        System.out.println(json);
    }

    /**
     * 测试指定日期会话在类别上的主要分布情况
     * 数据来源：Session表
     * 测试结果：全部通过
     */
    public void testHotCategoriesByDate() {
//        System.out.println(overviewService.getTopCategoriesByDate("2015-06-30", 10));
//        System.out.println(overviewService.getTopCategoriesByDate("2015-07-01", 10));
//        System.out.println(overviewService.getTopCategoriesByDate("2015-07-02", 10));
//        System.out.println(overviewService.getTopCategoriesByDate("2015-07-03", 10));
        System.out.println(overviewService.getTopCategoriesByDate("2015-07-04", 10));
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

    public void testDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal1 = new GregorianCalendar(2014,10,25,0,0,0);
        Calendar cal2 = new GregorianCalendar(2014,10,26,0,0,0);
        System.out.println(sdf.format(cal1.getTime()));
        System.out.println(sdf.format(cal2.getTime()));
    }
}