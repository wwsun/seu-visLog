package seu.entity;

/**
 * Created by Weiwei on 10/13/2014.
 */
public class Node {

    private String name;
    private int weight;
    private String domain;
    private int group; // 0 - www.made-in-china.com

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
