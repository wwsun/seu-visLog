package resources;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import service.OverviewService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.UnknownHostException;

/**
 * 整体数据概览
 * 数据主要来自 日志
 *
 */
@Path("/overview")
public class OverviewDataResource extends BasicResource {
    final MongoClient mongoClient;
    final DB siteDatabase;
    final OverviewService overviewService;

    public OverviewDataResource() throws UnknownHostException {
        super("Mongo_URI", "Mongo_DB");
        mongoClient = new MongoClient(new MongoClientURI(this.uri));
        siteDatabase = mongoClient.getDB(this.db);
        overviewService = new OverviewService(siteDatabase);
    }

    //某天的24小时会话分布情况
    @Path("/distribution/{date}") //2014-08-10
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getDistribution(@PathParam("date") String date) {
        return overviewService.getSessionDistributionByDate(date).toString();
    }

    //某天的会话总数，出错请求书，询盘会话占比  //2014-08-10
    @Path("/status/{date}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getOverviewStatus(@PathParam("date") String date) {
        return overviewService.getCountErrorsInquiryByDate(date).toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    //一天的24小时点击分布情况（和上面的会话情况类似）
    @Path("/status/clicks/{date}")//2014-08-10
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getClicksByDay(@PathParam("date") String date) {
        return overviewService.getClicksTrendsByDate(date).toString();
    }

    //一天的吞吐量(返回时包括单位 MB/GB)
    @Path("/status/thruput/{date}")//2014-08-10
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getThruputByDay(@PathParam("date") String date) {
        return overviewService.getThruputOneDayByDate(date).toString();
    }

    //一天的StatusList
    @Path("/status/list/{date}")//2014-08-10
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatusListByDay(@PathParam("date") String date) {
        return overviewService.getStatusListOneDayByDate(date).toString();
    }


    //主要搜索引擎来源
    @Path("/sessions/sources/se")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSearchEngineContribution() {
        return overviewService.getTopSearchEngines(10).toString();
    }

    //主要国家来源
    @Path("/sessions/sources/countries")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTopCountriesFlowContribution() {
        return overviewService.getTopCountriesFlow(10).toString();
    }

    //最频繁访问的页面类别
    @Path("/sessions/frequent/categories")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFrequentVisitedCategory() {
        return overviewService.getTopCategories(7).toString();
    }

    //频繁类别下面的top页面
    @Path("/sessions/frequent/{categoryName}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFrequentVisitedPagesByCategory(@PathParam("categoryName") String categoryName) {
        return overviewService.getTopPagesByCategory(categoryName, 5).toString();
    }

    //最频繁访问的页面
    @Path("/sessions/frequent/pages")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFrequentVisitedPages() {
        return overviewService.getFrequentVisitedPages(20).toString();
    }

    //主要着陆页面类别
    @Path("/sessions/landings/categories")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMainLandingCategories() {

        return overviewService.getMainLandingCategories(10).toString();
    }

    //主要跳出页面类别
    @Path("/sessions/dropoff/categories")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMainDropOffCategories() {

        return overviewService.getMainDropOffCategories(10).toString();
    }

    //搜索引擎来源(0)，直接来源(1)和推荐来源(2)各占session数
    @Path("/sessions/sourcedetail")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSourceSessionNums() {
        return overviewService.getSourceSessionNums().toString();
    }


    //下面暂时不做，需要细分时再做
    //////////////////////////////////////////////////////////////////////////////////////
    //主要着陆页
    @Path("/sessions/landings/pages")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMainLandingPages() {
        return overviewService.getMainLandingPages(10).toString();
    }

    //某一着陆页面(log表)类别下的top页面
    @Path("/sessions/landings/{categoryName}")   //目录搜索产品
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTopLandingPagesByCategory(@PathParam("categoryName") String categoryName) {
        return overviewService.getTopLandingPagesByCategory(categoryName, 5).toString();
    }

    //是主要来源页source
    //return : url ,dup
    @Path("/sessions/sources")   //
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMainSourcePages() {
        return overviewService.getTopSourcePages(10).toString();
    }

    //根据某一来源页，获得其去向的类别top
    //return : category,dup
    @Path("/sessions/sources/categories/{url}")   //
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMainCategoriesBySourcePage(@PathParam("url") String url) {
        return overviewService.getTopCategoriesBySourcePage(url, 10).toString();
    }

    //根据某一来源页，获得其去向的页面top
    @Path("/sessions/sources/pages/{url}")   //
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMainPagesBySourcePage(@PathParam("url") String url) {
        return overviewService.getTopPagesBySourcePage(url, 10).toString();
    }

    //某一跳出页面(leave表)类别下的top页面
    @Path("/sessions/dropoff/{categoryName}")   //展示厅首页
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTopDropOffPagesByCategory(@PathParam("categoryName") String categoryName) {
        return overviewService.getTopDropOffPagesByCategory(categoryName, 5).toString();
    }
///////////////////////////////////////////////////////////////////////////////////////////

}
