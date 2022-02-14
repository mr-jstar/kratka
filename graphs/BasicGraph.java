package graphs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author jstar
 */
public class BasicGraph implements Graph {

    private int numColumns;
    private int numRows;
    private HashMap<Integer, HashSet<Edge>> connectLists;
    private static final Random rand = new Random();

    public BasicGraph(int nC, int nR, double wMin, double wMax, double avgEdgesPerNode ) {
        numColumns = nC;
        numRows = nR;
        int nMax = numColumns * numRows;
        double dW = wMax - wMin;
        connectLists = new HashMap<>();
        for (int c = 0; c < numColumns; c++) {
            for (int r = 0; r < numRows; r++) {
                int nn = c * numRows + r;
                connectLists.put(nn, new HashSet<>());
            }
        }
        for (int c = 1; c < numColumns; c++) {
            for (int r = 1; r < numRows; r++) {
                int n2 = c * numRows + r;
                int n1 = n2 - 1;
                int n0 = n1 - numRows;
                int n3 = n0 + 1;
                HashSet<Edge> l0 = connectLists.get(n0);
                HashSet<Edge> l1 = connectLists.get(n1);
                HashSet<Edge> l2 = connectLists.get(n2);
                HashSet<Edge> l3 = connectLists.get(n3);
                double w01 = wMin + dW * rand.nextDouble();
                double w12 = wMin + dW * rand.nextDouble();
                double w23 = wMin + dW * rand.nextDouble();
                double w30 = wMin + dW * rand.nextDouble();
                l0.add( new Edge(n0,n1,w01));
                l1.add( new Edge(n1,n0,w01));
                l1.add( new Edge(n1,n2,w12));
                l2.add( new Edge(n2,n1,w12));
                l2.add( new Edge(n2,n3,w23));
                l3.add( new Edge(n3,n2,w23));
                l3.add( new Edge(n3,n0,w30));
                l0.add( new Edge(n0,n3,w30));
            }
        }
    }

    /**
     * @return the node number given row and column
     */
    public int nodeNum(int r, int c) {
        return c * numRows + r;
    }

    /**
     * @return the row number given node number
     */
    public int row(int n) {
        return n % numRows;
    }

    /**
     * @return the column number given node number
     */
    public int col(int n) {
        return (int)(n / numRows);
    }

    /**
     * @return the number of Nodes
     */
    @Override
    public int getNumNodes() {
        return numColumns * numRows;
    }

    /**
     * @return the label of given node
     */
    @Override
    public String getNodeLabel(int n) {
        if (n >= 0 && n < numColumns * numRows) {
            return "" + n;
        } else {
            return null;
        }
    }

    /**
     * @return the numColumns
     */
    public int getNumColumns() {
        return numColumns;
    }

    /**
     * @return the numRows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * @param n - node number
     * @return the connectLists
     */
    @Override
    public HashSet<Edge> getConnectionsList(int n) {
        return connectLists.get(n);
    }

    @Override
    public void save(String path) throws IOException {
        PrintWriter p = new PrintWriter(new File(path));
        p.println(numColumns + " " + numRows);
        for (int i = 0; i < numColumns * numRows; i++) {
            HashSet<Edge> edges = connectLists.get(i);
            p.print("\t");
            if (edges != null) {
                for (Edge e : edges) {
                    p.print(" " + e.getNodeB() + " :" + e.getWeight() + " ");

                }
            }
            p.println();
        }
        p.close();
    }
}
