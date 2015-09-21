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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Path("/sessions")
public class SessionController {

    //final String mongoURI = "mongodb://223.3.80.243:27017";
    final String mongoURI="mongodb://localhost:27017";
    final MongoClient mongoClient;
    final DB siteDatabase;
    final OverviewService overviewService;
    final PathService pathService;

    public SessionController() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        siteDatabase = mongoClient.getDB("jiaodian1");
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

    @Path("/distribution/{start}/{end}") //2014-10-22
    @GET
    @Produces("application/json")
    public JsonObject getDistributionByRange(@PathParam("start") String start,@PathParam("end")String end) throws  ParseException{
        String jsonString = overviewService.getSessionDistributionByDate(start,end).toString();
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

    @Path("/overview/sources/countries/{start}/{end}")
    @GET
    @Produces("application/json")
    public JsonArray getTopCountriesFlowContributionByRange(@PathParam("start")String start,@PathParam("end")String end) throws Exception {
        return overviewService.getTopCountriesFlow(start, end);
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

    @Path("/path/{date}") //2014-10-22
    @GET
    @Produces("application/json")
    public JsonObject getSessionPath(@PathParam("date") String date) throws ParseException, UnknownHostException {

        // todo: the computation efficiency is too bad!

        String[] arr = date.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar startCal = new GregorianCalendar(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]),
                Integer.parseInt(arr[2]),0,0,0);
        Calendar endCal = new GregorianCalendar(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]),
                Integer.parseInt(arr[2])+1, 0,0,0);  // analysis one day by default

        //�������ڲ����depth����(·�����)
        SankeyGraph sankeyGraph = pathService.getGraph(7, startCal.getTime().toString(), endCal.getTime().toString());

        //����ߵ�Ȩֵ
        SankeyGraph FiltedGraph = sankeyGraph.FilterByEdgeValue(4.5);  //   ��ݱߵ�Ȩֵ����

        //����ݽ�һ������õ�
        List<URLNode> highDropPage = sankeyGraph.topKDropPage(10);  //  topK ������ʵ�ҳ��
        List<URLNode> topKLandPage = sankeyGraph.topKLandPage(10);   // topK ��½ҳ

        String result = new SankeyGraphJsonObj(FiltedGraph, highDropPage, topKLandPage).toJson();  //���ս��

        // format transfer
        JsonReader reader = Json.createReader(new StringReader(result));
        JsonObject json = reader.readObject();
        reader.close();

        return json;
    }
}
