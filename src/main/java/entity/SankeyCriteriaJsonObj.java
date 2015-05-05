package entity;

import java.util.List;

/**
 * Created by Administrator on 2015/4/1.
 */
public class SankeyCriteriaJsonObj {
    List<URLNode> highdrop;
    List<URLNode> highdrop_per;
    List<URLNode> mainland;

    public SankeyCriteriaJsonObj(List<URLNode> highdrop, List<URLNode> mainland, List<URLNode> highdrop_per) {
        this.highdrop = highdrop;
        this.mainland = mainland;
        this.highdrop_per = highdrop_per;
    }
}
