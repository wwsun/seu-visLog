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

    /**
     * 测试指定日期会话的搜索引擎贡献
     * 测试来源：Source表
     * 测试结果：全部通过
     */
    public void testSearchEngineRefersByDate() {
        System.out.println(overviewService.getTopSearchEnginesByDate("2015-06-30", 10));
//        System.out.println(overviewService.getTopSearchEnginesByDate("2015-07-01", 10));
//        System.out.println(overviewService.getTopSearchEnginesByDate("2015-07-02", 10));
//        System.out.println(overviewService.getTopSearchEnginesByDate("2015-07-03", 10));
//        System.out.println(overviewService.getTopSearchEnginesByDate("2015-07-04", 10));
    }

    /**
     * 测试指定日期会话流量的国家分布
     * 测试来源：country表
     * 测试结果：全部通过
     */
    public void testTopCountriesByDate() {
        System.out.println(overviewService.getTopCountriesByDate("2015-06-30", 10));
        System.out.println(overviewService.getTopCountriesByDate("2015-07-01", 10));
        System.out.println(overviewService.getTopCountriesByDate("2015-07-02", 10));
        System.out.println(overviewService.getTopCountriesByDate("2015-07-03", 10));
        System.out.println(overviewService.getTopCountriesByDate("2015-07-04", 10));
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

    /**
     * 测试指定日期会话着陆时在网站上的主要类别分布
     * 测试来源：land表
     * 测试结果：全部通过
     */
    public void testMainLandingsCategoriesByDate() {
        System.out.println(overviewService.getMainLandingCategoriesByDate("2015-06-30", 10));
        System.out.println(overviewService.getMainLandingCategoriesByDate("2015-07-01", 10));
        System.out.println(overviewService.getMainLandingCategoriesByDate("2015-07-02", 10));
        System.out.println(overviewService.getMainLandingCategoriesByDate("2015-07-03", 10));
        System.out.println(overviewService.getMainLandingCategoriesByDate("2015-07-04", 10));
    }

    /**
     * 测试指定日期会话在跳出时的在网站上的主要类别分布
     * 测试来源：leave表
     * 测试结果：全部通过
     */
    public void testMainDropOffCategoriesByDate() {
        System.out.println(overviewService.getMainDropOffCategoriesByDate("2015-06-30", 10));
        System.out.println(overviewService.getMainDropOffCategoriesByDate("2015-07-01", 10));
        System.out.println(overviewService.getMainDropOffCategoriesByDate("2015-07-02", 10));
        System.out.println(overviewService.getMainDropOffCategoriesByDate("2015-07-03", 10));
        System.out.println(overviewService.getMainDropOffCategoriesByDate("2015-07-04", 10));
    }

    /**
     * 测试指定日期用户频繁访问的页面
     * 测试来源：nodes表
     * 测试结果：全部通过
     */
    public void testHotPagesByDate() {
        System.out.println(overviewService.getFrequentVisitedPagesByDate("2015-06-30", 10));
        System.out.println(overviewService.getFrequentVisitedPagesByDate("2015-07-01", 10));
        System.out.println(overviewService.getFrequentVisitedPagesByDate("2015-07-02", 10));
        System.out.println(overviewService.getFrequentVisitedPagesByDate("2015-07-03", 10));
        System.out.println(overviewService.getFrequentVisitedPagesByDate("2015-07-04", 10));
    }

    /**
     * 测试路径生成算法
     * 测试来源：path表
     * 测试结果：成功
     */
    public void testUserFlowPath() throws ParseException, IOException {

        final String startDate = "2015-07-03"; // 起始日期
        final String endDate = "2015-07-04";  // 结束日期
        final Double pathWeight = 100.0; // 路径权重阈值，低于该阈值的路径不显示
        final int graphDepth = 6; // 图的深度

        final String formatStartDate = startDate + " 0:0:0";
        final String formatEndDate = endDate + " 0:0:0";

        long start = System.currentTimeMillis(); // start time

        SankeyGraph sankeyGraph = pathService.getGraph(graphDepth, formatStartDate, formatEndDate); // 生成用户路径图
        SankeyGraph FiltedGraph = sankeyGraph.FilterByEdgeValue(pathWeight);  // 根据边的权值过滤

        // 对数据进一步处理得到
        List<URLNode> highDropPage = sankeyGraph.topKDropPage(10);  // topK 高跳出率的页面
        List<URLNode> topKLandPage = sankeyGraph.topKLandPage(10);  // topK 着陆页

        String result = new SankeyGraphJsonObj(FiltedGraph, highDropPage, topKLandPage).toJson();  //最终结果

        long end = System.currentTimeMillis(); // end time
        System.out.println("costTime:  " + (end - start) / 1000 + "s");

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("src/main/webapp/data/"+startDate+".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);

        bw.write(result);
        bw.close();
    }

    /**
     * 测试目录定位与文件的生成
     * 测试结果：成功
     */
    public void testFileGenerated() {
        String filename = "2015-01-01";

        String result = "{xxxx}";
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter("classes/artifacts/vislog_restful/data/" + filename + ".json"));
            bw.write(result);
            bw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试日期的格式化显示
     * 测试结果：成功
     */
    public void testDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cal1 = new GregorianCalendar(2014,10,25,0,0,0);
        Calendar cal2 = new GregorianCalendar(2014,10,26,0,0,0);
        System.out.println(sdf.format(cal1.getTime()));
        System.out.println(sdf.format(cal2.getTime()));
    }
}