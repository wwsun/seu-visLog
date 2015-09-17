package resources;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import service.CustomerSourceService;
import service.HeatmapService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;

/**
 *
 * 国家维度细分数据
 * 前端语义数据展示
 *
 * 数据来自前端采集和日志
 */
@Path("/customer")
public class CustomerDataResource extends BasicResource {
    final MongoClient mongoClient;
    final DB siteDatabase;
    final CustomerSourceService customerSourceService;
    final HeatmapService heatmapService;

    public CustomerDataResource() throws UnknownHostException {
        super("Mongo_URI","Mongo_DB");
        mongoClient = new MongoClient(new MongoClientURI(this.uri));
        siteDatabase = mongoClient.getDB(this.db);
        customerSourceService = new CustomerSourceService(siteDatabase);
        heatmapService = new HeatmapService(siteDatabase);
    }

    //某个国家的历史访问趋势
    @Path("/geo/distribution/{country}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGeoDistribution(@PathParam("country") String country){
        return customerSourceService.getGeoSessionDistribution(country).toString();
    }

    //主要来源国家的具体访问情况，国家维度细分数据
    @Path("/geo/detail")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGeoDetail() {
        return customerSourceService.getGeoDetail(10).toString();
    }

    //主要设备来源
    @Path("/devices/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSourceDevices() {
        return customerSourceService.getSourceDevices().toString();
    }

    //主要浏览器来源
    @Path("/browsers/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSourceBrowsers() {
        return customerSourceService.getSourceBrowsers().toString();
    }

//////////////////////
    //转化漏斗模板
    @Path("/funnel/{date}") //2014-08-10
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getConversionFunnel(@PathParam("date") String date) {
      return customerSourceService.getConversionFunnel(date).toString();
    }

    //目标转化率模板
    @Path("/targetList/{date}") //2014-08-10
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTargetConversionList(@PathParam("date") String date) {
        return customerSourceService.getConversionList(date).toString();
    }

    ///////////////////////////////////////////////////////////////////
    //采集的页面事件中的内容语义列表
    @Path("/heatmap/semantics")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getPageSemantics(){
        return heatmapService.getSemanticsList().toString();
    }

    //某语义下的top页面
    @Path("/heatmap/hotpagesbyse/{semantic}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHotPagesBySemantics(@PathParam("semantic") String semantic){
        return heatmapService.getHeatPagesBySemantic(semantic, 10).toString();
    }

    //改为某事件下的top页面，
    @Path("/heatmap/hotpagesbyev/{event}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHotPagesByEvent(@PathParam("event") String event){
        // 这里就暂时click,可以选择其他
        return heatmapService.getHeatPages(event, 10).toString();
    }


    //某个URL主要事件区域分布  // 223.3.68.141:8080/html/HongMiNote.html
    @Path("/heatmap/distribution/overall/{url:.+}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSemanticDistributionByURL(@PathParam("url") String url){
        return heatmapService.getSemanticsDistribution(url).toString();
    }

    //热点页面某种事件主要区域分布  //223.3.68.141:8080/html/HongMiNote.html
    @Path("/heatmap/distribution/{event}/{url:.+}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String  getSemanticDistributionByEvent(@PathParam("url") String url, @PathParam("event") String event){

        return heatmapService.getSemanticsDistributionByEvent(url, event).toString();
    }

////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    //连接操作，根据传回的ip,url,datetime（服务器端的url的时间），获得一个URL的所有语义分布
    @Path("/heatmap/distribution3/{ip}/{dateTime}/{url:.+}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSemanticsDistribution(@PathParam("ip") String ip, @PathParam("dateTime") String dateTime, @PathParam("url") String url){
        System.out.println(heatmapService.getSemanticsDistribution(ip, url, dateTime).toString());
        System.out.println("HERE");
        return heatmapService.getSemanticsDistribution(ip, url, dateTime).toString();

    }

    //连接操作，根据传回的ip,url,datetime（服务器端的url的时间）,具体一个事件，获得一个URL的所有语义分布
    @Path("/heatmap/distribution4/{event}/{ip}/{dateTime}/{url:.+}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSemanticsDistributionByEvent(@PathParam("ip") String ip, @PathParam("dateTime") String dateTime, @PathParam("url") String url, @PathParam("event") String event){
        return heatmapService.getSemanticsDistributionByEvent(ip, url, dateTime, event).toString();
    }
    //项被改变的次数
    @Path("/form/nums/change")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFormChangeNums(){
        return customerSourceService.getChangeNums().toString();
    }
    //项被留空的次数
    @Path("/form/nums/empty")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFormNotWriteNums(){
        return customerSourceService.getNotWriteNums().toString();
    }

    //平均每项花费时间（s）
    @Path("/form/avgtime/cost")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFromAvgTime(){
        return customerSourceService.getAvgTime().toString();
    }

    //未来细化的地方，可以传回url,时间，进一步限定结果集






    //表单分析数据读取，统计各个项的情况






}
