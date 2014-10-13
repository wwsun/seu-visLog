package seu.json;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import seu.Node;

import java.io.*;
import java.util.*;

/**
 * Created by Weiwei on 10/13/2014.
 */
public class JSONCreater {

    static void csvToJSON(String input, String output){
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
}
