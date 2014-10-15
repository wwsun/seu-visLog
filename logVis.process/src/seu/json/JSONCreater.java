package seu.json;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import seu.entity.Edge;
import seu.entity.Node;

import java.io.*;
import java.util.*;

/**
 * Created by Weiwei on 10/13/2014.
 */
public class JSONCreater {

    static void dataToJSON4Table(String input, String output){
        BufferedReader br = null;
        BufferedWriter bw = null;

        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            String line;

            List<Node> nodes = new LinkedList<Node>();
            Map<String, Integer> map = new HashMap<String, Integer>();
            Map<String, Integer> groupMap = new HashMap<String, Integer>();
            int index = 0;
            int group = 0;
            while((line = br.readLine())!=null) {
                String[] arr = line.split(",");
                Node node = new Node();
                node.setName(arr[0]);
                node.setWeight(Integer.valueOf(arr[1]));
                node.setDomain(arr[2]);
                nodes.add(node);
                if(map.get(node.getName())==null){
                    map.put(node.getName(),index++);
                }
                if(groupMap.get(node.getDomain())==null){
                    groupMap.put(node.getDomain(),group++);
                }
            }

            String result = createJSON(map,groupMap, nodes);
            bw.write(result);


        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String createJSON(Map indexMap,Map groupMap, List<Node> nodeList) {
        JSONObject json = new JSONObject();

        //the array of nodes
        JSONArray nodes = new JSONArray();
        //the array of links
        JSONArray links = new JSONArray();


        for (Node item : nodeList) {
            JSONObject node = new JSONObject();
            JSONObject link = new JSONObject();

            node.put("name", item.getName());
            node.put("size", dataNormalization(item.getWeight()));
            node.put("group", groupMap.get(item.getDomain()));

            link.put("source", indexMap.get(item.getName()));
            link.put("target", indexMap.size());

            nodes.add(node);
            links.add(link);
        }

        /**
         * create the virtual node
         */
        JSONObject vnode = new JSONObject();
        vnode.put("name", "outsite");
        vnode.put("size", 10);
        vnode.put("group", 100);
        nodes.add(vnode);

        json.put("nodes", nodes);
        json.put("links", links);

        System.out.println("Stage: Create json file");

        return json.toString();
    }

    /**
     * Normalize data to 1 to 20
     * @param number
     * @return
     */
    public static double dataNormalization(double number){
        double out = (number - 1)/(6095 - 1);
        return out*20+1;
    }

    public static double dataNormalization(double max, double min, double number){
        double norm = (number - min)/(max - min);
        return norm;
    }

    static void dataToJSON(String input, String output){
        BufferedReader br = null;
        BufferedWriter bw = null;

        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            String line;

            List<Node> nodes = new LinkedList<Node>();
            //Map<String, Integer> nodeMap = new HashMap<String, Integer>();//map用来建立索引
            Set<Node> nodeSet = new HashSet<Node>();//set用来存储列表, 因为节点是不重复出现的
            List<Edge> edgeList = new ArrayList<Edge>();

            int index = 0;
            //int group = 0;

            while((line = br.readLine())!=null) {
                Node reqNode = new Node();
                Node refNode = new Node();
                Edge edge = new Edge();
                String[] arr = line.split("\t");

                edge.setTarget(arr[0]);
                edge.setSource(arr[1]);
                edge.setWeight(Integer.valueOf(arr[2]));
                edgeList.add(edge);


                if(!nodeSet.contains(arr[0])){
                    reqNode.setName(arr[0]);
                    reqNode.setGroup(0);//group 0: inner site nodes
                    nodeSet.add(reqNode);
                }

                if(!nodeSet.contains(arr[1])){
                    refNode.setName(arr[1]);
                    reqNode.setGroup(1);//group 1: out site nodes
                    nodeSet.add(refNode);
                }

            }

            String result = toJSON(nodeSet, edgeList);
            bw.write(result);


        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String toJSON(Set<Node> nodeSet, List<Edge> edgeList) {
        JSONObject json = new JSONObject();

        //the array of nodes
        JSONArray nodes = new JSONArray();
        //the array of links
        JSONArray links = new JSONArray();

        Integer index = 0;
        Map<String, Integer> nodeIndexMap = new HashMap<String, Integer>();

        for (Node item : nodeSet) {
            JSONObject node = new JSONObject();
            node.put("name", item.getName());
            node.put("group", item.getGroup());
            nodes.add(node);
            nodeIndexMap.put(item.getName(), index++);//建立节点在json中的位置索引
        }


        //节点的索引应该是json文件中的索引位置
        for (Edge edge : edgeList) {
            JSONObject link = new JSONObject();
            link.put("source", nodeIndexMap.get(edge.getSource()));
            link.put("target", nodeIndexMap.get(edge.getTarget()));
            link.put("value", dataNormalization(1, 816, edge.getWeight()));
            links.add(link);
        }

        json.put("nodes", nodes);
        json.put("links", links);

        System.out.println("Stage: Create json file");

        return json.toString();
    }


}
