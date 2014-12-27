package me.wwsun.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import me.wwsun.LevelDAO;
import me.wwsun.LinkDAO;
import me.wwsun.NodeDAO;
import me.wwsun.model.Graph;
import me.wwsun.model.GraphObject;
import me.wwsun.util.FileUtil;

import java.util.*;

/**
 * Created by Weiwei on 12/27/2014.
 */
public class LinkAnalysisService {

    LinkDAO linkDAO;
    NodeDAO nodeDAO;
    LevelDAO levelDAO;

    public LinkAnalysisService(final DB siteDatabase) {
        linkDAO = new LinkDAO(siteDatabase);
        nodeDAO = new NodeDAO(siteDatabase);
        levelDAO = new LevelDAO(siteDatabase);
    }

    public DBObject getOverviewGraph(int type, int threshhold) {
        final String HOMEPAGE = "www.made-in-china.com/";
        Graph initGraph = linkDAO.getGraphByNodeName(HOMEPAGE, 2, 100);
        Graph nextLayer = linkDAO.getNextLayerByReferNodeName(initGraph.getReqNodes(), 30);

        System.out.println("Links of next layer: " + nextLayer.getLinks().size());
        System.out.println("Nodes of next layer: " + nextLayer.getNodes().size());
        initGraph.addLayer(nextLayer);

        outputGraphNodeDetail(initGraph.getNodes(), 10); //Output the graph detail

        Graph d3Graph = transferToD3Graph(initGraph);

        List<DBObject> nodeObjectList = formNodeObjectList(initGraph.getNodes());

        //GraphObject graph = new GraphObject(nodeObjectList, initGraph.getLinks());
        GraphObject graph = new GraphObject(nodeObjectList, d3Graph.getLinks());
        System.out.println("Total Links: " + graph.getLinkList().size());
        System.out.println("Total Nodes: " + graph.getNodeList().size());
        return formOutputJSONObject(graph);
    }

    public void outputGraphNodeDetail(Set<String> nodeSet, final int LIMIT) {
        DBObject nodeDetailList = new BasicDBObject();
        for (String nodeName : nodeSet) {
            List<DBObject> refList = linkDAO.getTopReferers(nodeName, LIMIT);
            List<DBObject> reqList = linkDAO.getTopTargets(nodeName, LIMIT);

            DBObject nodeObj = nodeDAO.getNodeByName(nodeName);
            DBObject degree = getDegreeObject(nodeObj);

            DBObject refCats = getSourceCategories(refList);

            DBObject nodeDetail = new BasicDBObject();
            nodeDetail.put("degree", degree);
            nodeDetail.put("topReferrals", refList);
            nodeDetail.put("topTargets", reqList);
            nodeDetail.put("visitTrend", "".toCharArray()); //todo: use default value
            nodeDetail.put("sourceCategories", refCats);
            nodeDetailList.put(nodeName, nodeDetail);
        }
        FileUtil.outputAsJSON(nodeDetailList, "node-detail");
        System.out.println("The Node Detail file is output successfully!");
    }

    private DBObject getDegreeObject(DBObject object) {
        int in = (int) object.get("inDegree");
        int out = (int) object.get("outDegree");
        DBObject obj = new BasicDBObject();
        obj.put("in", in);
        obj.put("out", out);
        return obj;
    }

    private DBObject getSourceCategories(List<DBObject> refList) {
        List<DBObject> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (DBObject refObj : refList) {
            String ref = (String) refObj.get("referer");
            int sum = (int) refObj.get("sum");

            DBObject node = nodeDAO.getNodeByName(ref);
            int catCode = (int) node.get("category");
            String cat = levelDAO.getCategoryById(catCode);

            if(!map.containsKey(cat)) {
                map.put(cat, sum);
            } else {
                int old = map.get(cat);
                map.put(cat, sum+old);
            }
        }

        List<String> catList = new ArrayList<>();
        List<Integer> valList = new ArrayList<>();

        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Integer> kv = it.next();
            catList.add(kv.getKey());
            valList.add(kv.getValue());
        }
        DBObject catObj = new BasicDBObject();
        catObj.put("category", catList);
        catObj.put("values", valList);
        return catObj;
    }

    private Graph transferToD3Graph(Graph graph) {
        //D3 links use node index
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
}
