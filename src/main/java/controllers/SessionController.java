package controllers;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import entity.SankeyGraph;
import entity.SankeyGraphJsonObj;
import entity.URLNode;
import services.OverviewService;
import services.PathService;

import javax.json.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

@Path("/sessions")
public class SessionController {

    final String mongoURI = "mongodb://223.3.80.243:27017";
    final MongoClient mongoClient;
    final DB siteDatabase;
    final OverviewService overviewService;
    final PathService pathService;

    public SessionController() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        siteDatabase = mongoClient.getDB("jiaodian");
        overviewService = new OverviewService(siteDatabase);
        pathService = new PathService(siteDatabase);
    }

    @Path("/distribution/{date}") //2014-10-22
    @GET
    @Produces("application/json")
    public JsonObject getDistribution(@PathParam("date") String date) {
        String jsonString = overviewService.getSessionDistributionByDate(date).toString();
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject json = reader.readObject();
        reader.close();
        return json;
    }

    @Path("/overview/status")
    @GET
    @Produces("application/json")
    public JsonObject getSessionStatus() {
        return overviewService.getSessionCountsAndBounceRate();
    }

    @Path("/overview/sources/se")
    @GET
    @Produces("application/json")
    public JsonArray getSearchEngineContribution() {
        return overviewService.getTopSearchEngines(10);
    }

    @Path("/overview/sources/countries")
    @GET
    @Produces("application/json")
    public JsonArray getTopCountriesFlowContribution() {
        return overviewService.getTopCountriesFlow(10);
    }

    @Path("/overview/frequent/pages")
    @GET
    @Produces("application/json")
    public JsonArray getFrequentVisitedPages() {
        return overviewService.getFrequentVisitedPages(20);
    }

    @Path("/overview/frequent/categories")
    @GET
    @Produces("application/json")
    public JsonArray getFrequentVisitedCategory() {
        return overviewService.getTopCategories(7);
    }

    @Path("/overview/landings/categories")
    @GET
    @Produces("application/json")
    public JsonArray getMainLandingCategories() {
        return overviewService.getMainLandingCategories(10);
    }

    @Path("/overview/dropoff/categories")
    @GET
    @Produces("application/json")
    public JsonArray getMainDropOffCategories() {
        return overviewService.getMainDropOffCategories(10);
    }

    @Path("/path")
    @GET
    @Produces("application/json")
    public JsonObject getSessionPath() throws ParseException, UnknownHostException {

        //传入日期参数和depth参数(路径深度)
        SankeyGraph sankeyGraph = pathService.getGraph(7, "2014-10-22 0:0:0", "2014-10-23 0:0:0");

        //传入边的权值
        SankeyGraph FiltedGraph = sankeyGraph.FilterByEdgeValue(4.5);  //   根据边的权值过滤

        //对数据进一步处理得到
        List<URLNode> highDropPage = sankeyGraph.topKDropPage(10);  //  topK 高跳出率的页面
        List<URLNode> topKLandPage = sankeyGraph.topKLandPage(10);   // topK 着陆页

        String result = new SankeyGraphJsonObj(FiltedGraph, highDropPage, topKLandPage).toJson();  //最终结果

        // format transfer
        JsonReader reader = Json.createReader(new StringReader(result));
        JsonObject json = reader.readObject();
        reader.close();

        return json;
    }
}
