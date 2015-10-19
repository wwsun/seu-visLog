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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Path("/sessions")
public class SessionController {

    final String mongoURI = "mongodb://localhost:27017";
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

    @Path("/distribution/trend/{date}")
    @GET
    @Produces("application/json")
    public JsonObject getDistribution(@PathParam("date") String date) {
        String jsonString = overviewService.getSessionDistributionByDate(date).toString();
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject json = reader.readObject();
        reader.close();
        return json;
    }

    @Path("/distribution/category/{date}")
    @GET
    @Produces("application/json")
    public JsonArray getCategoriesDistributionByDate(@PathParam("date") String date) {
        return overviewService.getTopCategoriesByDate(date, 7);
    }

    @Path("/distribution/index/{date}")
    @GET
    @Produces("application/json")
    public JsonObject getSessionStatus(@PathParam("date") String date) {
        return overviewService.getSessionCountsAndBounceRateByDate(date);
    }

    @Path("/distribution/sources/se/{date}")
    @GET
    @Produces("application/json")
    public JsonArray getSearchEngineContribution(@PathParam("date") String date) {
        return overviewService.getTopSearchEnginesByDate(date, 10);
    }

    @Path("/distribution/sources/countries/{date}")
    @GET
    @Produces("application/json")
    public JsonArray getTopCountriesFlowContribution(@PathParam("date") String date) {
        return overviewService.getTopCountriesByDate(date, 10);
    }

    @Path("/distribution/landings/categories/{date}")
    @GET
    @Produces("application/json")
    public JsonArray getMainLandingCategories(@PathParam("date") String date) {
        return overviewService.getMainLandingCategoriesByDate(date, 10);
    }

    @Path("/distribution/dropoff/categories/{date}")
    @GET
    @Produces("application/json")
    public JsonArray getMainDropOffCategories(@PathParam("date") String date) {
        return overviewService.getMainDropOffCategoriesByDate(date, 10);
    }

    @Path("/distribution/frequent/pages/{date}")
    @GET
    @Produces("application/json")
    public JsonArray getFrequentVisitedPages(@PathParam("date") String date) {
        return overviewService.getFrequentVisitedPagesByDate(date, 20);
    }

    @Path("/path/flow")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    public JsonObject getSessionPath(@QueryParam("startDate") String startDate, // 起始日期
                                     @QueryParam("endDate") String endDate, // 结束日期
                                     @QueryParam("graphDepth") int graphDepth, // 图的深度
                                     @QueryParam("pathWeight") double pathWeight // 边权重过滤
    ){
        // todo: get session path

//        final String formatStartDate = startDate + " 0:0:0";
//        final String formatEndDate = endDate + " 0:0:0";
//
//        SankeyGraph sankeyGraph = pathService.getGraph(graphDepth, formatStartDate, formatEndDate); // 生成用户路径图
//        SankeyGraph FiltedGraph = sankeyGraph.FilterByEdgeValue(pathWeight);  // 根据边的权值过滤
//
//        // 对数据进一步处理得到
//        List<URLNode> highDropPage = sankeyGraph.topKDropPage(10);  // topK 高跳出率的页面
//        List<URLNode> topKLandPage = sankeyGraph.topKLandPage(10);   // topK 着陆页
//
//        String result = new SankeyGraphJsonObj(FiltedGraph, highDropPage, topKLandPage).toJson();  // 最终结果
//
////         change to JSON format
//        JsonReader reader = Json.createReader(new StringReader(result));
//        JsonObject responseJson = reader.readObject();
//
//        reader.close();
//
//        boolean isGraphGenerated = false; // 文件是否生成
////        String result = "{xxx}";
////
////        BufferedWriter bw;
////        try {
//////            bw = new BufferedWriter(new FileWriter("classes/artifacts/vislog_restful/data/" + startDate + ".json"));
////            bw = new BufferedWriter(new FileWriter("graph2.json"));
////            bw.write(result);
////            bw.close();
////            isGraphGenerated = true;
////        } catch (IOException e) {
//////            isGraphGenerated = false;
////            e.printStackTrace();
////        }
//
//        JsonObject responseJson; // 返回结果
//
//        if (isGraphGenerated) {
//            responseJson = Json.createObjectBuilder()
//                    .add("startDate", startDate)
//                    .add("endDate", endDate)
//                    .add("graphDepth", graphDepth)
//                    .add("pathWeight", pathWeight)
//                    .add("file", startDate + ".json")
//                    .add("result", "success")
//                    .build();
//        } else {
//            responseJson = Json.createObjectBuilder()
//                    .add("result", "failed")
//                    .build();
//        }
//
//        return responseJson;
        return null;
    }
}
