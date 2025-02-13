import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.netGraphAlgorithm.Edge;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.netGraphAlgorithm.Node;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.netGraphAlgorithm.NodeTypeEnum;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.netGraphAlgorithm.RailwayGlobalGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NodeCreator extends JFrame {
    private final DrawingPanel drawingPanel;
    private final RailwayGlobalGraph nodeGraph;

    public NodeCreator() {
        setTitle("右键创建节点");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nodeGraph = RailwayGlobalGraph.getOrCreate(null);
        drawingPanel = new DrawingPanel(nodeGraph);
        add(drawingPanel);

        // 右键菜单设置
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem stationItem = new JMenuItem("创建站台节点");
        JMenuItem signalItem = new JMenuItem("创建信号节点");
        JMenuItem deleteItem = new JMenuItem("删除节点");
        popupMenu.add(stationItem);
        popupMenu.add(signalItem);
        popupMenu.addSeparator();
        popupMenu.add(deleteItem);

        drawingPanel.setComponentPopupMenu(popupMenu);
        stationItem.addActionListener(e -> {
            Point clickPoint = drawingPanel.getLastRightClickPoint();
            if (clickPoint != null) {
                nodeGraph.addNode(NodeTypeEnum.STATION, point2Coord(clickPoint));
                drawingPanel.repaint();
            }
        });
        signalItem.addActionListener(e -> {
            Point clickPoint = drawingPanel.getLastRightClickPoint();
            if (clickPoint != null) {
                nodeGraph.addNode(NodeTypeEnum.SIGNAL, point2Coord(clickPoint));
                drawingPanel.repaint();
            }
        });
        deleteItem.addActionListener(e -> {
            Point clickPoint = drawingPanel.getLastRightClickPoint();
            if (clickPoint != null) {
                nodeGraph.removeNodeAt(point2Coord(clickPoint));
                drawingPanel.repaint();
            }
        });
    }

    static Coord point2Coord(Point point) {
        return new Coord(point.x, point.y, 0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NodeCreator().setVisible(true));
    }
}

class DrawingPanel extends JPanel {
    private static final int ARROW_SIZE = 10;
    private final RailwayGlobalGraph nodeGraph;
    private Point lastRightClickPoint;
    private Node dragStartNode;
    private Point currentDragPoint;

    public DrawingPanel(RailwayGlobalGraph nodeGraph) {
        this.nodeGraph = nodeGraph;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastRightClickPoint = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragStartNode = nodeGraph.findNodeAt(NodeCreator.point2Coord(e.getPoint()));
                    currentDragPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragStartNode != null) {
                    Node endNode = nodeGraph.findNodeAt(NodeCreator.point2Coord(e.getPoint()));
                    if (endNode != null && endNode.getId() != dragStartNode.getId()) {
                        boolean isDirected = !e.isShiftDown();
                        nodeGraph.addEdge(dragStartNode.getId(), endNode.getId(), isDirected);
                    }
                    dragStartNode = null;
                    currentDragPoint = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStartNode != null) {
                    currentDragPoint = e.getPoint();
                    repaint();
                }
            }
        });
    }

    public Point getLastRightClickPoint() {
        return lastRightClickPoint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawNodes(g);
        drawConnections(g);
        drawTemporaryConnection(g);
    }

    static Point coord2Point(Coord coord) {
        return new Point(coord.getX(), coord.getY());
    }

    private void drawNodes(Graphics g) {
        for (Node node : nodeGraph.getNodes()) {
            g.setColor(node.getType().getColor());
            g.fillOval(
                node.coordinate.getX() - RailwayGlobalGraph.NODE_RADIUS / 2,
                node.coordinate.getY() - RailwayGlobalGraph.NODE_RADIUS / 2,
                RailwayGlobalGraph.NODE_RADIUS,
                RailwayGlobalGraph.NODE_RADIUS
            );
        }
    }

    private void drawConnections(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        for (Node node : nodeGraph.getNodes()) {
            for (Edge edge : nodeGraph.getEdges(node.getId())) {
                Node toNode = nodeGraph.getNodes().stream()
                    .filter(n -> n.getId() == edge.to)
                    .findFirst()
                    .orElse(null);

                if (toNode != null) {
                    g2d.setColor(Color.BLACK);
                    drawArrowLine(g2d,
                        coord2Point(node.coordinate),
                        coord2Point(toNode.coordinate),
                        true
                    );
                }
            }
        }
    }

    private void drawArrowLine(Graphics2D g2d, Point start, Point end, boolean isDirected) {
        // 绘制连接线
        g2d.drawLine(start.x, start.y, end.x, end.y);

        if (isDirected) {
            // 计算中点
            int midX = (start.x + end.x) / 2;
            int midY = (start.y + end.y) / 2;

            // 计算线的角度
            double dx = end.x - start.x;
            double dy = end.y - start.y;
            double angle = Math.atan2(dy, dx);

            // 绘制中点箭头
            drawArrow(g2d, midX, midY, angle);

            // 绘制终点箭头
            drawArrow(g2d, end.x, end.y, angle);
        }
    }

    private void drawArrow(Graphics2D g2d, int x, int y, double angle) {
        int x1 = (int) (x - ARROW_SIZE * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (y - ARROW_SIZE * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (x - ARROW_SIZE * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (y - ARROW_SIZE * Math.sin(angle + Math.PI / 6));

        g2d.fillPolygon(
            new int[]{x, x1, x2},
            new int[]{y, y1, y2},
            3
        );
    }

    private void drawTemporaryConnection(Graphics g) {
        if (dragStartNode != null && currentDragPoint != null) {
            g.setColor(Color.GRAY);
            g.drawLine(
                dragStartNode.coordinate.getX(),
                dragStartNode.coordinate.getY(),
                currentDragPoint.x,
                currentDragPoint.y
            );
        }
    }
}
