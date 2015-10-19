package services;

import com.mongodb.DB;
import com.mongodb.DBObject;
import dao.LevelDAO;
import dao.PathDAO;
import entity.SankeyGraph;
import entity.StreamEdge;
import entity.URLNode;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathService {

    PathDAO pathDAO;
    LevelDAO levelDAO;

    public PathService(DB db) {
        pathDAO = new PathDAO(db);
        levelDAO = new LevelDAO(db);  //需要用到regex,name
    }

    public SankeyGraph getGraph(int depth, String startTime, String endTime) throws UnknownHostException, ParseException {

        List<URLNode> nodes;
        // 带session标记的路段数据，用于计算每个节点的out_degree和in_degree
        List<StreamEdge> links;
        // 不带session标记的路段数据，带每条边的权值，用于显示
        List<StreamEdge> noSessionLinksList;

        SankeyGraph graph = null;
        int node_index = 0;   //将来的name

        nodes = new ArrayList<URLNode>();
        links = new ArrayList<StreamEdge>();
        noSessionLinksList = new ArrayList<StreamEdge>();

        // 两个map用于存放（起点->index），（终点->index）的映射
        Map<String, Integer> startMap = null;
        Map<String, Integer> endMap = null;

        // depthmap用于存放每个节点（用name标识）和在路径中深度的对应关系
        Map<Integer, Integer> depthMap = new HashMap<Integer, Integer>();

        //按深度 取 边数据
        for (int i = 0; i < depth; i++) {
            //存放不同session相同起点和终点的路段的ln(count)之和
            Map<String, Double> segCount = new HashMap<String, Double>();
            //初始化map
            if (i == 0) {
                startMap = new HashMap<String, Integer>();
            } else {
                startMap = endMap;
            }
            endMap = new HashMap<String, Integer>();
            //一次取数据
            List<DBObject> pathGroupList = pathDAO.groupByFour(i + 1, i + 2, startTime, endTime);

            //取得的数据包含 _id(start,end,session),nums
            for (DBObject obj : pathGroupList) {
                int count = (Integer) obj.get("nums");  //获得边的count
                DBObject idObject = (DBObject) obj.get("_id");
                String start_url = (String) idObject.get("P" + (i + 1));
                String end_url = (String) idObject.get("P" + (i + 2));
                String session = (String) idObject.get("session");

                if (!(start_url.equals("null")) && !(end_url.equals("null"))) {
                    if (i == 0) {
                        //startMap中<start_url,node_index>
                        if (!startMap.containsKey(start_url)) {
                            startMap.put(start_url, node_index);
                            depthMap.put(node_index, i);
                            node_index++;
                        }
                    }
                    //endMap中<end_url,node_index>
                    if (!endMap.containsKey(end_url)) {
                        endMap.put(end_url, node_index);
                        depthMap.put(node_index, i + 1);
                        node_index++;
                    }

                    StreamEdge link = new StreamEdge(startMap.get(start_url), endMap.get(end_url), (double) count);
                    link.setSession(session);
                    links.add(link);

                    String key = start_url + "~" + end_url;
                    // System.out.println(key);
                    if (!segCount.containsKey(key)) {
                        segCount.put(key, Math.log(count) + 1);
                    } else {
                        double old_count = segCount.get(key);
                        segCount.put(key, old_count + Math.log(count) + 1);
                    }

                }
            }
            //
            for (Map.Entry<String, Double> entry : segCount.entrySet()) {
                String key = entry.getKey();
                double count = entry.getValue();
                String start_url = key.split("~")[0];
                String end_url = key.split("~")[1];

                StreamEdge noSessionLinks = new StreamEdge(startMap.get(start_url), endMap.get(end_url), count);

                noSessionLinksList.add(noSessionLinks);
            }

            if (i == 0) {
                for (Map.Entry<String, Integer> entry : startMap.entrySet()) {
                    //startMap<start_url, node_index>
                    //node_index  >>>>  name
                    //URLNode(Integer name,String url)
                    URLNode node = new URLNode(entry.getValue(), entry.getKey());
                    node.setDepth(depthMap.get(entry.getValue()));
                    nodes.add(node);
                }
            }
            for (Map.Entry<String, Integer> entry : endMap.entrySet()) {
                URLNode node = new URLNode(entry.getValue(), entry.getKey());
                node.setDepth(depthMap.get(entry.getValue()));
                nodes.add(node);
            }

        }

        // nodeOut存放node的name和其outDegree
        // nodeIn存放node的name和其InDegree
        Map<Integer,Double> nodeOut=new HashMap<>();
        Map<Integer,Double> nodeIn=new HashMap<>();

        // 将links按照source排序
        Collections.sort(links, new Comparator<StreamEdge>() {
            @Override
            public int compare(StreamEdge o1, StreamEdge o2) {
                if(o1.getSource()<o2.getSource())
                    return -1;
                else if(o1.getSource()>o2.getSource())
                    return 1;
                else
                    return 0;
            }
        });

        int curSource=0;
        int preSource=links.get(0).getSource();
        String curSession=null;
        Map<String,Double> outSession=new HashMap<>();
        outSession.put(links.get(0).getSession(),links.get(0).getValue());

        for(int i=1;i<links.size();i++) {
            curSource = links.get(i).getSource();
            curSession = links.get(i).getSession();
            double curValue = links.get(i).getValue();
            if (curSource != preSource) {
                double count = 0;
                for (Map.Entry<String, Double> entry : outSession.entrySet()) {
                    count += Math.log(entry.getValue()) + 1;
                }
                nodeOut.put(preSource, count);
                outSession = new HashMap<>();
                outSession.put(curSession, curValue);
            } else {
                if (outSession.containsKey(curSession))
                    outSession.put(curSession, outSession.get(curSession) + curValue);
                else
                    outSession.put(curSession, curValue);
            }
            preSource = curSource;
            if (i == links.size() - 1) {
                double count = 0;
                for (Map.Entry<String, Double> entry : outSession.entrySet()) {
                    count += Math.log(entry.getValue()) + 1;
                }
                nodeOut.put(preSource, count);
                outSession = null;
            }
        }

        // 将links按照target排序
        Collections.sort(links, new Comparator<StreamEdge>() {
            @Override
            public int compare(StreamEdge o1, StreamEdge o2) {
                if(o1.getTarget()<o2.getTarget())
                    return -1;
                else if(o1.getTarget()>o2.getTarget())
                    return 1;
                else
                    return 0;
            }
        });


        int curTarget=0;
        int preTarget=links.get(0).getTarget();
        curSession=null;
        Map<String,Double> inSession=new HashMap<>();
        inSession.put(links.get(0).getSession(),links.get(0).getValue());

        for(int i=1;i<links.size();i++){
            curTarget=links.get(i).getTarget();
            curSession=links.get(i).getSession();
            double curValue=links.get(i).getValue();
            if(curTarget!=preTarget){
                double count=0;
                for(Map.Entry<String,Double> entry : inSession.entrySet()){
                    count+=Math.log(entry.getValue()) + 1;
                }
                nodeIn.put(preTarget,count);
                inSession=new HashMap<>();
                inSession.put(curSession,curValue);
            }
            else {
                if(inSession.containsKey(curSession))
                    inSession.put(curSession,inSession.get(curSession)+curValue);
                else
                    inSession.put(curSession,curValue);
            }
            preTarget=curTarget;
            if(i==links.size()-1){
                double count=0;
                for(Map.Entry<String,Double> entry : inSession.entrySet()){
                    count+=Math.log(entry.getValue()) + 1;
                }
                nodeIn.put(preTarget,count);
                inSession=null;
            }
        }

        Collections.sort(nodes, new Comparator<URLNode>() {
            @Override
            public int compare(URLNode o1, URLNode o2) {
                if(o1.getName()<o2.getName())
                    return -1;
                else if(o1.getName()>o2.getName())
                    return 1;
                else
                    return 0;
            }
        });

        for (URLNode node : nodes) {
            int name=node.getName();
            if(nodeIn.containsKey(name))
                node.setIn_degree(nodeIn.get(name));
            if(nodeOut.containsKey(name))
                node.setOut_degree(nodeOut.get(name));

            // 设置每个node的url的语义信息
            String[][] RegexName = levelDAO.getRegexName();
            for (int j = 0; j < RegexName.length; j++) {
                Matcher m = Pattern.compile(RegexName[j][0]).matcher(node.getUrl());
                if(m.matches()) {
                    node.setSemantics(RegexName[j][1]);
                    break;
                }
            }
        }

        // depth次跑完以后
        // 计算depth为0的节点的入度
        Map<String, Double> Startout_session = new HashMap<String, Double>();
        List<DBObject> path0List = pathDAO.getDepth0(startTime, endTime);
        // 取得的数据包含 _id(start,session),nums
        for (DBObject obj : path0List) {
            int count = (Integer) obj.get("nums");
            DBObject idObject = (DBObject) obj.get("_id");
            String start_url = (String) idObject.get("P1");
            // String session = (String) idObject.get("session");

            if (Startout_session.containsKey(start_url)) {
                double value = Startout_session.get(start_url);
                Startout_session.put(start_url, value + Math.log((double) count) + 1);
            } else
                Startout_session.put(start_url, Math.log((double) count) + 1);
        }
        // 设置start_url的入度
        for (URLNode node : nodes) {
            if (node.getDepth() == 0) {
                node.setIn_degree(Startout_session.get(node.getUrl()));
            }
        }
        // 设置每个节点的跳出率
        for (URLNode node : nodes) {
            double in = node.getIn_degree();
            double out = node.getOut_degree();
            node.setDrop_per((in - out) / in);
        }

        graph = new SankeyGraph(nodes, noSessionLinksList);
        return graph;
    }
}