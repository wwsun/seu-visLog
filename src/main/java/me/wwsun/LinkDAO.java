package me.wwsun;

import com.mongodb.*;
import me.wwsun.model.Graph;
import me.wwsun.util.FileUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Weiwei on 12/24/2014.
 */
public class LinkDAO {
    private DBCollection links;

    private final String urlPattern = "(\\w+.\\w?)+";
    //private final String HOMEPAGE = "www.made-in-china.com/";

    public LinkDAO(final DB siteDatabase) { links = siteDatabase.getCollection("links"); }

    public List<DBObject> getTopReferers(String nodeName, final int LIMIT) {
        QueryBuilder builder = QueryBuilder.start("referer").is(new BasicDBObject("$regex", urlPattern))
                .and("request").is(nodeName);
        DBCursor cursor = links.find(builder.get(), new BasicDBObject("_id", false).append("request", false))
                .sort(new BasicDBObject("sum", -1)).limit(LIMIT);

        List<DBObject> list = new ArrayList<>();
        while(cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }

    public List<DBObject> getTopTargets(String nodeName, final int LIMIT) {
        QueryBuilder builder = QueryBuilder.start("referer").is(nodeName)
                .and("request").is(new BasicDBObject("$regex", urlPattern));
        DBCursor cursor = links.find(builder.get(), new BasicDBObject("_id", false).append("referer", false))
                .sort(new BasicDBObject("sum", -1)).limit(LIMIT);

        List<DBObject> list = new ArrayList<>();
        while(cursor.hasNext()) {
            list.add(cursor.next());
        }
        cursor.close();
        return list;
    }

    /**
     *
     * @param nodeName central node name that you want to form the graph
     * @param type 0, the node is a referer; 1, the node is a request; 2, either referer or request
     * @param threshold the filter condition
     * @return a graph
     */
    public Graph getGraphByNodeName(String nodeName, int type, int threshold) {
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
        Graph nextLayerOfCurrentNode;
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
