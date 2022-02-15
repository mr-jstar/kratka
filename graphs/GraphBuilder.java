
package graphs;

/**
 *
 * @author jstar
 */
public interface GraphBuilder {

    public void addNode(); // with subsequent number

    public void addNode(int number);

    public void addEdge(int first, int second);  // weight == 1

    public void addEdge(int first, int second, double weight);

    public void setNodeLabel(int n, String label);

    public Graph getGraph();
}
