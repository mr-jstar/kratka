/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package graphs;

/**
 *
 * @author jstar
 */
public class GraphPaths {

    public double[] d;
    public int[] p;
    public double dMin, dMax;
    public int src, farthest;

    public GraphPaths(double[] d, int[] p) {
        if (d.length < 1 || d.length != p.length) {
            throw new IllegalArgumentException("GraphPaths constructor: distance and precedessor lists are not compatible!");
        }
        this.d = d;
        this.p = p;
        dMin = d[0];
        dMax = dMin;
        src = 0;
        farthest = 0;
        for (int i = 1; i < d.length; i++) {
            double x = d[i];
            if (x < dMin) {
                dMin = x;
                src = i;
            }
            if (x > dMax) {
                dMax = x;
                farthest = i;
            }
        }
    }
}