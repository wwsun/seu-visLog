package entity;


public class Edge {
    protected int source;
    protected int target;

    public Edge(int source,int target){
        this.source=source;
        this.target=target;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getSource(){
        return source;
    }

    public int getTarget(){
        return target;
    }
}
