package graphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jstar
 */
public class BasicGraph implements Graph {

    protected int nextNodeNo = 0;
    protected HashMap<Integer, HashSet<Edge>> connectLists = new HashMap<>();
    protected HashMap<Integer, String> nodeLabels = new HashMap<>();

    private double minEdgeWeight, maxEdgeWeight;

    /**
     * @return the number of Nodes
     */
    @Override
    public int getNumNodes() {
        return connectLists == null ? 0 : connectLists.size();
    }

    /**
     * @return numbers of Nodes
     */
    @Override
    public Set<Integer> getNodeNumbers() {
        return connectLists.keySet();
    }

    /**
     * @return all egdes
     */
    @Override
    public Set<Edge> getAllEdges() {
        Set<Edge> all = new HashSet<>();
        for (Integer n : connectLists.keySet()) {
            all.addAll(connectLists.get(n));
        }
        return all;
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
        if (minEdgeWeight == 0.0 && maxEdgeWeight == 0.0) {
            updateEdgesWeights();
        }
        return minEdgeWeight;
    }

    /**
     * @return the maximum of Edges' weights
     */
    @Override
    public double getMaxEdgeWeight() {
        if (minEdgeWeight == 0.0 && maxEdgeWeight == 0.0) {
            updateEdgesWeights();
        }
        return maxEdgeWeight;
    }

    protected void updateEdgesWeights() {
        minEdgeWeight = Double.POSITIVE_INFINITY;
        maxEdgeWeight = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < nextNodeNo; i++) {
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
        if (minEdgeWeight == Double.POSITIVE_INFINITY && maxEdgeWeight == Double.NEGATIVE_INFINITY) {
            minEdgeWeight = maxEdgeWeight = 0.0;
        }
    }

    /**
     * @param n - node number
     * @return the connectLists
     */
    @Override
    public HashSet<Edge> getConnectionsList(int n) {
        HashSet<Edge> s = connectLists == null ? null : connectLists.get(n);
        return s == null ? new HashSet<>() : s;
    }

    @Override
    public void save(PrintWriter pw) throws IOException {
        if (connectLists == null) {
            pw.println("0");
        } else {
            pw.println(nextNodeNo);
            for (int i = 0; i < nextNodeNo; i++) {
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
            nextNodeNo = Integer.parseInt(words[0]);
            connectLists.clear();
            for (int i = 0; i < nextNodeNo; i++) {
                HashSet<Edge> edges = new HashSet<>();
                words = br.readLine().split("[\\s:]*");
                for (int j = 0; j < words.length; j += 2) {
                    edges.add(new Edge(i, Integer.parseInt(words[j]), Double.parseDouble(words[j + 1])));
                }
                connectLists.put(i, edges);
            }
            updateEdgesWeights();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException("GridGraph can not read graph: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        String s = new String(connectLists.size() + "");
        for (Integer i : connectLists.keySet()) {
            s += "\n\t" + i + ":";
            for (Edge e : connectLists.get(i)) {
                s += " " + e;
            }
        }
        return s;
    }
}
