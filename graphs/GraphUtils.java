package graphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import java.util.Arrays;
import java.util.PriorityQueue;

import java.text.DecimalFormat;
import java.util.HashSet;

/**
 *
 * @author jstar
 */
public class GraphUtils {

    static final int WHITE = 0;
    static final int GRAY = 1;
    static final int BLACK = 2;

    public static String lastError = null;

    public static ModifiableGraph read(Reader r) throws IOException {
        try {
            ModifiableGraph g = new ModifiableGraph();
            BufferedReader br = new BufferedReader(r);
            String[] words = br.readLine().split("\\s*");
            int nNodes = Integer.parseInt(words[0]);
            for (int i = 0; i < nNodes; i++) {
                g.addNode(i);
                words = br.readLine().split("[\\s:]*");
                for (int j = 0; j < words.length; j += 2) {
                    g.addEdge(i, Integer.parseInt(words[j]), Double.parseDouble(words[j + 1]));
                }
            }
            br.close();
            r.close();
            return g;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException("GridGraph can not read graph: " + e.getMessage());
        }
    }

    public static boolean valid(Graph g, int startNode) {
        if (g == null || g.getNumNodes() < 1 || startNode < 0 || startNode >= g.getNumNodes()) {
            return false;
        }
        for (int currentNode = 0; currentNode < g.getNumNodes(); currentNode++) {
            for (Edge e : g.getConnectionsList(currentNode)) {
                if (e.getNodeA() != currentNode) {
                    lastError = "bfs: graph given as argument is not valid: edge starting from node " + currentNode + " has first node == " + e.getNodeA() + " instead of " + currentNode;
                    return false;
                }
            }
        }

        return true;
    }

    public static Graph prim(Graph g) {
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        ModifiableGraph mst = new ModifiableGraph();
        mst.addNode(0);
        for (Edge e : g.getConnectionsList(0)) {
            pq.add(e);
        }
        while (!pq.isEmpty() && mst.getNumNodes() < g.getNumNodes()) {
            //System.out.println( "Prim: |V.mst|="+mst.getNumNodes()+"  |PQ|="+pq.size());
            Edge se = pq.poll();
            int nA = se.getNodeA();
            int nB = se.getNodeB();
            if (mst.hasNode(nA) && mst.hasNode(nB)) {
                continue;
            } else {
                if (mst.hasNode(nA)) {
                    mst.addNode(nB);
                    mst.addEdge(nA, nB, se.getWeight());
                    for (Edge e : g.getConnectionsList(nB)) {
                        pq.add(e);
                    }
                } else {
                    mst.addNode(nA);
                    mst.addEdge(nB, nA, se.getWeight());
                    for (Edge e : g.getConnectionsList(nA)) {
                        pq.add(e);
                    }
                }
            }
        }
        return mst;
    }

    static class Forest { // set of grah-trees, very simple (not effective) implementation 

        private ModifiableGraph[] f;
        int n;

        public Forest(int size) {
            f = new ModifiableGraph[size];
            n = 0;
        }

        public void add(ModifiableGraph g) {
            f[n++] = g;
        }

        public int getTreeWithNode(int node) {
            //System.out.print( "Forest search for graph with " + node + " -> ");
            for (int i = 0; i < n; i++) {
                if (f[i].hasNode(node)) {
                    //System.out.println( i );
                    return i;
                }
            }
            return -1;
        }

        public void removeTree(int i) {
            f[i] = f[--n];
        }

        public int size() {
            return n;
        }

        public ModifiableGraph get(int i) {
            return f[i];
        }
    }

