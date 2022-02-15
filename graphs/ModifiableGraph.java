package graphs;

import java.util.HashSet;

/**
 *
 * @author jstar
 */
public class ModifiableGraph extends BasicGraph implements GraphBuilder {


    @Override
    public void addNode() {
        connectLists.put(++nextNodeNo, new HashSet<>());
        nodeLabels.put(nextNodeNo, "" + nextNodeNo);
    }

    @Override
    public void addNode(int number) {
        if (!connectLists.containsKey(number)) {
            connectLists.put(number, new HashSet<>());
            nodeLabels.put(number, "" + number);
            if (number > nextNodeNo) {
                nextNodeNo = number;
            }
        }
    }

    @Override
    public void addEdge(int first, int second) {
        addEdge(first, second, 1.0);
    }

    @Override
    public void addEdge(int first, int second, double weight) {
        addNode(first);
        addNode(second);
        connectLists.get(first).add(new Edge(first, second, weight));
    }

    @Override
    public void setNodeLabel(int n, String label) {
        if (nodeLabels.containsKey(n)) {
            nodeLabels.remove(n);
        }
        nodeLabels.put(n, label);
    }

    @Override
    public Graph getGraph() {
        return this;
    }
}
