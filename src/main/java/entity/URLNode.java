package entity;

/**
 * Created by Administrator on 2015/3/16.
 */
public class URLNode extends Node {
    //Integer name 在父类里
    protected String url;
    protected String semantics="";
    protected double out_degree;
    protected double in_degree;
    protected double drop_per;
    protected String datetime;
    protected int depth;

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUrl() {
        return url;
    }
    public double getDrop_per() {
        return drop_per;
    }

    public void setDrop_per(double drop_per) {
        this.drop_per = drop_per;
    }

    public URLNode(){}

    public URLNode(Integer name,String url){
        super(name);
        this.url=url;
    }

    public void setSemantics(String semantics){
        this.semantics=semantics;
    }

    public void setIn_degree(double in){
        in_degree=in;
    }

    public void setOut_degree(double out){
        out_degree=out;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public double getIn_degree() {
        return in_degree;
    }

    public double getOut_degree() {
        return out_degree;
    }

    public String getSemantics() {
        return semantics;
    }

    @Override
    public String toString() {
        return "url:" + url + "," + "in_degree:" + in_degree + "," + "out_degree:" + out_degree;
    }
}
