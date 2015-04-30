package entity;

import java.util.List;

/**
 * Created by Administrator on 2015/4/1.
 */
public class SankeyCriteria {

    List<URLNode> highdrop;
    List<URLNode> mainland;

    public SankeyCriteria(List<URLNode> highdrop,List<URLNode> mainland) {
        this.highdrop = highdrop;
        this.mainland = mainland;
    }
}
