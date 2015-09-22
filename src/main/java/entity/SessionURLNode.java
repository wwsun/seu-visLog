package entity;

/**
 * Created by Administrator on 2015/3/16.
 */
public class SessionURLNode extends Node {
    //Integer name 在父类里
    protected String url;
    protected String datetime;



    protected String ip;

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUrl() {
        return url;
    }

    public SessionURLNode(){}

    public SessionURLNode(Integer name, String url){
        super(name);
        this.url=url;
    }

    public void setUrl(String url){
        this.url=url;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
}
