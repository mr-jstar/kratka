package graphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author jstar
 */
public class BasicGraph implements Graph, GraphBuilder {

    private int maxNodeNo = -1;
    private HashMap<Integer, HashSet<Edge>> connectLists = new HashMap<>();
    private HashMap<Integer, String> nodeLabels = new HashMap<>();

    private double minEdgeWeight, maxEdgeWeight;

    /**
     * @return the number of Nodes
     */
    @Override
    public int getNumNodes() {
        return connectLists == null ? 0 : connectLists.size();
    }

    /**
     * @return the label of given node
     */
    @Override
    public String getNodeLabel(int n) {
        if (connectLists != null && connectLists.containsKey(n)) {
            return nodeLabels.get(n);
        } else {
            return null;
        }
    }

    /**
     * @return the minimum of Edges' weights
     */
    @Override
    public double getMinEdgeWeight() {
        updateEdgeWeight();
        return minEdgeWeight;
    }

    /**
     * @return the maximum of Edges' weights
     */
    @Override
    public double getMaxEdgeWeight() {
        updateEdgeWeight();
        return maxEdgeWeight;
    }

    private void updateEdgeWeight() {
        minEdgeWeight = Double.POSITIVE_INFINITY;
        maxEdgeWeight = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < maxNodeNo; i++) {
            for (Edge e : connectLists.get(i)) {
                double w = e.getWeight();
                if (w < minEdgeWeight) {
                    minEdgeWeight = w;
                }
                if (w > maxEdgeWeight) {
                    maxEdgeWeight = w;
                }

            }
        }
    }

    /**
     * @param n - node number
     * @return the connectLists
     */
    @Override
    public HashSet<Edge> getConnectionsList(int n) {
        return connectLists == null ? null : connectLists.get(n);
    }

    @Override
    public void save(PrintWriter pw) throws IOException {
        if (connectLists == null) {
            pw.println("0");
        } else {
            pw.println(maxNodeNo);
            for (int i = 0; i < maxNodeNo; i++) {
                if (connectLists.containsKey(i)) {
                    HashSet<Edge> edges = connectLists.get(i);
                    pw.print("\t");
                    if (edges != null) {
                        for (Edge e : edges) {
                            pw.print(" " + e.getNodeB() + " :" + e.getWeight() + " ");

                        }
                    }
                    pw.println();
                } else {
                    pw.println("\t");
                }
            }
        }
        pw.close();
    }

    @Override
    public void read(Reader r) throws IOException {
        try {
            BufferedReader br = new BufferedReader(r);
            String[] words = br.readLine().split("\\s*");
            maxNodeNo = Integer.parseInt(words[0]);
            connectLists.clear();
            for (int i = 0; i < maxNodeNo; i++) {
                HashSet<Edge> edges = new HashSet<>();
                words = br.readLine().split("[\\s:]*");
                for (int j = 0; j < words.length; j += 2) {
                    edges.add(new Edge(i, Integer.parseInt(words[j]), Double.parseDouble(words[j + 1])));
                }
                connectLists.put(i, edges);
            }
        } catch (ArrayIndexOutOfBoundsException| NumberFormatException e) {
            throw new IOException("GridGraph can not read graph: " + e.getMessage());
        }
    }

    @Override
    public void addNode() {
        connectLists.put(++maxNodeNo, new HashSet<>());
        nodeLabels.put(maxNodeNo, "" + maxNodeNo);
    }

    @Override
    public void addNode(int number) {
        if (!connectLists.containsKey(number)) {
            connectLists.put(number, new HashSet<>());
            nodeLabels.put(number, "" + number);
            if (number > maxNodeNo) {
                maxNodeNo = number;
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
