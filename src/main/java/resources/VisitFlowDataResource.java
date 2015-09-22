package resources;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import entity.SankeyGraph;
import entity.SankeyGraphJsonObj;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import service.LogService;
import service.PathServiceTool;

import javax.json.JsonArray;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
* 路径图展示,个人会话还原部分
*/
@Path("/visitflow")
public class VisitFlowDataResource extends BasicResource{
    final MongoClient mongoClient;
    final DB siteDatabase;
    final PathServiceTool pathServiceTool;
    final LogService logService;

    public VisitFlowDataResource() throws UnknownHostException {
        super("Mongo_URI","Mongo_DB");
        mongoClient = new MongoClient(new MongoClientURI(this.uri));
        siteDatabase = mongoClient.getDB(this.db);
        pathServiceTool = new PathServiceTool(siteDatabase);
        logService = new LogService(siteDatabase);
    }

    //个人访问会话还原   //sessiontest1
    @Path("/sequence/id/{sessionID}")//DYuMjQxLjIyMy43OTIwMTQwODEwMDEwMTI1MjAzOTQzNDUxOTMN
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSequenceBySessionID(@PathParam("sessionID") String sessionID) {
        return pathServiceTool.getSequenceBySessionID(sessionID);
    }

    //群体访问路径(图)(一天算一次，比较耗时)，
    @Path("/path/{date}") //2014-8-10
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSessionPath(@PathParam("date") String date) throws ParseException, UnknownHostException {
        //建立一个数据目录
        String pathfolder="../webapp/vislog/pathdata";
        String pathfile=pathfolder+"/"+date+".json";

        File file = new File(pathfile);
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            //每次读取文件的缓存
            String temp = null;
            while((temp = reader.readLine()) != null){
                data.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭文件流
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }


    //筛选会话 条件:访问过的页面类别ID(category),日期(date)如2014-8-10),访问时长>(last),访问页面数>(pages),来源国家(country),设备(device)
    @Path("/filter")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getSessionIDList(String condition){
        return logService.getSessionListByCondition(condition).toString();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //个人访问路径(图)
    @Path("/path/id/{sessionID}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSessionPathByID(@PathParam("sessionID") String sessionID) throws ParseException, UnknownHostException {
        //传入日期参数和depth参数(路径深度)
        SankeyGraph sankeyGraph = pathServiceTool.getGraph(7, sessionID);
        String result = new SankeyGraphJsonObj(sankeyGraph).toJson();  //最终结果

        return result;
    }

    @Path("/path/state/{state}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStateNums(@PathParam("state")int state){
        System.out.println("execute........");
        return pathServiceTool.getStatesNum(state).toString();
    }




    @Path("/path/stateInfo/{state}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStateInfo(@PathParam("state")int state){
        System.out.println("execute........");
        return pathServiceTool.getStatesInfo(state).toString();
    }

    @Path("/path/category/{state}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getCategoryState(@PathParam("state")int state){
        System.out.println("execute........");

        return pathServiceTool.getCategoryNums(state).toString();
    }


    @Path("/path/jumpInfo/{state}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getCateJumpInfo(@PathParam("state")int state){
        System.out.println("execute........");

        return pathServiceTool.getJumpInfo(state).toString();
    }


    @Path("/path/depth/{depth}/{date}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getPathByDate1(@PathParam("depth") Integer depth,@PathParam("date") String date) throws ParseException, UnknownHostException {
        //传入日期参数和depth参数(路径深度)

       /*
        SankeyGraph sankeyGraph = pathServiceTool.getGraph(7, date, date2);
        String result = new SankeyGraphJsonObj(sankeyGraph).toJson();  //最终结果
        */
        //System.out.println("..................");
        //SankeyGraph sankeyGraph = ps.getGraph(7, date+" 0:0:0", date+" 23:59:59")

        String str = pathServiceTool.getGrapthByTime(depth, date).toString();

        /*

        File file = new File("graphByCategory_" + date + ".json" );
        System.out.println(file.getAbsolutePath());
        if(!file.exists()) try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

*/
        return str;
        //return pathServiceTool.getGrapthByTime(depth, date).toString();
    }

    /*

    @Path("/path/depth/{depth}/{date}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSankyGraph(@PathParam("depth") Integer depth,@PathParam("date") String date) throws ParseException, UnknownHostException {

        return pathServiceTool.getGraphByCategory(depth, date).toString();
    }
    */

}
