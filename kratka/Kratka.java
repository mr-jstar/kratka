package kratka;

import graphs.GridGraph;
import graphs.Edge;
import graphs.GraphPaths;
import graphs.GraphUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;

/**
 *
 * @author jstar
 */
public class Kratka extends Application {

    private int nodeSize = 16;
    private int leftSep = 10;
    private int topSep = 10;
    private int nodeSep = 40;

    private GraphicsContext gc;
    private GridGraph graph;
    private Canvas canvas;
    private double minWght = 0;
    private double maxWght = 20;
    private final ColorMap edgeCM = new ColorMap(minWght, maxWght);

    private ColorMap nodeCM;
    private Label nodeViewMinLabel;
    private Label nodeViewMaxLabel;
    private Label edgeViewMinLabel;
    private Label edgeViewMaxLabel;

    private int plotWidth = 1000;
    private int plotHeight = 1000;

    private GraphPaths paths = null;

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {

        Label slabel = new Label("Grid size: ");
        TextField sTextField = new TextField();
        sTextField.setAlignment(Pos.CENTER);
        sTextField.setText("10 x 10");
        HBox shbox = new HBox(sTextField);

        Label rlabel = new Label("Edge weight range: ");
        TextField rTextField = new TextField();
        rTextField.setAlignment(Pos.CENTER);
        rTextField.setText(minWght + " - " + maxWght);
        HBox rhbox = new HBox(rTextField);

        HBox phbox = new HBox(5, slabel, shbox, rlabel, rhbox);

        Button abtn = new Button();
        abtn.setText("Generate");
        abtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                nodeSize = 16;
                leftSep = 10;
                topSep = 10;
                nodeSep = 40;
                try {
                    String[] cr = sTextField.getText().split("\\s*x\\s*");
                    String[] mx = rTextField.getText().split("\\s*-\\s*");
                    minWght = Double.parseDouble(mx[0]);
                    maxWght = Double.parseDouble(mx[1]);
                    edgeViewMinLabel.setText("" + minWght);
                    edgeViewMaxLabel.setText("" + maxWght);
                    edgeCM.setMin(minWght);
                    edgeCM.setMax(maxWght);
                    graph = new GridGraph(Integer.parseInt(cr[0]), Integer.parseInt(cr[1]), minWght, maxWght);
                    paths = null;
                    System.out.println("Draw graph " + graph.getNumColumns() + "x" + graph.getNumRows());
                    drawGraph(gc, canvas.getWidth(), canvas.getHeight());
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        Button rbtn = new Button();
        rbtn.setText("Redraw");
        rbtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Redraw graph");
                drawGraph(gc, canvas.getWidth(), canvas.getHeight());
                if (paths != null) {
                    colorNodes(gc, canvas.getWidth(), canvas.getHeight(), paths.d);
                }
            }
        });

        Button dbtn = new Button();
        dbtn.setText("Delete");
        dbtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Delete graph");
                graph = null;
                paths = null;
                drawGraph(gc, canvas.getWidth(), canvas.getHeight());
            }
        });

        Button sbtn = new Button();
        sbtn.setText("Save");
        sbtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (graph != null) {
                    FileChooser fileChooser = new FileChooser();
                    //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                    //fileChooser.getExtensionFilters().add(extFilter);
                    File file = fileChooser.showSaveDialog(primaryStage);
                    if (file != null) {
                        System.out.println("Save graph");
                        try {
                            graph.save(file.getAbsolutePath());
                        } catch (IOException e) {
                            System.out.println("NOT SAVED: " + e.getLocalizedMessage());
                        }
                    }
               }
            }
        });

        Button ebtn = new Button();
        ebtn.setText("Exit");
        ebtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        HBox btnBox = new HBox(40, phbox, abtn, rbtn, sbtn, dbtn, ebtn);

        FlowPane root = new FlowPane();
        root.getChildren().add(btnBox);

        canvas = new Canvas(plotWidth, plotHeight);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {

                int c = (int) ((e.getX() - leftSep) / nodeSep);
                int r = (int) ((e.getY() - topSep) / nodeSep);

                System.out.println("(" + e.getX() + "," + e.getY() + ") -> " + "(" + c + "," + r + ")");

                if (graph != null && c >= 0 && c < graph.getNumColumns() && r >= 0 && r < graph.getNumRows()) {
                    System.out.println("Node # " + graph.nodeNum(r, c));
                    if (e.getButton() == MouseButton.PRIMARY) {
                        if( pressedKeys.contains(KeyCode.D) ) {
                            System.out.println("Dijkstra");
                            paths = GraphUtils.dijkstra(graph, graph.nodeNum(r, c));
                        } else if( pressedKeys.contains(KeyCode.B) ) {
                            System.out.println("BFS");
                            paths = GraphUtils.bfs(graph, graph.nodeNum(r, c));
                        }
                    }
                    if (e.getButton() == MouseButton.SECONDARY) {
                        if (paths != null) {
                            System.out.println("Path to");
                            ArrayList<Integer> path = decodePathTo(graph.nodeNum(r, c));
                            printPath(path);
                            drawPath(gc, canvas.getWidth(), canvas.getHeight(), path);
                        } else {
                            System.out.println("No paths defined!");
                        }
                    } else {
                        drawGraph(gc, canvas.getWidth(), canvas.getHeight());
                        if (paths != null) {
                            nodeCM = new ColorMap(paths.dMin, paths.dMax);
                            nodeViewMinLabel.setText("" + paths.dMin);
                            nodeViewMaxLabel.setText("" + paths.dMax);
                            colorNodes(gc, canvas.getWidth(), canvas.getHeight(), paths.d);
                            ArrayList<Integer> longestPath = decodePathTo(paths.farthest);
                            printPath(longestPath);
                        }
                    }
                }

            }
        };

        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        edgeViewMinLabel = new Label("" + minWght);
        edgeViewMaxLabel = new Label("" + maxWght);
        HBox lbox = new HBox(plotWidth / 2, edgeViewMinLabel, new Label("Edge color scale"), edgeViewMaxLabel);
        root.getChildren().add(lbox);
        root.getChildren().add(new ImageView(edgeCM.createColorScaleImage(plotWidth, 20, Orientation.HORIZONTAL)));
        nodeCM = new ColorMap(0, 1);
        nodeViewMinLabel = new Label("0");
        nodeViewMaxLabel = new Label("1");
        HBox kbox = new HBox(plotWidth / 2, nodeViewMinLabel, new Label("Node color scale"), nodeViewMaxLabel);
        root.getChildren().add(kbox);

        Scene scene = new Scene(root, plotHeight, plotHeight + 80);

        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        primaryStage.setTitle("Kratka");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ArrayList<Integer> decodePathTo(int farthest) {
        ArrayList<Integer> path = new ArrayList<>();
        //restore from last to source
        for (; farthest >= 0; farthest = paths.p[farthest]) {
            path.add(farthest);
        }
        //and reverse
        for (int i = 0, j = path.size() - 1; i < j; i++, j--) {
            int tmp = path.get(i);
            path.set(i, path.get(j));
            path.set(j, tmp);
        }
        return path;
    }

    private void printPath(ArrayList<Integer> path) {
        for (int i = 0; i < path.size(); i++) {
            System.out.print(" " + path.get(i));
        }
        System.out.println();
    }

    private void drawGraph(GraphicsContext gc, double width, double height) {

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);
        if (graph == null || graph.getNumNodes() < 1) {
            return;
        }
        gc.setFill(Color.GREY);
        gc.setLineWidth(2);
        int rows = graph.getNumRows();
        int cols = graph.getNumColumns();
        if (leftSep + cols * nodeSep + nodeSize / 2 > width) {
            nodeSep = (int) ((width - leftSep - nodeSize / 2) / cols);
        }
        if (topSep + rows * nodeSep + nodeSize / 2 > height) {
            nodeSep = (int) ((height - topSep - nodeSize / 2) / rows);
        }
        if (nodeSep < 1) {
            return;
        }
        nodeSize = (int) (nodeSize * nodeSep / 40);
        nodeSize = nodeSize < 10 ? 10 : nodeSize;
        System.out.println("Node size: " + nodeSize + " sep: " + nodeSep);
        int[][] rc = new int[graph.getNumNodes()][2];
        for (int n = 0; n < graph.getNumNodes(); n++) {
            rc[n][0] = n % rows;  // column
            rc[n][1] = n / rows;  // row
            //System.out.println(n + "-> r=" + rc[n][0] + " c=" + rc[n][1]);
        }
        for (int n = 0; n < graph.getNumNodes(); n++) {
            Set<Edge> edges = graph.getConnectionsList(n);
            for (Edge e : edges) {
                Color c = edgeCM.getColorForValue(e.getWeight());
                //System.out.println(e.getNodeA() + "--" + e.getNodeB() + " : " + e.getWeight() + "->" + c);
                gc.setStroke(c);
                int nA = e.getNodeA();
                int nB = e.getNodeB();
                gc.strokeLine(leftSep + nodeSize / 2 + rc[nA][1] * nodeSep, topSep + nodeSize / 2 + rc[nA][0] * nodeSep, leftSep + nodeSize / 2 + rc[nB][1] * nodeSep, topSep + nodeSize / 2 + rc[nB][0] * nodeSep);
            }
        }
        for (int r = 0; r < graph.getNumRows(); r++) {
            for (int c = 0; c < graph.getNumColumns(); c++) {
                gc.fillOval(leftSep + c * nodeSep, topSep + r * nodeSep, nodeSize, nodeSize);
            }
        }
    }

    private void colorNodes(GraphicsContext gc, double width, double height, double[] colors) {
        if (graph == null || graph.getNumNodes() < 1) {
            return;
        }
        for (int r = 0; r < graph.getNumRows(); r++) {
            for (int c = 0; c < graph.getNumColumns(); c++) {
                gc.setFill(nodeCM.getColorForValue(colors[graph.nodeNum(r, c)]));
                gc.fillOval(leftSep + c * nodeSep, topSep + r * nodeSep, nodeSize, nodeSize);
            }
        }
    }

    private void drawPath(GraphicsContext gc, double width, double height, ArrayList<Integer> path) {
        if (graph == null || graph.getNumNodes() < 1) {
            return;
        }
        gc.setLineWidth(4);
        gc.setStroke(Color.WHITE);
        int nA = path.get(0);
        int nB;
        for (int i = 1; i < path.size(); i++) {
            nB = path.get(i);
            gc.strokeLine(leftSep + nodeSize / 2 + graph.col(nA) * nodeSep, topSep + nodeSize / 2 + graph.row(nA) * nodeSep, leftSep + nodeSize / 2 + graph.col(nB) * nodeSep, topSep + nodeSize / 2 + graph.row(nB) * nodeSep);
            nA = nB;
        }
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
