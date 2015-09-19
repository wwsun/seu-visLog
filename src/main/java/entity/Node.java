package entity;

/**
 * Created by Administrator on 2015/3/16.
 */
public abstract class Node implements Comparable<Node>{
    protected Integer name;

    public Node(){}

    public void setName(Integer name) {
        this.name = name;
    }

    public Node(int name){
        this.name=name;
    }

    @Override
    public int compareTo(Node node) {
        return this.name.compareTo(node.name);
    }

    public int getName(){
        return name;
    }


}
