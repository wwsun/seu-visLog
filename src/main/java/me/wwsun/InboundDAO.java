package me.wwsun;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class InboundDAO {
    //private DBCollection nodes;
    private DBCollection links;

    public InboundDAO(final DB siteDatabase) {
        links = siteDatabase.getCollection("links");
       // nodes = siteDatabase.getCollection("nodes");
    }

    public String getInboundDataAsJSON() {
        QueryBuilder builder = QueryBuilder.start("type").is("0");
        DBCursor cursor = links.find(builder.get(), new BasicDBObject("referer", true)
                .append("request", true).append("_id", false));

        List<DBObject> linkList = new LinkedList<>();
        List<String> nodeList = new ArrayList<>();

        Set<String> nodeSet = new LinkedHashSet<>();

        DBObject linkObjectList = new BasicDBObject();

        while (cursor.hasNext()) {

            DBObject obj = cursor.next();
            String ref = (String) obj.get("referer");
            String req = (String) obj.get("request");

            nodeSet.add(ref);
            nodeSet.add(req);

            linkList.add(obj);
        }

        nodeList.addAll(nodeSet);
        List<DBObject> newLinkList = new ArrayList<>();
        List<DBObject> newNodeList = new ArrayList<>();

        for (String item : nodeList) {
            DBObject object = new BasicDBObject("name", item);
            if (item.contains("made-in-china")) {
                object.put("group", 1);
            } else {
                object.put("group", 0);
            }
            newNodeList.add(object);
        }

        for (DBObject obj : linkList) {
            String ref = (String) obj.get("referer");
            String req = (String) obj.get("request");
            int refIndex = nodeList.indexOf(ref);
            int reqIndex = nodeList.indexOf(req);
            DBObject newObj = new BasicDBObject("source", refIndex)
                    .append("target", reqIndex).append("value", 1);//value is default
            newLinkList.add(newObj);
        }

        System.out.println("==========");
        linkObjectList.put("nodes", newNodeList);
        linkObjectList.put("links", newLinkList);

        //output to file
        Path outsite = null;
        try {
            outsite = Paths.get("./target/classes/public/data/outsite.json").toRealPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outsite,
                StandardCharsets.UTF_8)) {
            writer.write(JSON.serialize(linkObjectList));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(JSON.serialize(linkObjectList));
        cursor.close();

        return JSON.serialize(linkObjectList);
    }

}
