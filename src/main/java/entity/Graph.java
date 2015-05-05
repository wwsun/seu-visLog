package entity;

import java.util.List;

/**
 * Created by Administrator on 2015/4/16.
 */
public abstract class Graph {
    List<? extends Node> nodes;
    List<? extends Edge> links;

    public Graph(){}

    public Graph(List<? extends Node> nodes, List<? extends Edge> links){
        this.nodes= nodes;
        this.links= links;
    }


    public List<? extends Node> getNodes() {
        return nodes;
    }

    public List<? extends Edge> getLinks() {
        return links;
    }
}
