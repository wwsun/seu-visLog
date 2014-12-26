package me.wwsun.model;

import com.mongodb.DBObject;

import java.util.List;
import java.util.Set;

/**
 * Created by Weiwei on 12/25/2014.
 */
public class Graph {

    private Set<String> nodes;
    private Set<String> reqNodes;
    private List<DBObject> links;

    public Graph() {}

    public Graph(Set<String> nodes, Set<String> reqNodes, List<DBObject> links) {
        this.nodes = nodes;
        this.reqNodes = reqNodes;
        this.links = links;
    }

    public Set<String> getReqNodes() {
        return reqNodes;
    }

    public void setReqNodes(Set<String> reqNodes) {
        this.reqNodes = reqNodes;
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    public List<DBObject> getLinks() {
        return links;
    }

    public void setLinks(List<DBObject> links) {
        this.links = links;
    }

    public void addLayer(Graph nextLayer) {
        this.nodes.addAll(nextLayer.getNodes());
        this.links.addAll(nextLayer.getLinks());
        this.reqNodes = nextLayer.reqNodes;
    }
}
