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
public class DirGridGraph implements Graph {

    private int numColumns;
    private int numRows;
    private HashMap<Integer, HashSet<Edge>> connectLists;
    private static final Random rand = new Random();

    public DirGridGraph(int nC, int nR, double wMin, double wMax, double avgEdgesPerNode) {
        numColumns = nC;
        numRows = nR;
        int nMax = numColumns * numRows;
        double dW = wMax - wMin;
        connectLists = new HashMap<>();
        for (int c = 0; c < numColumns; c++) {
            for (int r = 0; r < numRows; r++) {
                int nn = c * numRows + r;
                connectLists.put(nn, new HashSet<>());
                HashSet<Edge> list = connectLists.get(nn);
                if (nn >= numRows && rand.nextDouble() < avgEdgesPerNode / 4) {
                    list.add(new Edge(nn, nn - getNumRows(), wMin + dW * rand.nextDouble()));
                }
                if (nn < nMax - numRows && rand.nextDouble() < avgEdgesPerNode / 4) {
                    list.add(new Edge(nn, nn + getNumRows(), wMin + dW * rand.nextDouble()));
                }
                if (r > 0 && rand.nextDouble() < avgEdgesPerNode / 4) {
                    list.add(new Edge(nn, nn - 1, wMin + dW * rand.nextDouble()));
                }
                if (r < numRows - 1 && rand.nextDouble() < avgEdgesPerNode / 4) {
                    list.add(new Edge(nn, nn + 1, wMin + dW * rand.nextDouble()));
                }
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
        return (int) (n / numRows);
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