    public static Graph kruskal(Graph g) {
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        Forest forest = new Forest(g.getNumNodes());
        for (int i = 0; i < g.getNumNodes(); i++) {
            ModifiableGraph t = new ModifiableGraph();
            t.addNode(i);
            forest.add(t);
            for (Edge e : g.getConnectionsList(i)) {
                pq.add(e);
            }
        }

        while (!pq.isEmpty() && forest.size() > 1) {
            Edge se = pq.poll();

            int iA = forest.getTreeWithNode(se.getNodeA());
            int iB = forest.getTreeWithNode(se.getNodeB());

            //System.out.println("Edge " + se + "  iA=" + iA + "  iB=" + iB);
            if (iA != iB) {
                // add tB to tA
                ModifiableGraph tA = forest.get(iA);
                tA.addGraph(forest.get(iB));
                tA.addEdge(se);
                forest.removeTree(iB);
            }
        }

        return forest.get(0);
    }

    public static SingleSourceGraphPaths bfs(Graph g, int startNode) {
        if (g == null || g.getNumNodes() < 1 || startNode < 0 || startNode >= g.getNumNodes()) {
            return null;
        }
        int[] p = new int[g.getNumNodes()];
        double[] d = new double[g.getNumNodes()];
        java.util.Arrays.fill(d, -1);    // distance equal -1 marks node which is not connected to the start node
        java.util.Arrays.fill(p, -1);    //  same is valid for precedessor

        int[] c = new int[g.getNumNodes()];
        java.util.Arrays.fill(c, WHITE);

        c[startNode] = GRAY;
        d[startNode] = 0;   // made by Arrays.fill, repeated here for clarity
        p[startNode] = -1;  // made by Arrays.fill, repeated here for clarity
        java.util.Deque<Integer> fifo = new java.util.ArrayDeque<>();
        fifo.add(startNode);
        int currentNode;
        while (!fifo.isEmpty()) {
            currentNode = fifo.pop();
            for (Edge e : g.getConnectionsList(currentNode)) {
                int n = e.getNodeB();
                if (c[n] == WHITE) {
                    c[n] = GRAY;
                    p[n] = currentNode;
                    d[n] = d[currentNode] + 1;
                    fifo.add(n);
                }
            }
            c[currentNode] = BLACK;
        }

        return new SingleSourceGraphPaths(d, p);
    }

    public static SingleSourceGraphPaths dfs(Graph g) {
        if (g == null || g.getNumNodes() < 1) {
            return null;
        }
        int[] d = new int[g.getNumNodes()];
        int[] f = new int[g.getNumNodes()];
        int[] p = new int[g.getNumNodes()];
        java.util.Arrays.fill(d, -1);    // discovery "time"  -1 means "not visited"
        java.util.Arrays.fill(f, -1);    // finish "time"
        java.util.Arrays.fill(p, -1);    // parent

        int[] c = new int[g.getNumNodes()];
        java.util.Arrays.fill(c, WHITE);

        try {
            for (int n = 0; n < g.getNumNodes(); n++) {
                if (c[n] == WHITE) {
                    d[n] = 0;
                    dfs_visit(g, n, d, f, p, c, 0);
                }
            }
        } catch (StackOverflowError e) {
            throw new IllegalArgumentException("Recursive DFS: graph is to big/complicated");
        }

        return new SingleSourceGraphPaths(p, d, f);
    }

    private static void dfs_visit(Graph g, int currentNode, int[] d, int[] f, int[] p, int[] c, int time) {

        c[currentNode] = GRAY;
        d[currentNode] = time;
        for (Edge e : g.getConnectionsList(currentNode)) {
            int n = e.getNodeB();
            if (c[n] == WHITE) {
                p[n] = currentNode;
                dfs_visit(g, n, d, f, p, c, time + 1);
            }
        }
        c[currentNode] = BLACK;
        f[currentNode] = time + 1;
    }

