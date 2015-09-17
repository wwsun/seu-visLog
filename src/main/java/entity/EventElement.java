package entity;

/**
 * Created by Administrator on 2015/5/17.
 */
public class EventElement {
    private String name;
    private int click;
    private int mousemove;
    private int scroll;
    private String semantics;

    public EventElement(String name,String semantics) {
        this.name=name;
        this.click=0;
        this.mousemove=0;
        this.scroll=0;
        this.semantics=semantics;

    }

    public String getSemantics() {
        return semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public void addOneClick(){
        this.click++;
    }

    public int getMousemove() {
        return mousemove;
    }

    public void setMousemove(int mousemove) {
        this.mousemove = mousemove;
    }

    public void addOneMouseMove(){
        this.mousemove++;
    }


    public int getScroll() {
        return scroll;
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
    }

    public void addOneScroll(){
        this.scroll++;
    }

    public String toString(){
        return "Name:"+name+" Semantics:"+semantics+" "+click+" "+mousemove+" "+scroll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
