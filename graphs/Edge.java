package graphs;

/**
 *
 * @author jstar
 */
public class Edge {

    private int nodeA;
    private int nodeB;
    private double weight;

    public Edge(int nA, int nB, double w) {
        nodeA = nA;
        nodeB = nB;
        weight = w;
    }

    /**
     * @return the nodeA
     */
    public int getNodeA() {
        return nodeA;
    }

    /**
     * @param nodeA the nodeA to set
     */
    public void setNodeA(int nodeA) {
        this.nodeA = nodeA;
    }

    /**
     * @return the nodeB
     */
    public int getNodeB() {
        return nodeB;
    }

    /**
     * @param nodeB the nodeB to set
     */
    public void setNodeB(int nodeB) {
        this.nodeB = nodeB;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

}
