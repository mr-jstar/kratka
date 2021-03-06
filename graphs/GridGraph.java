package graphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author jstar
 */
public class GridGraph extends BasicGraph {

    private int numColumns;
    private int numRows;
    private static final Random rand = new Random();

    private double minEdgeWeight, maxEdgeWeight;

    public GridGraph() {
        super();
        numColumns = numRows = 0;
    }

    public GridGraph(int nC, int nR, double wMin, double wMax, double avgEdgesPerNode) {
        super();
        numColumns = nC;
        numRows = nR;
        nextNodeNo = numColumns * numRows;
        int nMax = numColumns * numRows;
        double dW = wMax - wMin;
        for (int c = 0; c < numColumns; c++) {
            for (int r = 0; r < numRows; r++) {
                int nn = c * numRows + r;
                connectLists.put(nn, new HashSet<>());
            }
        }
        for (int c = 0; c < numColumns; c++) {
            for (int r = 1; r < numRows; r++) {
                int n2 = c * numRows + r;
                int n1 = n2 - 1;
                HashSet<Edge> l1 = connectLists.get(n1);
                HashSet<Edge> l2 = connectLists.get(n2);
                double w12 = wMin + dW * rand.nextDouble();
                if (rand.nextDouble() < avgEdgesPerNode / 4) {
                    l1.add(new Edge(n1, n2, w12));
                    l2.add(new Edge(n2, n1, w12));
                }
            }
        }
        for (int c = 1; c < numColumns; c++) {
            for (int r = 0; r < numRows; r++) {
                int n2 = c * numRows + r;
                int n1 = n2 - numRows;
                HashSet<Edge> l1 = connectLists.get(n1);
                HashSet<Edge> l2 = connectLists.get(n2);
                double w12 = wMin + dW * rand.nextDouble();
                if (rand.nextDouble() < avgEdgesPerNode / 4) {
                    l1.add(new Edge(n1, n2, w12));
                    l2.add(new Edge(n2, n1, w12));
                }
            }
        }
        updateEdgesWeights();
    }

    public GridGraph(int nC, int nR, Graph toCopy ) {
        super();
        numColumns = nC;
        numRows = nR;
        nextNodeNo = numColumns * numRows;
        int nMax = numColumns * numRows;
        for (int c = 0; c < numColumns; c++) {
            for (int r = 0; r < numRows; r++) {
                int nn = c * numRows + r;
                connectLists.put(nn, new HashSet<>(toCopy.getConnectionsList(nn)));
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

    @Override
    public void save(PrintWriter pw) throws IOException {
        pw.println(numColumns + " " + numRows);
        for (int i = 0; i < numColumns * numRows; i++) {
            HashSet<Edge> edges = connectLists.get(i);
            pw.print("\t");
            if (edges != null) {
                for (Edge e : edges) {
                    pw.print(" " + e.getNodeB() + " :" + e.getWeight() + " ");

                }
            }
            pw.println();
        }
        pw.close();
    }

    @Override
    public void read(Reader r) throws IOException {
        try {
            BufferedReader br = new BufferedReader(r);
            String[] words = br.readLine().trim().split("\\s+");
            numColumns = Integer.parseInt(words[0]);
            numRows = Integer.parseInt(words[1]);
            nextNodeNo = numColumns * numRows;
            connectLists.clear();
            //System.out.println(numColumns+" "+numRows);
            for (int i = 0; i < numColumns * numRows; i++) {
                HashSet<Edge> edges = new HashSet<>();
                words = br.readLine().trim().split("[\\s:]+");
                //System.out.println(i+":"+words.length);
                for (int j = 0; j+1 < words.length; j += 2) {
                    Edge e = new Edge(i, Integer.parseInt(words[j]), Double.parseDouble(words[j + 1]));
                    edges.add(e);
                }
                connectLists.put(i, edges);
            }
            updateEdgesWeights();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException("GridGraph can not read graph: " + e);
        }
    }

}
