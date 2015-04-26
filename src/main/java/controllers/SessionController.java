package controllers;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import services.OverviewService;

import javax.json.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.StringReader;
import java.net.UnknownHostException;

@Path("/sessions")
public class SessionController {

    final String mongoURI = "mongodb://223.3.80.243:27017";
    final MongoClient mongoClient;
    final DB siteDatabase;
    final OverviewService overviewService;

    public SessionController() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        siteDatabase = mongoClient.getDB("jiaodian");
        overviewService = new OverviewService(siteDatabase);
    }

    @Path("/all")
    @GET
    @Produces("application/json")
    public JsonArray getAll() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        // for-loop
        builder.add(Json.createObjectBuilder().add("name", "weiwei"));
        return builder.build();
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
    public JsonArray getSearchEngineContribution() { return overviewService.getTopSearchEngines(10); }

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
        // todo get frequent visited pages
        return null;
    }

    @Path("/overview/frequent/categories")
    public JsonArray getFrequentVisitedCategory() {
        // todo get frequent visited categories
        return null;
    }
}
