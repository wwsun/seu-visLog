package entity;


import java.util.*;

/**
 * Created by Administrator on 2015/3/16.
 */
public class SankeyGraph extends Graph {

    //    ArrayList<URLNode> nodes;
//    ArrayList<StreamEdge> links;
    public SankeyGraph(List<URLNode> nodes, List<StreamEdge> links) {
        super(nodes, links);
    }

//    public SankeyGraph(List<URLNode> nodes, List<StreamEdge> links){
//        this.nodes=(ArrayList<URLNode>)nodes;
//        this.links=(ArrayList<StreamEdge>)links;
//    }

    /**
<<<<<<< HEAD
     * ���ߵ�valueֵ���й���
=======
     * 按边的value值进行过滤
>>>>>>> seuvislogwws/master
     *
     * @param filter
     * @return
     */
    public SankeyGraph FilterByEdgeValue(double filter) {
        Set<Integer> nodeset = new HashSet<Integer>();
        Iterator<? extends Edge> iterator_links = this.links.iterator();
        while (iterator_links.hasNext()) {
            Edge edge = iterator_links.next();
            double value = ((StreamEdge) edge).getValue();
            if (value < filter)
                iterator_links.remove();  //�Ƴ��

            else {
                nodeset.add(edge.getSource());
                nodeset.add(edge.getTarget());
            }
        }
        Iterator<? extends Node> iterator_nodes = this.nodes.iterator();
        while (iterator_nodes.hasNext()) {
            Node node = iterator_nodes.next();

            if (!nodeset.contains(node.getName())) {  //����nodes��
                iterator_nodes.remove();
            }
        }
        // System.out.println( "���˺�nodes�Ĵ�С�� "+this.nodes.size());
        //�����˹���json����ٴΰ�node��name��0��ʼ�ź�
        int index = 0;
        for (Node n : this.nodes) {
            int old_name = n.getName();
            int new_name = index++;
            n.setName(new_name);

            for (Edge e : this.links) {
                if (e.getSource() == old_name)
                    e.setSource(new_name);
                if (e.getTarget() == old_name)
                    e.setTarget(new_name);
            }
        }
        return this;
    }

    /**
<<<<<<< HEAD
     * topk ��½ҳ
=======
     * topk 着陆页
>>>>>>> seuvislogwws/master
     *
     * @param topK
     * @return
     */
    public List<URLNode> topKLandPage(int topK) {
        List<URLNode> mainland_nodes = new ArrayList<URLNode>();
        List<? extends Node> nodes = this.getNodes();
        for (Node node : nodes) {
            URLNode n = (URLNode) node;
            if (n.getDepth() == 0) {
//                System.out.println(n.getIn_degree());
                for (URLNode no : mainland_nodes) {
//                    System.out.print(no.getIn_degree()+"  ");
                }
                if (mainland_nodes.size() < topK) {
                    mainland_nodes.add(n);
                } else {
                    URLNode minnode = getMinNode(mainland_nodes, "in_degree");
//                    System.out.print("[" + minnode.getIn_degree() + "]");
                    if (minnode.getIn_degree() < n.getIn_degree()) {
                        mainland_nodes.remove(minnode);
                        mainland_nodes.add(n);
                    }
                }
//                System.out.println();
            }
        }
        return mainland_nodes;
    }

    /**
<<<<<<< HEAD
     * topK ���ҳ
=======
     * topK 跳出页
>>>>>>> seuvislogwws/master
     */
    public List<URLNode> topKDropPage(int k) {
        List<URLNode> highdrop_nodes = new ArrayList<URLNode>();
        List<? extends Node> nodes = this.getNodes();
        //���ҵ����нڵ��indegree����λ��

        double middle = getMiddle(nodes);
        for (Node node : nodes) {
            URLNode n = (URLNode) node;
            if (n.getIn_degree() > middle) {
                if (highdrop_nodes.size() < k) {
                    highdrop_nodes.add(n);
                } else {
                    URLNode minNode = getMinNode(highdrop_nodes, "drop_percent");
                    if (minNode.getDrop_per() < n.getDrop_per()) {
                        highdrop_nodes.remove(minNode);
                        highdrop_nodes.add(n);
                    }
                }
            }
        }
        return highdrop_nodes;
    }

    /**
<<<<<<< HEAD
     * ȡ��nodes��ĳ��ָ����С��node
=======
     * 取得nodes中某个指标最小的node
>>>>>>> seuvislogwws/master
     *
     * @param nodes
     * @param param
     * @return
     */
    public URLNode getMinNode(List<URLNode> nodes, String param) {
        URLNode node = nodes.get(0);
        for (int i = 1; i < nodes.size(); i++) {
            double quota1 = 0;
            double quota2 = 0;
            if (param.equals("in_degree")) {
                quota1 = node.getIn_degree();
                quota2 = nodes.get(i).getIn_degree();
            }
            if (param.equals("drop_percent")) {
                quota1 = node.getDrop_per();
                quota2 = nodes.get(i).getDrop_per();
            }
            if (quota1 > quota2)
                node = nodes.get(i);
        }
        return node;
    }

    /**
<<<<<<< HEAD
     * �õ�nodes��List��indegree����λ��
=======
     * 得到nodes的List的indegree的中位数
>>>>>>> seuvislogwws/master
     */
    public double getMiddle(List<? extends Node> nodes) {
        double[] indegree = new double[nodes.size()];
        int i = 0;
        for (Node node : nodes) {
            URLNode n = (URLNode) node;
            indegree[i] = n.getIn_degree();
            i++;
        }
        double middle = quickSelect(indegree, 0, indegree.length - 1, indegree.length / 2);
        return middle;

    }

    /**
<<<<<<< HEAD
     * �����������ҵ�k����
=======
     * 下面两个是找第k大数
>>>>>>> seuvislogwws/master
     *
     * @param a
     * @param low
     * @param high
     * @return
     */
    public static int partition(double[] a, int low, int high) {
        double key = a[low];
        while (low < high) {
            while (low < high && a[high] >= key)
                high--;
            a[low] = a[high];
            while (low < high && a[low] <= key)
                low++;
            a[high] = a[low];
        }
        a[low] = key;
        return low;
    }

    public static double quickSelect(double[] a, int low, int high, int k) {

        if (low == high)
            return a[low];
        int keyPos = partition(a, low, high);

        int num = keyPos - low + 1;
        if (num == k)
            return a[keyPos];
        else if (k < num)
            return quickSelect(a, low, keyPos - 1, k);
        else
            return quickSelect(a, keyPos + 1, high, k - num);

    }
}
