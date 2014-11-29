package me.wwsun;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static spark.Spark.setPort;
import static spark.Spark.staticFileLocation;

/**
 * Created by Weiwei on 11/24/2014.
 */
public class SiteController {

    private final Configuration cfg;
    private final InboundDAO inboundDAO;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new SiteController("mongodb://223.3.75.101:27017");
        } else {
            new SiteController(args[0]);
        }
    }

    public SiteController(String mongoURIString) throws IOException {
        //init of mongodb
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));
        final DB siteDatabase = mongoClient.getDB("sample");

        inboundDAO = new InboundDAO(siteDatabase);

        //init of freemarker
        cfg = createFreemarkerConfiguration();
        setPort(8082);
        staticFileLocation("/public");
        initializeRoutes();
    }

    private void initializeRoutes() throws IOException {
        // this is the blog home page
        Spark.get(new FreemarkerBasedRoute("/", "index.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                String inbound = inboundDAO.getInboundDataAsJSON();
                System.out.println((inbound.length()));

//                if(Paths.get("outsite.json") == null) {
//                    inbound = inboundDAO.getInboundDataAsJSON();
//                    System.out.println("generate data successfully!");
//                }

                SimpleHash root = new SimpleHash();
                root.put("title","Inbound Flow Overview");

                if(inbound == null) {
                    root.put("inbound", "It seems that nothing returned!");
                } else {
                    root.put("inbound", inbound);
                }

                template.process(root, writer);
            }
        });

        Spark.get(new FreemarkerBasedRoute("/inbound","tables.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                SimpleHash root = new SimpleHash();
                root.put("title","Inbound Data Table");
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
