package me.wwsun.model;

import com.mongodb.DBObject;

import java.util.List;
import java.util.Set;

/**
 * Created by Weiwei on 12/25/2014.
 */
public class Graph {

    private Set<String> nodes;
    private List<DBObject> links;

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
}
