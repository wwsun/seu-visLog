package me.wwsun;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import me.wwsun.service.FlowAnalysisService;
import me.wwsun.service.LinkAnalysisService;
import me.wwsun.service.OverviewService;
import me.wwsun.util.FileUtil;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static spark.Spark.*;

/**
 * Created by Weiwei on 11/24/2014.
 */
public class SiteController {

    private final Configuration cfg;
    private final OverviewService overviewService;
    private final LinkAnalysisService linkAnalysisService;
    private final FlowAnalysisService flowAnalysisService;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new SiteController("mongodb://223.3.80.243:27017");
        } else {
            new SiteController(args[0]);
        }
    }

    public SiteController(String mongoURIString) throws IOException {
        //init of mongodb
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));
        final DB siteDatabase = mongoClient.getDB("sample");

        overviewService = new OverviewService(siteDatabase);
        linkAnalysisService = new LinkAnalysisService(siteDatabase);
        flowAnalysisService = new FlowAnalysisService(siteDatabase);

        //init of freemarker
        cfg = createFreemarkerConfiguration();
        setPort(8082);
        staticFileLocation("/public");
        initializeRoutes();
    }

    private void initializeRoutes() throws IOException {
        //Homepage:
        get(new FreemarkerBasedRoute("/", "index.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                SimpleHash root = new SimpleHash();
                root.put("title", "Website Overview");
                template.process(root, writer);
            }
        });

        //Page: link analysis for whole site
        get(new FreemarkerBasedRoute("/link", "link.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                SimpleHash root = new SimpleHash();
                root.put("title", "Link Analysis");
                template.process(root, writer);
            }
        });

        //Page: flow analysis for whole site
        get(new FreemarkerBasedRoute("/flow", "flow.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                SimpleHash root = new SimpleHash();
                root.put("title", "Flow Analysis");
                template.process(root, writer);
            }
        });

        //todo: Post: update dataset
        get(new FreemarkerBasedRoute("/update", "index.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                DBObject overview = overviewService.getOverviewData("2014-10-22");
                FileUtil.outputAsJSON(overview, "site-overview");

                SimpleHash root = new SimpleHash();
                System.out.println("update: overview");
                root.put("title", "Website Overview (Update)");
                template.process(root, writer);
            }
        });

        get(new FreemarkerBasedRoute("/update/link", "link.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                DBObject overviewGraph = linkAnalysisService.getOverviewGraph(2, 100);//type=2, threshold=100
                FileUtil.outputAsJSON(overviewGraph, "overview-graph");

                SimpleHash root = new SimpleHash();
                root.put("title", "Link Analysis (Update)");
                template.process(root, writer);
            }
        });

        get(new FreemarkerBasedRoute("/update/flow", "flow.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                flowAnalysisService.getOverviewFlowMap();
                SimpleHash root = new SimpleHash();
                root.put("title", "Flow Analysis (Update)");
                template.process(root, writer);
            }
        });
    }

    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(SiteController.class, "/freemarker");
        return retVal;
    }


    abstract class FreemarkerBasedRoute extends Route {
        final Template template;

        /**
         * Constructor
         *
         * @param path The route path which is used for matching. (e.g. /hello, users/:name)
         */
        protected FreemarkerBasedRoute(final String path, final String templateName) throws IOException {
            super(path);
            template = cfg.getTemplate(templateName);
        }

        @Override
        public Object handle(Request request, Response response) {
            StringWriter writer = new StringWriter();
            try {
                doHandle(request, response, writer);
            } catch (Exception e) {
                e.printStackTrace();
                response.redirect("/internal_error");
            }
            return writer;
        }

        protected abstract void doHandle(final Request request, final Response response, final Writer writer)
                throws IOException, TemplateException;

    }

}