    public static SingleSourceGraphPaths dfs_iterative(Graph g) {
        if (g == null || g.getNumNodes() < 1) {
            return null;
        }
        int[] d = new int[g.getNumNodes()];
        int[] f = new int[g.getNumNodes()];
        int[] p = new int[g.getNumNodes()];
        java.util.Arrays.fill(d, -1);    // discovery "time"  -1 means "not visited"
        java.util.Arrays.fill(f, -1);    // finish "time"
        java.util.Arrays.fill(p, -1);    // parent

        int[] c = new int[g.getNumNodes()];
        java.util.Arrays.fill(c, WHITE);

        int time = 0;
        java.util.Deque<Integer> stack = new java.util.ArrayDeque<>();
        for (int n = 0; n < g.getNumNodes(); n++) {
            if (c[n] == WHITE) {
                time = 0;
                c[n] = GRAY;
                d[n] = time++;
                stack.push(n);
                while (!stack.isEmpty()) {
                    int currentNode = stack.pop();
                    boolean isFinished = true;
                    for (Edge e : g.getConnectionsList(currentNode)) {
                        int neighbour = e.getNodeB();
                        if (c[neighbour] == WHITE) {
                            c[neighbour] = GRAY;
                            p[neighbour] = currentNode;
                            d[neighbour] = time++;
                            stack.push(neighbour);
                            isFinished = false;
                            break;
                        }
                    }
                    if (isFinished) {
                        c[currentNode] = BLACK;
                        f[currentNode] = time++;
                        if (p[currentNode] != -1) {
                            stack.push(p[currentNode]);
                        }
                    }
                }
            }
        }

        return new SingleSourceGraphPaths(p, d, f);

    }

// Self made priority queue for Dijkstra
    private static class HeapPQ {

        private int[] h;    // heap
        private int n;      // actual length of heap
        private double[] d; // distances of all nodes to the source
        private int[] pos;  // positions of all nodes to the source
        // pos is used to find the position of node x on the heap at O(1)

        HeapPQ(double[] d) {
            this.d = d;              // copy of the table used in Dijkstra algorithm
            pos = new int[d.length];
            Arrays.fill(pos, -1);    // none of the nodes is on the heap
            h = new int[d.length];
            n = 0;                   // initially the heap is empty
        }

        boolean isEmpty() {
            return n == 0;
        }

        void heapUp(int c) {
            while (c > 0) {
                int p = (c - 1) / 2;
                if (d[h[p]] > d[h[c]]) {
                    int tmp = h[p];
                    h[p] = h[c];
                    h[c] = tmp;
                    pos[h[p]] = p;
                    pos[h[c]] = c;
                    return;
                }
                c = p;
            }
        }

        void add(int i, double dst) {
            d[i] = dst;
            h[n++] = i;
            pos[i] = n - 1;
            heapUp(n - 1);
        }

        int poll() {
            if (n == 0) {
                throw new IllegalStateException("GraphUtils::dijkstra: trying to pop from empty priority queue!");
            }
            int ret = h[0];
            h[0] = h[--n];
            pos[ret] = -1;
            pos[h[0]] = 0;
            int p = 0;  // heap down
            int c = 2 * p + 1;
            while (c < n) {
                if (c + 1 < n && d[h[c + 1]] < d[h[c]]) {
                    c++;
                }
                if (d[h[p]] <= d[h[c]]) {
                    break;
                }
                int tmp = h[p];
                h[p] = h[c];
                h[c] = tmp;
                pos[h[p]] = p;
                pos[h[c]] = c;
                p = c;
                c = 2 * p + 1;
            }
            return ret;
        }

        void update(int x, double dst) {
            d[x] = dst;
            if (pos[x] == -1) { //  there was no x in heap yet
                h[n++] = x;
                pos[x] = n - 1;
            }
            //System.out.println(); print();
            heapUp(pos[x]);
            //print();
        }

        void print() {
            System.out.print("[");
            for (int i = 0; i < n; i++) {
                System.out.print(" (" + h[i] + ":" + d[h[i]] + ")");
            }
            System.out.println(" ]");
        }
    }

