package me.wwsun;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Weiwei on 12/24/2014.
 */
public class LinkDAO {
    private DBCollection links;

    private final String UrlPattern = "(\\w+.\\w?)+";

    public LinkDAO(final DB SiteDatabase) {
        links = SiteDatabase.getCollection("links");
    }

    public DBObject getEffectiveLinks() {
        QueryBuilder builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", UrlPattern))
                .and("request").is(new BasicDBObject("$regex", UrlPattern)).and("sum").is(new BasicDBObject("$gt", 50));
        DBCursor cursor = links.find(builder.get(), new BasicDBObject("_id", false));

        int counter = 0;

        Set<String> nodeSet = new LinkedHashSet<>();
        List<DBObject> linkList = new ArrayList<>();

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            String ref = (String) obj.get("referer");
            String req = (String) obj.get("request");

            nodeSet.add(ref);
            nodeSet.add(req);

            DBObject linkObj = new BasicDBObject();
            linkObj.put("source", ref);
            linkObj.put("target", req);
            linkObj.put("weight", obj.get("sum"));

            if(ref.toLowerCase().contains("made-in-china"))
                linkObj.put("name", "in-site");
            else
                linkObj.put("name", "out-site");

            linkList.add(linkObj);

            counter++;
        }
        cursor.close();

        System.out.println(nodeSet.size());

        List<DBObject> nodeList = new ArrayList<>();
        for (String str : nodeSet) {
            DBObject object = new BasicDBObject();

            /**
             * category:
             *  0 - inner site
             *  1 - out site
             */
            if(str.contains("made-in-china"))
                object.put("category", 0);
            else
                object.put("category", 1);

            object.put("name", str);
            object.put("value", 10); //default value
            nodeList.add(object);
        }

        DBObject overviewGraph = new BasicDBObject();
        overviewGraph.put("nodes", nodeList);
        overviewGraph.put("links", linkList);

        System.out.println(JSON.serialize(overviewGraph));
        System.out.println("The number of effective links is: "+counter);

        return overviewGraph;
    }

    /**
     *
     * @param nodeName
     * @param tag 0: the node is a referer node, 1: the node is a target node, 2: either referer or target
     * @return
     */
    public DBObject getLinksByNodeName(String nodeName, int tag) {



        return null;
    }
}
