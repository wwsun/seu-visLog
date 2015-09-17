package entity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/16.
 */
public class SankeyGraphJsonObj {
    List<URLNode> nodes;
    List<StreamEdge> links;
    List<URLNode> highdrop;
    List<URLNode> mainland;

    public SankeyGraphJsonObj(SankeyGraph graph,List<URLNode> highdrop,List<URLNode> mainland){
        nodes=(ArrayList<URLNode>)graph.getNodes();
        links=(ArrayList<StreamEdge>)graph.getLinks();
        this.highdrop=highdrop;
        this.mainland=mainland;
    }

    public SankeyGraphJsonObj(SankeyGraph graph){
        nodes=(ArrayList<URLNode>)graph.getNodes();
        links=(ArrayList<StreamEdge>)graph.getLinks();
    }

    public String toJson(){
        Gson gson=new Gson();
        return gson.toJson(this);
    }
}
