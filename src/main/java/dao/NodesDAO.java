package dao;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Minglu SHAO
 * Created by Weiwei on 2015/4/27.
 */
public class NodesDAO {

    private DBCollection nodes;

    public NodesDAO(final DB siteDatabase) {
        nodes = siteDatabase.getCollection("nodes");
    }

    /**
     *
     * @param topk
     */
    public List<DBObject> getHotPages(int topk) {

        // $project
        DBObject fields = new BasicDBObject("url", 1);
        fields.put("inDegree", 1);
        DBObject project = new BasicDBObject("$project", fields);
        // $group
        DBObject groupFields = new BasicDBObject("_id", "$url");
        groupFields.put("nums", new BasicDBObject("$sum", "$inDegree"));
        DBObject group = new BasicDBObject("$group", groupFields);
        // $sort
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("nums", -1));
        //limit,ȡtop20
        DBObject limit = new BasicDBObject("$limit", topk);
        //run
        List<DBObject> pipeline = Arrays.asList(project, group, sort, limit);
        //allowDiskUse
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Cursor cursor = nodes.aggregate(pipeline, options);

        List<DBObject> pageList = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            pageList.add(item);
        }

        return pageList;
    }

}
