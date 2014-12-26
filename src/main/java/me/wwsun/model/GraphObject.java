package me.wwsun.model;

import com.mongodb.DBObject;

import java.util.List;

/**
 * Created by Weiwei on 12/26/2014.
 */
public class GraphObject {
    private List<DBObject> nodeList;
    private List<DBObject> linkList;

    public List<DBObject> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<DBObject> nodeList) {
        this.nodeList = nodeList;
    }

    public List<DBObject> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<DBObject> linkList) {
        this.linkList = linkList;
    }
}
