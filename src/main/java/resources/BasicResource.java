package resources;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2015/3/30.
 */
public class BasicResource {
    private String fileName="../webapps/vislog/DBConf.properties";
    protected String uri;
    protected String db;

    public BasicResource(String URI,String DB){
        Properties p = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(fileName));
            p.load(in);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        uri=p.getProperty(URI);
        db=p.getProperty(DB);
    }
}
