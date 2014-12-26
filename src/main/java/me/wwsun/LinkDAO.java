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

    private final String urlPattern = "(\\w+.\\w?)+";
    private final String HOMEPAGE = "www.made-in-china.com/";

    public LinkDAO(final DB SiteDatabase) { links = SiteDatabase.getCollection("links"); }

    public DBObject getOverviewGraph(int type, int threshold) {
        Graph initGraph = getGraphByNodeName(HOMEPAGE, 2, 100);

        Graph nextLayer = getNextLayerByReferNodeName(initGraph.getReqNodes(), 30);
        System.out.println("Links of next layer: " + nextLayer.getLinks().size());
        System.out.println("Nodes of next layer: " + nextLayer.getNodes().size());
        initGraph.addLayer(nextLayer);

        Graph d3Graph = transferToD3Graph(initGraph);

        List<DBObject> nodeObjectList = formNodeObjectList(initGraph.getNodes());


        //GraphObject graph = new GraphObject(nodeObjectList, initGraph.getLinks());
        GraphObject graph = new GraphObject(nodeObjectList, d3Graph.getLinks());
        System.out.println("Total Links: " + graph.getLinkList().size());
        System.out.println("Total Nodes: " + graph.getNodeList().size());
        return formOutputJSONObject(graph);
    }


    /**
     *
     * @param nodeName central node name that you want to form the graph
     * @param type 0, the node is a referer; 1, the node is a request; 2, either referer or request
     * @param threshold the filter condition
     * @return a graph
     */
    private Graph getGraphByNodeName(String nodeName, int type, int threshold) {
        DBCursor cursor = getDbObjects(nodeName, type, threshold);

        //1. get nodeSet, linkList of initial graph
        Set<String> nodeSet = new LinkedHashSet<>();
        Set<String> requestNodeSet = new LinkedHashSet<>();

        List<DBObject> linkList = new ArrayList<>();

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            String ref = (String) obj.get("referer");
            String req = (String) obj.get("request");
            int sum = (int) obj.get("sum");

            nodeSet.add(ref);
            nodeSet.add(req);
            requestNodeSet.add(req);

            DBObject linkObject = formLinkObject(ref, req, sum);
            linkList.add(linkObject);
        }
        return new Graph(nodeSet, requestNodeSet, linkList);
    }

    public Graph getNextLayerByReferNodeName(Set<String> reqNodeSet, int threshold) {
        Graph nextLayerOfCurrentNode = new Graph();
        Set<String> nextLayerNodes = new LinkedHashSet<>();
        Set<String> nextLayerReqNodes = new LinkedHashSet<>();
        List<DBObject> nextLayerLinks = new ArrayList<>();

        for (String refNodeName : reqNodeSet) {
            System.out.println("Processing referer Node <"+refNodeName+">");
            nextLayerOfCurrentNode = getGraphByNodeName(refNodeName, 0, threshold);
            nextLayerLinks.addAll(nextLayerOfCurrentNode.getLinks());
            nextLayerNodes.addAll(nextLayerOfCurrentNode.getNodes());
            nextLayerReqNodes.addAll(nextLayerOfCurrentNode.getReqNodes());
        }
        return new Graph(nextLayerNodes, nextLayerReqNodes, nextLayerLinks);
    }

    private DBObject formLinkObject(String ref, String req, int sum) {
        DBObject link = new BasicDBObject();
        link.put("source", ref);
        link.put("target", req);
        link.put("weight", sum);

        if(ref.toLowerCase().contains("made-in-china"))
            link.put("name", "in-site");
        else
            link.put("name", "out-site");

        return link;
    }

    private List<DBObject> formNodeObjectList(Set<String> nodeSet) {
        List<DBObject> nodeList = new ArrayList<>();
        for (String str : nodeSet) {
            DBObject object = new BasicDBObject();

            if(str.contains("made-in-china"))
                object.put("category", 0); //0 - inner site
            else
                object.put("category", 1); //1 - out site

            object.put("name", str);
            object.put("value", 10); //default value
            nodeList.add(object);
        }
        return nodeList;
    }

    private DBObject formOutputJSONObject(GraphObject graph){
        DBObject outputGraph = new BasicDBObject();
        outputGraph.put("nodes", graph.getNodeList());
        outputGraph.put("links", graph.getLinkList());
        return outputGraph;
    }


    private Graph transferToD3Graph(Graph graph) {
        //Todo: D3 links use node index
        List<String> nodeList = new ArrayList<>();
        nodeList.addAll(graph.getNodes());

        List<DBObject> linkList = new ArrayList<>();
        for (DBObject obj : graph.getLinks()) {
            String ref = (String) obj.get("source");
            String req = (String) obj.get("target");
            int sum = (int) obj.get("weight");
            String type = (String) obj.get("name");

            int refIndex = nodeList.indexOf(ref);
            int reqIndex = nodeList.indexOf(req);

            DBObject link = new BasicDBObject();
            link.put("source", refIndex);
            link.put("target", reqIndex);
            link.put("weight", sum);
            link.put("name", type);
            linkList.add(link);
        }

        graph.setLinks(linkList);
        return graph;
    }

    private DBCursor getDbObjects(String nodeName, int type, int threshold) {
        QueryBuilder builder = null;
        switch (type) {
            case 0:
                builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", urlPattern))
                        .and("referer").is(nodeName)
                        .and("request").is(new BasicDBObject("$regex", urlPattern))
                        .and("sum").is(new BasicDBObject("$gt", threshold));

                break;
            case 1:
                builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", urlPattern))
                        .and("request").is(new BasicDBObject("$regex", urlPattern))
                        .and("request").is(nodeName)
                        .and("sum").is(new BasicDBObject("$gt", threshold));

                break;

            case 2:
                builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", urlPattern))
                        .and("request").is(new BasicDBObject("$regex", urlPattern))
                        .or(QueryBuilder.start("referer").is(nodeName).get(),
                                QueryBuilder.start("request").is(nodeName).get())
                        .and("sum").is(new BasicDBObject("$gt", threshold));
                break;
            default:
                System.out.println("Only 0, 1, 2 are supported by type");
        }

        return links.find(builder.get(), new BasicDBObject("_id", false));
    }
}
