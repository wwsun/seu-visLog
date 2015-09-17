package service;

import com.mongodb.*;
import dao.EventsDAO;
//import dao.EventsFalseDAO;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据整合：For  CustomerDataResource
 * 语义热点图
 */
public class HeatmapService {

    EventsDAO eventsDAO;

    public HeatmapService(final DB db) {
        eventsDAO = new EventsDAO(db);
    }

    //keys,lengths，根据lengths排序，同时交换对应key的位置
    public void QuickSortByValue(String[] keys, int [] values, int low, int high) {
        if (keys.length == 0 || values.length == 0 || keys.length != values.length)
            return;
        if (low < 0 || high >= values.length || low >= high)
            return;
        String key = keys[low];
        int value = values[low];
        int i = low;
        int j = high;
        while (i < j) {
            while (values[j] <= value && j > i) {
                j--;
            }
            values[i] = values[j]; //
            keys[i] = keys[j];
            while (values[i] >= value && j > i) {
                i++;
            }
            values[j] = values[i];
            keys[j] = keys[i];
        }
        values[i] = value;
        keys[i] = key;

        QuickSortByValue(keys, values, low, i - 1);
        QuickSortByValue(keys, values, i + 1, high);
    }

    //获得所有语义的列表
    public List<DBObject> getSemanticsList() {
        return eventsDAO.getSemanticsList();
    }

    //在某一语义下，top k热门页面
    public List<DBObject> getHeatPagesBySemantic(String semantic, int limit) {
        return eventsDAO.getTopURLBySemantic(semantic, limit);
    }

    //在某一事件类型下，top k热门页面
    public List<DBObject> getHeatPages(String event, int limit) {
        return eventsDAO.getTopURLByEvent(event, limit);
    }

    //给定一个url,统计该页面语义分布
    public JsonArray getSemanticsDistribution(String url) {
        Map<String, Integer> semantics = eventsDAO.getSemanticsDistributionByURL(url);
        String[] seman_arr = new String[semantics.size()];
        int[] lengths = new int[semantics.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : semantics.entrySet()) {
            seman_arr[i] = entry.getKey();
            lengths[i] = entry.getValue();
            i++;
        }
        //按值排序
        QuickSortByValue(seman_arr, lengths, 0, seman_arr.length - 1);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        //把页面语义内容浏览分布转成json
        for (int j = 0; j < seman_arr.length; j++) {
            jsonArrayBuilder.add(Json.createObjectBuilder().add("semantics", seman_arr[j]).add("length", lengths[j]));
        }
        return jsonArrayBuilder.build();
    }

    //根据一个url,具体某个事件,获得语义分布
    public JsonArray getSemanticsDistributionByEvent(String url, String event) {
        Map<String, Integer> semantics = eventsDAO.getSemanticsNumsByURLEvent(url, event);
        //对结果进行排序
        String[] seman_arr = new String[semantics.size()];
        int [] lengths = new int[semantics.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : semantics.entrySet()) {
            seman_arr[i] = entry.getKey();
            lengths[i] = (int) entry.getValue();
            i++;
        }
        QuickSortByValue(seman_arr, lengths, 0, seman_arr.length - 1);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        //把页面语义内容浏览分布转成json
        for (int j = 0; j < seman_arr.length; j++) {
            jsonArrayBuilder.add(Json.createObjectBuilder().add("semantics", seman_arr[j]).add("length", lengths[j]));
        }
        return jsonArrayBuilder.build();
    }

   //////////////////
    //连接操作，根据传回的ip,url,datetime（服务器端的url的时间）,获得一个URL的所有语义分布
    public JsonArray getSemanticsDistribution(String ip, String url, String dateTime) {
        //传入的是服务器端url的时间，这里去前端数据里（前段得到的是页面loadtime）去匹配，
        // 时间会稍微不一致，为此时间应该向后推迟一些，这里延迟10s
        // dateTime +10s <= loadtime,
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Integer> semantics = null;
        try {
            Date date = sdf.parse(dateTime);
            Calendar cl = Calendar.getInstance();
            cl.setTime(date);
            cl.add(Calendar.SECOND, 10);
            String delayTime = sdf.format(cl.getTime());
            //获的所有语义的分布
            semantics = eventsDAO.getSemanticsDistribution(ip, url, dateTime, delayTime);
        } catch (ParseException e) {

        }
       // System.out.println(semantics);
        //对结果进行排序
        String[] seman_arr = new String[semantics.size()];
        int [] lengths = new int[semantics.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : semantics.entrySet()) {
            seman_arr[i] = entry.getKey();
            lengths[i] = (int) entry.getValue();
            i++;
        }
        QuickSortByValue(seman_arr, lengths, 0, seman_arr.length - 1);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        //把页面语义内容浏览分布转成json
        for (int j = 0; j < seman_arr.length; j++) {
            jsonArrayBuilder.add(Json.createObjectBuilder().add("semantics", seman_arr[j]).add("length", lengths[j]));
        }

        return jsonArrayBuilder.build();
    }

    //连接操作，根据传回的ip,url,datetime（服务器端的url的时间）,具体某个事件，获得一个URL的所有语义分布
    public JsonArray getSemanticsDistributionByEvent(String ip, String url, String dateTime, String event) {
        //传入的是服务器端url的时间，这里去前端数据里（前段得到的是页面loadtime）去匹配，
        // 时间会稍微不一致，为此时间应该向后推迟一些，这里延迟10s
        // dateTime +10s <= loadtime,
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Integer> semantics = null;
        try {
            Date date = sdf.parse(dateTime);
            Calendar cl = Calendar.getInstance();
            cl.setTime(date);
            cl.add(Calendar.SECOND, 10);
            String delayTime = sdf.format(cl.getTime());
            //获的所有语义的分布
            semantics = eventsDAO.getSemanticsDistributionByEvent(ip, url, dateTime, delayTime, event);
        } catch (ParseException e) {

        }
        //对结果进行排序
        String[] seman_arr = new String[semantics.size()];
        int [] lengths = new int [semantics.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : semantics.entrySet()) {
            seman_arr[i] = entry.getKey();
            lengths[i] = (int) entry.getValue();
            i++;
        }
        QuickSortByValue(seman_arr, lengths, 0, seman_arr.length - 1);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        //把页面语义内容浏览分布转成json
        for (int j = 0; j < seman_arr.length; j++) {
            jsonArrayBuilder.add(Json.createObjectBuilder().add("semantics", seman_arr[j]).add("length", lengths[j]));
        }
        return jsonArrayBuilder.build();
    }


    public static void main(String [] args)throws IOException,ParseException {
        final String mongoURI = "mongodb://223.3.75.101:27017";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        DB db = mongoClient.getDB("huawei");
        HeatmapService heatmapService = new HeatmapService(db);
        String ip = "223.3.75.101";
        String url = "223.3.68.141:8080/html/HongMiNote.html";
        String dateTime = "2015-07-22 18:08:04";
        String s1=heatmapService.getSemanticsDistribution(ip, url, dateTime).toString();
        System.out.println(s1);
    }
}
