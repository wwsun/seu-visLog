package entity;

/**
 * Created by Administrator on 2015/3/16.
 */
public class StreamEdge extends Edge{
    protected double value;
    protected String session;

    public StreamEdge(int source,int target,double value){
        super(source,target);
        this.value=value;
    }

    public double getValue(){
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {

        return "target:" + target + "," + "sorce:" + source + "," + "value" +value;
     }
}
