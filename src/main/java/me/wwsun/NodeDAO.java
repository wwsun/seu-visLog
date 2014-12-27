package me.wwsun;

import com.mongodb.*;

/**
 * Created by Weiwei on 12/27/2014.
 */
public class NodeDAO {
    private DBCollection nodes;

    public NodeDAO(final DB siteDatabase) {
        nodes = siteDatabase.getCollection("nodes");
    }

    public DBObject getNodeByName(String name) {
        QueryBuilder builder = QueryBuilder.start("url").is(name);
        return nodes.findOne(builder.get(), new BasicDBObject("_id", false));
    }
}