    public static SingleSourceGraphPaths dijkstra(Graph g, int startNode) {
        if (g == null || g.getNumNodes() < 1 || startNode < 0 || startNode >= g.getNumNodes()) {
            return null;
        }
        //System.out.println("Dijkstra, source=" + startNode);
        int[] p = new int[g.getNumNodes()];
        double[] d = new double[g.getNumNodes()];
        java.util.Arrays.fill(d, Double.POSITIVE_INFINITY);
        java.util.Arrays.fill(p, -1);

        p[startNode] = -1;  // made by Arrays.fill, repeated here for clarity
        HeapPQ queue = new HeapPQ(d);
        queue.add(startNode, 0.0);
        int currentNode;
        while (!queue.isEmpty()) {
            currentNode = queue.poll();
            //System.out.println("current: " + currentNode);
            for (Edge e : g.getConnectionsList(currentNode)) {
                int n = e.getNodeB();
                //System.out.print("\t" + n + ": ");
                if (d[n] > d[currentNode] + e.getWeight()) {
                    //System.out.print(d[n] + "->" + (d[currentNode] + e.getWeight()));
                    d[n] = d[currentNode] + e.getWeight();
                    queue.update(n, d[n]);
                    p[e.getNodeB()] = currentNode;
                }
                //System.out.println();
            }
        }

        return new SingleSourceGraphPaths(d, p);
    }

    public static SingleSourceGraphPaths bellmanFord(Graph g, int startNode) {
        if (g == null || g.getNumNodes() < 1 || startNode < 0 || startNode >= g.getNumNodes()) {
            return null;
        }
        //System.out.println("Dijkstra, source=" + startNode);
        int nn = g.getNumNodes();
        int[] p = new int[nn];
        double[] d = new double[nn];
        java.util.Arrays.fill(d, Double.POSITIVE_INFINITY);
        java.util.Arrays.fill(p, -1);

        p[startNode] = -1;  // made by Arrays.fill, repeated here for clarity
        d[startNode] = 0;
        Set<Edge> allEdges = g.getAllEdges();
        for (int i = 0; i < nn; i++) {
            for (Edge e : allEdges) {
                int nA = e.getNodeA();
                int nB = e.getNodeB();
                double w = e.getWeight();
                if (d[nA] > d[nB] + w) {
                    d[nA] = d[nB] + w;
                    p[nA] = nB;
                }
            }
        }

        return new SingleSourceGraphPaths(d, p);
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static void printArray(double[][] d) {
        int nn = d.length;
        for (int i = 0; i < nn; i++) {
            for (int j = 0; j < nn; j++) {
                System.out.print(" " + df.format(d[i][j]));
            }
            System.out.println();
        }
    }

    private static void printArray(int[][] d) {
        int nn = d.length;
        for (int i = 0; i < nn; i++) {
            for (int j = 0; j < nn; j++) {
                System.out.print(" " + d[i][j]);
            }
            System.out.println();
        }
    }

    public static AllGraphPaths floydWarshall(Graph g) {
        if (g == null || g.getNumNodes() < 1) {
            return null;
        }

        int nn = g.getNumNodes();
        int[][] p = new int[nn][nn];
        double[][] d = new double[nn][nn];
        for (int i = 0; i < nn; i++) {
            java.util.Arrays.fill(d[i], Double.POSITIVE_INFINITY);
            java.util.Arrays.fill(p[i], -1);
        }
        for (int i = 0; i < nn; i++) {
            d[i][i] = 0;
        }
        Set<Edge> allEdges = g.getAllEdges();
        for (Edge e : allEdges) {
            int nA = e.getNodeA();
            int nB = e.getNodeB();
            double w = e.getWeight();
            d[nA][nB] = d[nB][nA] = w;
            p[nA][nB] = nA;
            p[nB][nA] = nB;
        }

        for (int m = 0; m < nn; m++) {
            for (int src = 0; src < nn; src++) {
                for (int dst = 0; dst < nn; dst++) {
                    if (d[src][dst] > d[src][m] + d[m][dst]) {
                        d[src][dst] = d[src][m] + d[m][dst];
                        p[src][dst] = p[m][dst];
                    }
                }
            }
        }

        return new AllGraphPaths(d, p);
    }
}
