package resources;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import dao.EventsDAO;
import dao.FormDAO;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 用于接端发送的数据，解析数据存入数据库
 */

@Path("/receive")
public class ReceiveDataResource extends BasicResource {
    final MongoClient mongoClient;
    final DB siteDatabase;
    final EventsDAO eventsDAO;
    final FormDAO formDAO;

    public ReceiveDataResource() throws UnknownHostException {
        super("Mongo_URI", "Mongo_DB");
        mongoClient = new MongoClient(new MongoClientURI(this.uri));
        siteDatabase = mongoClient.getDB(this.db);
        eventsDAO = new EventsDAO(siteDatabase);
        formDAO =new FormDAO(siteDatabase);
    }

    //对接收的（click,mouseover,scroll）数据进行处理，并存入events
    @Path("/events/store")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void StoreEvents(String data, @Context HttpServletRequest request) throws UnsupportedEncodingException,IOException{

        String schema = "browser,os,url,ip,loadtime,time,element,id,text,semantics,event,left,top,height,width";
        HashSet<String> hs_log = new HashSet<String>();
        System.out.println(data);
        //读取获得的json数据
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonObject jsonobj = reader.readObject();
        reader.close();
        //获取来源IP,读取url，time，events等信息
        String ip = "";
        String browser = "";
        String os = "";
        String url = "";
        String loadtime = "";
        ip = getRemoteHost(request);
        browser = jsonobj.getString("browser");
        os = jsonobj.getString("os");
        url = jsonobj.getString("url").split("\\?")[0];
        url=url.replace("http://","").replace("HTTP://","").replace("https://","");
        loadtime = jsonobj.getString("loadime");
        JsonArray events = jsonobj.getJsonArray("events");

        //整理数据
        for (int i = 0; i < events.size(); i++) {
            JsonObject obj = events.getJsonObject(i);
            StringBuffer elebuf = new StringBuffer();
            //先存储头信息
            elebuf.append(browser);
            elebuf.append(",");
            elebuf.append(os);
            elebuf.append(",");
            elebuf.append(url);
            elebuf.append(",");
            elebuf.append(ip);
            elebuf.append(",");
            elebuf.append(loadtime);
            elebuf.append(",");
            //各个事件的信息
            elebuf.append(obj.getString("time"));
            elebuf.append(",");
            elebuf.append(obj.getString("element"));
            elebuf.append(",");
            try {
                elebuf.append(obj.getString("id"));
            } catch (Exception e) {
                elebuf.append("");
            }
            elebuf.append(",");
            elebuf.append(obj.getString("text"));
            elebuf.append(",");
            //在这里可以引入SVM标注，标注后在存入数据库
            //或者可以读取数据在标注
            elebuf.append("");  //semantics,标注后更新
            elebuf.append(",");
            elebuf.append(obj.getString("event"));
            elebuf.append(",");
            elebuf.append(obj.getString("left"));
            elebuf.append(",");
            elebuf.append(obj.getString("top"));
            elebuf.append(",");
            elebuf.append(obj.getString("height"));
            elebuf.append(",");
            elebuf.append(obj.getString("width"));
            //添加set中，去重
            hs_log.add(elebuf.toString());
        }

        eventsDAO.insertEvents(schema, hs_log);
        //  return "Post Data Success!";
    }

    //对接收的表单数据进行处理，并存入form
    @Path("/form/store")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void StoreForm(String data, @Context HttpServletRequest request) throws UnsupportedEncodingException,IOException{
        String schema = "";
      //  System.out.println(data);
        //读取获得的json数据
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonObject jsonobj = reader.readObject();
        reader.close();
        //获取来源IP,读取url，time，events等信息
        String ip = "";
        String browser = "";
        String os = "";
        String url = "";
        String loadtime = "";
        String focusTime = "";
        String blurTime = "";
        String text = "";
        String type = "";
        String id = "";
        String name = "";
        String costTime ="";
        ip = getRemoteHost(request);
        browser = jsonobj.getString("browser");
        os = jsonobj.getString("os");
        url = jsonobj.getString("url").split("\\?")[0];
        url=url.replace("http://","").replace("HTTP://","").replace("https://","");
        loadtime = jsonobj.getString("loadTime");
        focusTime = jsonobj.getString("focusTime");
        blurTime = jsonobj.getString("blurTime");
        text = jsonobj.getString("text");
        type = jsonobj.getString("type");
        id = jsonobj.getString("id");
        name = jsonobj.getString("name");
        costTime = jsonobj.getString("costTime");


        schema = "browser,os,url,loadTime,blurTime,id,name,focusTime,text,type,costTime,ip";
            StringBuffer elebuf = new StringBuffer();
            //先存储头信息
            elebuf.append(browser);
            elebuf.append(",");
            elebuf.append(os);
            elebuf.append(",");
            elebuf.append(url);
            elebuf.append(",");
            elebuf.append(loadtime);
            elebuf.append(",");
            elebuf.append(blurTime + ",");
            elebuf.append(id + ",");
            elebuf.append(name + ",");
            elebuf.append(focusTime + ",");
            elebuf.append(text + ",");
            elebuf.append(type + ",");
            elebuf.append(costTime + ",");
        elebuf.append(ip);
        formDAO.insertForm(schema, elebuf.toString());
        //  return "Post Data Success!";
    }

    //获得发送数据的ip
    public static String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }


    @Path("/test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "this is get";
    }
}
