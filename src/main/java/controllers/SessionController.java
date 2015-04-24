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

    public SessionController() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        siteDatabase = mongoClient.getDB("sample");
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
        OverviewService overviewService = new OverviewService(siteDatabase);
        String jsonString = overviewService.getSessionDistributionByDate(date).toString();
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonObject json = reader.readObject();
        reader.close();
        return json;
    }

    public JsonObject getSessionStatus() {

        return null;
    }
}
