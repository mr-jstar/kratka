package graphs;

import java.util.Deque;
import java.util.PriorityQueue;

/**
 *
 * @author jstar
 */
public class GraphUtils {

    static final int WHITE = 0;
    static final int GRAY = 1;
    static final int BLACK = 2;

    public static String lastError = null;

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

    public static Graph prim( Graph g ) {
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        ModifiableGraph mst = new ModifiableGraph();
        mst.addNode(0);
        for( Edge e : g.getConnectionsList(0))
            pq.add(e);
        while( ! pq.isEmpty() && mst.getNumNodes() < g.getNumNodes() ) {
            //System.out.println( "Prim: |V.mst|="+mst.getNumNodes()+"  |PQ|="+pq.size());
            Edge se = pq.poll();
            int nA = se.getNodeA();
            int nB = se.getNodeB();
            if( mst.hasNode( nA ) && mst.hasNode( nB ) )
                continue;
            else {
                if(mst.hasNode(nA)) {
                    mst.addNode(nB);
                    mst.addEdge(nA,nB,se.getWeight());
                    for( Edge e: g.getConnectionsList(nB))
                        pq.add(e);
                } else {
                    mst.addNode(nA);
                    mst.addEdge(nB,nA,se.getWeight());
                    for( Edge e: g.getConnectionsList(nA))
                        pq.add(e);
                }
            }
        }
        return mst;
    }

    public static GraphPaths bfs(Graph g, int startNode) {
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

        return new GraphPaths(d, p);
    }

    public static GraphPaths dfs(Graph g) {
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

        return new GraphPaths(p, d, f);
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

    public static GraphPaths dfs_iterative(Graph g) {
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

        return new GraphPaths(p, d, f);

    }

    private static class HeapPQ {

        private int[] h;
        private double[] d;
        private int n;

        HeapPQ(double[] d) {
            this.d = d;
            h = new int[d.length];
            n = 0;
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
                    return;
                }
                c = p;
            }
        }

        void add(int i, double dst) {
            d[i] = dst;
            h[n++] = i;
            heapUp(n - 1);
        }

        int poll() {
            if (n == 0) {
                throw new IllegalStateException("GraphUtils::dijkstra: trying to pop from empty priority queue!");
            }
            int ret = h[0];
            h[0] = h[--n];
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
                p = c;
                c = 2 * p + 1;
            }
            return ret;
        }

        void update(int x, double dst) {
            d[x] = dst;
            int i;
            for (i = 0; i < n; i++) {
                if (h[i] == x) {
                    break;
                }
            }
            if (i == n) {
                h[i] = x;  //  there was no x in heap yet
                n++;
            }
            //System.out.println(); print();
            heapUp(i);
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

    public static GraphPaths dijkstra(Graph g, int startNode) {
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

        return new GraphPaths(d, p);
    }
}
