package me.wwsun;

import com.mongodb.*;
import com.mongodb.util.JSON;
import me.wwsun.model.Graph;
import me.wwsun.model.GraphObject;

import java.util.*;

/**
 * Created by Weiwei on 12/24/2014.
 */
public class LinkDAO {
    private DBCollection links;

    private final String UrlPattern = "(\\w+.\\w?)+";

    public LinkDAO(final DB SiteDatabase) { links = SiteDatabase.getCollection("links"); }

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
     * @param type 0: the node is a referer node, 1: the node is a target node, 2: either referer or target
     * @param threshold
     * @return
     */
    public DBObject getLinksByNodeName(String nodeName, int type, int threshold) {

        DBCursor cursor = getDbObjects(nodeName, type, threshold);

        Set<String> nodeSet = new LinkedHashSet<>();
        List<DBObject> linkList = new ArrayList<>();

        while(cursor.hasNext()) {
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
        }
        cursor.close();
        System.out.println("Number of nodes of initial round: " + nodeSet.size());

        Graph nextLayer = getGraphNextLayer(nodeSet, 30);

        System.out.println(">>>Links:"+nextLayer.getLinks().size());
        System.out.println(">>>Nodes:"+nextLayer.getNodes().size());
        linkList.addAll(nextLayer.getLinks());
        nodeSet.addAll(nextLayer.getNodes());


        Graph fullGraph = new Graph();
        fullGraph.setLinks(linkList);
        fullGraph.setNodes(nodeSet);

        GraphObject d3Graph = TransferToGraphWithD3Format(fullGraph);


        //List<DBObject> nodeList = setNodeCategory(nodeSet);

        DBObject overviewGraph = new BasicDBObject();
        //overviewGraph.put("nodes", nodeList);
        //overviewGraph.put("links", linkList);

        overviewGraph.put("nodes", d3Graph.getNodeList());
        overviewGraph.put("links", d3Graph.getLinkList());

        System.out.println(JSON.serialize(overviewGraph));
        System.out.println("The number of effective links is: "+linkList.size());

        return overviewGraph;
    }



    /**
     *
     * @param nodeSet
     * @param threshold
     */
    private Graph getGraphNextLayer(Set<String> nodeSet, int threshold) {
        Graph graph = new Graph();
        Set<String> nodeSet2 = new LinkedHashSet<>();
        List<DBObject> linkList2 = new ArrayList<>();

        for (String nodeName : nodeSet) {

            System.out.println("Processing NODE <" +nodeName+">");

//            QueryBuilder builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", UrlPattern))
//                    .and("referer").is(nodeName)
//                    .and("request").is(new BasicDBObject("$regex", UrlPattern))
//                    .and("sum").is(new BasicDBObject("$gt", threshold));
//
//            DBCursor cursor = links.find(builder.get(), new BasicDBObject("_id", false));
            DBCursor cursor = getDbObjects(nodeName, 0, threshold);

            System.out.println("NODE <" +nodeName+"> have "+cursor.size() +" effective links");

            int counter = 0;
            while(cursor.hasNext()) {
                DBObject obj = cursor.next();

                String ref = (String) obj.get("referer");
                String req = (String) obj.get("request");

                //nodeSet2.add(ref);
                nodeSet2.add(req);

                DBObject linkObj = new BasicDBObject();
                linkObj.put("source", ref);
                linkObj.put("target", req);
                linkObj.put("weight", obj.get("sum"));
                linkObj.put("name", "in-site");

                linkList2.add(linkObj);
                counter++;
            }
        }

        graph.setLinks(linkList2);
        graph.setNodes(nodeSet2);
        return graph;
    }

    /**
     *
     * @param nodeName
     * @param type 0: the node is a referer node, 1: the node is a target node, 2: either referer or target
     * @param threshold
     * @return
     */
    private DBCursor getDbObjects(String nodeName, int type, int threshold) {
        QueryBuilder builder = null;
        switch (type) {
            case 0:
                builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", UrlPattern))
                        .and("referer").is(nodeName)
                        .and("request").is(new BasicDBObject("$regex", UrlPattern))
                        .and("sum").is(new BasicDBObject("$gt", threshold));

                break;
            case 1:
                builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", UrlPattern))
                        .and("request").is(new BasicDBObject("$regex", UrlPattern))
                        .and("request").is(nodeName)
                        .and("sum").is(new BasicDBObject("$gt", threshold));

                break;

            case 2:
                builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", UrlPattern))
                        .and("request").is(new BasicDBObject("$regex", UrlPattern))
                        .or(QueryBuilder.start("referer").is(nodeName).get(),
                                QueryBuilder.start("request").is(nodeName).get())
                        .and("sum").is(new BasicDBObject("$gt", threshold));
                break;
            default:
                System.out.println("Only 0, 1, 2 are supported by type");
        }

        return links.find(builder.get(), new BasicDBObject("_id", false));
    }

    private List<DBObject> setNodeCategory(Set<String> nodeSet) {
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
        return nodeList;
    }

    private GraphObject TransferToGraphWithD3Format(Graph graph)  {
        GraphObject d3Graph = null;

        List<String> nodeList = new ArrayList<>();
        List<DBObject> linkListUsingIndex = new ArrayList<>();

        List<DBObject> outputNodeList = setNodeCategory(graph.getNodes());

        nodeList.addAll(graph.getNodes());

        for (DBObject obj : graph.getLinks()) {
            String ref = (String) obj.get("referer");
            String req = (String) obj.get("request");
            int refIndex = nodeList.indexOf(ref);
            int reqIndex = nodeList.indexOf(req);

            DBObject newObj = new BasicDBObject("source", refIndex)
                    .append("target", reqIndex).append("value", obj.get("sum"));
            linkListUsingIndex.add(newObj);
        }

        d3Graph.setNodeList(outputNodeList);
        d3Graph.setLinkList(linkListUsingIndex);

        return d3Graph;
    }
}
