package service;

import com.mongodb.*;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import dao.LevelDAO;
import dao.LogDAO;
import dao.PathDAO;
import entity.SankeyGraph;
import entity.SankeyGraphJsonObj;
import entity.StreamEdge;
import entity.URLNode;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �����ϣ�For VisitFlowDataResource
 */
public class PathServiceTool {

    PathDAO pathDAO;
    LevelDAO levelDAO;
    LogDAO logDAO;

    public PathServiceTool(DB db) {
        pathDAO = new PathDAO(db);
        levelDAO = new LevelDAO(db);  //��Ҫ�õ�regex,name
        logDAO = new LogDAO(db);
    }

    //���˻Ự��ԭ(�Ƿ���Ҫ���Ӹ��ip��ȡ)
    public String getSequenceBySessionID(String sessionID) {
        return logDAO.getPathBySession(sessionID);
    }

    //���뿪ʼʱ��ͽ���ʱ�䣬���ȫ��·����Ȼ����Ҫ���ˣ�����ȡ��TOP10��
    public SankeyGraph getGraph(int depth, String startTime, String endTime) throws UnknownHostException, ParseException {
        List<URLNode> nodes;
        //��session��ǵ�·����ݣ����ڼ���ÿ���ڵ��out_degree��in_degree
        List<StreamEdge> links;
        //����session��ǵ�·����ݣ���ÿ���ߵ�Ȩֵ��������ʾ
        List<StreamEdge> noSessionLinksList;
        SankeyGraph graph = null;
        int node_index = 0;   //������name

        nodes = new ArrayList<URLNode>();
        links = new ArrayList<StreamEdge>();
        noSessionLinksList = new ArrayList<StreamEdge>();

        //����map���ڴ�ţ����->index�������յ�->index����ӳ��
        Map<String, Integer> startMap = null;
        Map<String, Integer> endMap = null;

        //depthmap���ڴ��ÿ���ڵ㣨��name��ʶ������·������ȵĶ�Ӧ��ϵ
        Map<Integer, Integer> depthMap = new HashMap<Integer, Integer>();

        //����� ȡ �����
        for (int i = 0; i < depth; i++) {
            //��Ų�ͬsession��ͬ�����յ��·�ε�ln(count)֮��
            Map<String, Double> segCount = new HashMap<String, Double>();
            //��ʼ��map
            if (i == 0) {
                startMap = new HashMap<String, Integer>();
            } else {
                startMap = endMap;
            }
            endMap = new HashMap<String, Integer>();
            //һ��ȡ���
            List<DBObject> pathGroupList = pathDAO.groupByFour(i + 1, i + 2, startTime, endTime);

            //ȡ�õ���ݰ� _id(start,end,session),nums
            for (DBObject obj : pathGroupList) {
                int count = (Integer) obj.get("nums");  //��ñߵ�count
                DBObject idObject = (DBObject) obj.get("_id");
                String start_url = (String) idObject.get("P" + (i + 1));
                String end_url = (String) idObject.get("P" + (i + 2));
                String session = (String) idObject.get("session");

                if (!(start_url.equals("null")) && !(end_url.equals("null"))) {
                    if (i == 0) {
                        //startMap��<start_url,node_index>
                        if (!startMap.containsKey(start_url)) {
                            startMap.put(start_url, node_index);
                            depthMap.put(node_index, i);
                            node_index++;
                        }
                    }
                    //endMap��<end_url,node_index>
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
        //��nodes����name����
        for (URLNode node : nodes) {
            //����map���ڼ�¼ͬһ��session���Ե�ǰ�ڵ��Ϊ���ıߵ�value֮��
            Map<String, Double> in_session = new HashMap<String, Double>();
            Map<String, Double> out_session = new HashMap<String, Double>();
            //����ÿ��node�ĳ��Ⱥ����
            double out = 0;
            double in = 0;
            for (StreamEdge link : links) {
                if (link.getSource() == node.getName()) {
                    if (out_session.containsKey(link.getSession())) {
                        double value = out_session.get(link.getSession());
                        out_session.put(link.getSession(), value + link.getValue());
                    } else
                        out_session.put(link.getSession(), link.getValue());
                }
                if (link.getTarget() == node.getName()) {
                    if (in_session.containsKey(link.getSession())) {
                        double value = in_session.get(link.getSession());
                        in_session.put(link.getSession(), value + link.getValue());
                    } else
                        in_session.put(link.getSession(), link.getValue());
                }
            }
            for (Map.Entry<String, Double> entry : out_session.entrySet()) {
                out += Math.log(entry.getValue()) + 1;
            }
            for (Map.Entry<String, Double> entry : in_session.entrySet()) {
                in += Math.log(entry.getValue()) + 1;
            }
            node.setIn_degree(in);
            node.setOut_degree(out);

            //����ÿ��node��url��������Ϣ
            String[][] RegexName = levelDAO.getRegexName();
            for (int j = 0; j < RegexName.length; j++) {
                Matcher m = Pattern.compile(RegexName[j][0]).matcher(node.getUrl());
                while (m.find()) {
                    node.setSemantics(RegexName[j][1]);  //
                }
            }
        }
        //depth�������Ժ�
        //����depthΪ0�Ľڵ�����
        Map<String, Double> Startout_session = new HashMap<String, Double>();
        List<DBObject> path0List = pathDAO.getDepth0(startTime, endTime);
        //ȡ�õ���ݰ� _id(start,session),nums
        for (DBObject obj : path0List) {
            int count = (Integer) obj.get("nums");
            DBObject idObject = (DBObject) obj.get("_id");
            String start_url = (String) idObject.get("P1");
            //  String session = (String) idObject.get("session");

            if (Startout_session.containsKey(start_url)) {
                double value = Startout_session.get(start_url);
                Startout_session.put(start_url, value + Math.log((double) count) + 1);
            } else
                Startout_session.put(start_url, Math.log((double) count) + 1);
        }
        //����start_url�����
        for (URLNode node : nodes) {
            if (node.getDepth() == 0) {
                node.setIn_degree(Startout_session.get(node.getUrl()));
            }
        }
        //����ÿ���ڵ�������
        for (URLNode node : nodes) {
            double in = node.getIn_degree();
            double out = node.getOut_degree();
            node.setDrop_per((in - out) / in);
        }

        graph = new SankeyGraph(nodes, noSessionLinksList);
        return graph;
    }

    //����·����ԭ(ͼ)
    public SankeyGraph getGraph(int depth, String sessionID) throws UnknownHostException, ParseException {

        List<URLNode> nodes;
        //��session��ǵ�·����ݣ����ڼ���ÿ���ڵ��out_degree��in_degree
        List<StreamEdge> links;
        //����session��ǵ�·����ݣ���ÿ���ߵ�Ȩֵ��������ʾ
        List<StreamEdge> noSessionLinksList;

        SankeyGraph graph = null;
        int node_index = 0;   //������name

        nodes = new ArrayList<URLNode>();
        links = new ArrayList<StreamEdge>();
        noSessionLinksList = new ArrayList<StreamEdge>();

        //����map���ڴ�ţ����->index�������յ�->index����ӳ��
        Map<String, Integer> startMap = null;
        Map<String, Integer> endMap = null;

        //depthmap���ڴ��ÿ���ڵ㣨��name��ʶ������·������ȵĶ�Ӧ��ϵ
        Map<Integer, Integer> depthMap = new HashMap<Integer, Integer>();

        //����� ȡ �����
        for (int i = 0; i < depth; i++) {
            //��Ų�ͬsession��ͬ�����յ��·�ε�ln(count)֮��
            Map<String, Double> segCount = new HashMap<String, Double>();
            //��ʼ��map
            if (i == 0) {
                startMap = new HashMap<String, Integer>();
            } else {
                startMap = endMap;
            }
            endMap = new HashMap<String, Integer>();
            //һ��ȡ���
            List<DBObject> pathGroupList = pathDAO.groupBySessionID(i + 1, i + 2, sessionID);
//            System.out.println(pathGroupList);

            //ȡ�õ���ݰ� _id(start,end,session),nums
            for (DBObject obj : pathGroupList) {
                int count = (Integer) obj.get("nums");  //��ñߵ�count
                DBObject idObject = (DBObject) obj.get("_id");
                String start_url = (String) idObject.get("P" + (i + 1));
                String end_url = (String) idObject.get("P" + (i + 2));
                String session = (String) idObject.get("session");

                if (!(start_url.equals("null")) && !(end_url.equals("null"))) {
                    if (i == 0) {
                        //startMap��<start_url,node_index>
                        if (!startMap.containsKey(start_url)) {
                            startMap.put(start_url, node_index);
                            depthMap.put(node_index, i);
                            node_index++;
                        }
                    }
                    //endMap��<end_url,node_index>
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
        //��nodes����name����
        for (URLNode node : nodes) {
            //����map���ڼ�¼ͬһ��session���Ե�ǰ�ڵ��Ϊ���ıߵ�value֮��
            Map<String, Double> in_session = new HashMap<String, Double>();
            Map<String, Double> out_session = new HashMap<String, Double>();
            //����ÿ��node�ĳ��Ⱥ����
            double out = 0;
            double in = 0;
            for (StreamEdge link : links) {
                if (link.getSource() == node.getName()) {
                    if (out_session.containsKey(link.getSession())) {
                        double value = out_session.get(link.getSession());
                        out_session.put(link.getSession(), value + link.getValue());
                    } else
                        out_session.put(link.getSession(), link.getValue());
                }
                if (link.getTarget() == node.getName()) {
                    if (in_session.containsKey(link.getSession())) {
                        double value = in_session.get(link.getSession());
                        in_session.put(link.getSession(), value + link.getValue());
                    } else
                        in_session.put(link.getSession(), link.getValue());
                }
            }
            for (Map.Entry<String, Double> entry : out_session.entrySet()) {
                out += Math.log(entry.getValue()) + 1;
            }
            for (Map.Entry<String, Double> entry : in_session.entrySet()) {
                in += Math.log(entry.getValue()) + 1;
            }
            node.setIn_degree(in);
            node.setOut_degree(out);

//            System.out.println("NODES:"+nodes);

            //����ÿ��node��url��������Ϣ
            String[][] RegexName = levelDAO.getRegexName();
            for (int j = 0; j < RegexName.length; j++) {
                Matcher m = Pattern.compile(RegexName[j][0]).matcher(node.getUrl());
                while (m.find()) {
                    node.setSemantics(RegexName[j][1]);  //
                }
            }
        }
        //depth�������Ժ�
        //����depthΪ0�Ľڵ�����
        Map<String, Double> Startout_session = new HashMap<String, Double>();
        List<DBObject> path0List = pathDAO.getDepth0(sessionID);
//        System.out.println(path0List);
        //ȡ�õ���ݰ� _id(start,session),nums
        for (DBObject obj : path0List) {
            int count = (Integer) obj.get("nums");
            DBObject idObject = (DBObject) obj.get("_id");
            String start_url = (String) idObject.get("P1");
            //  String session = (String) idObject.get("session");

            if (Startout_session.containsKey(start_url)) {
                double value = Startout_session.get(start_url);
                Startout_session.put(start_url, value + Math.log((double) count) + 1);
            } else
                Startout_session.put(start_url, Math.log((double) count) + 1);
        }
        //����start_url�����
        for (URLNode node : nodes) {
            if (node.getDepth() == 0) {
                node.setIn_degree(Startout_session.get(node.getUrl()));
            }
        }
        //����ÿ���ڵ�������
        for (URLNode node : nodes) {
            double in = node.getIn_degree();
            double out = node.getOut_degree();
            node.setDrop_per((in - out) / in);
        }

        graph = new SankeyGraph(nodes, noSessionLinksList);
        return graph;
    }

   //�������Ⱥ��·���ļ�(һ����һ��)
   public static void main(String[] args)throws  ParseException,IOException{
       final String mongoURI = "mongodb://223.3.75.101:27017";
       MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
       DB db = mongoClient.getDB("huawei");
       PathServiceTool ps =new PathServiceTool(db);
       // �������ڲ����depth����(·�����)
       String date="2014-08-10";
       SankeyGraph sankeyGraph = ps.getGraph(7, date+" 0:0:0", date+" 23:59:59");

       //����ݽ�һ������õ�������ߵ�Ȩֵ����
       SankeyGraph FiltedGraph = sankeyGraph.FilterByEdgeValue(10); // ��ݱߵ�Ȩֵ����
       List<URLNode> highDropPage = sankeyGraph.topKDropPage(10); // topK
       List<URLNode> topKLandPage = sankeyGraph.topKLandPage(10); // topK ��½ҳ
       String result = new SankeyGraphJsonObj(FiltedGraph, highDropPage,
               topKLandPage).toJson(); // ���ս��

       //д�뵽�����tomcatĿ¼�����������
       BufferedWriter bw2 = new BufferedWriter(new FileWriter("E:\\toolkit\\apache-tomcat-7.0.56\\webapps\\vislog\\pathdata\\"+date+".json"));
       bw2.write(result);
       bw2.close();
       System.out.println("DONE");
   }

    public JsonArray getStatesNum(int state){

       List<DBObject> lists = pathDAO.getStateNums(state);


        JsonArrayBuilder jab = Json.createArrayBuilder();


        for(DBObject obj : lists){

            String session = (String)obj.get("_id");
            Integer nums = (Integer)obj.get("nums");

            jab.add(Json.createObjectBuilder().add("url", session).add("nums",nums));
        }

        return jab.build();

    }


    public JsonArray getStatesInfo(int state){

        List<DBObject> lists = pathDAO.getStateInfo(state);
        JsonArrayBuilder jab = Json.createArrayBuilder();
        String p = "P" + state;
        for(DBObject obj : lists){

            String session = (String)obj.get("session");
            String url = (String)obj.get(p);

            jab.add(Json.createObjectBuilder().add("session", session).add("url",url));
        }

        return jab.build();

    }


    public JsonArray getCategoryNums(int state){

        String [][]regex = levelDAO.getRegexName();

        String p = "P" + state;
        List<DBObject> lists = pathDAO.getStateInfo(state);
        HashMap<String, Integer> map = new HashMap<>();

        for(DBObject dbObject : lists) {

            for (int j = 0; j < regex.length; j++) {
                String url = (String)dbObject.get(p);

                Matcher m = Pattern.compile(regex[j][0]).matcher(url);

               if (m.find()) {
                    if(!map.containsKey(regex[j][1]))
                        map.put(regex[j][1], 1);
                     else
                        map.put(regex[j][1], map.get(regex[j][1]) + 1);
                   break;
                }
            }
        }

        Map<String, Integer> map1 = sortByValues(map);

        JsonArrayBuilder jab = Json.createArrayBuilder();
        for(Map.Entry<String, Integer> entry:map1.entrySet()){

            jab.add(Json.createObjectBuilder().add("category", entry.getKey()).add("nums",entry.getValue()));
        }



        return jab.build();
    }

    public JsonArray getJumpInfo(int state){

        JsonArray topPages = getStatesNum(state);
        //pathServiceTool.getStatesNum(state);
        JsonArray cateDistribution = getCategoryNums(state);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(Json.createObjectBuilder()
                .add("topPages", topPages)
                .add("cateDistribution", cateDistribution));


        return builder.build();
    }


    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });


        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }


    public JsonArray getGrapthByTime(int depth, String startTime, String endTime) throws ParseException, UnknownHostException {
        SankeyGraph sg = getGraph(depth, startTime, endTime);
        List<URLNode> list = (List<URLNode>)sg.getNodes();
        JsonArrayBuilder jab = Json.createArrayBuilder();

        for(URLNode u : list)
        {
            jab.add(Json.createObjectBuilder().add("category", u.getSemantics()).add("out_degree", u.getOut_degree())
                    .add("in_degree", u.getIn_degree()).add("drop_per", u.getDrop_per())
                    .add("depth", u.getDepth()).add("name", u.getName()));
        }

        JsonArrayBuilder jab2 = Json.createArrayBuilder();
        List<StreamEdge> ls = (List<StreamEdge>) sg.getLinks();
        for(StreamEdge se : ls){

            jab2.add(Json.createObjectBuilder().add("value", se.getValue()).add("target", se.getTarget()
            ).add("source", se.getSource()));
        }

        JsonArrayBuilder jab3 = Json.createArrayBuilder();
        jab3.add(Json.createObjectBuilder().add("nodes", jab).add("links",jab2));

        return jab3.build();
    }




}
