import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

enum NodeType {
    STATION(Color.RED),
    SIGNAL(Color.BLUE);

    private final Color color;

    NodeType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}

public class NodeCreator extends JFrame {
    private final DrawingPanel drawingPanel;
    private final NodeGraph nodeGraph;

    public NodeCreator() {
        setTitle("右键创建节点");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nodeGraph = new NodeGraph();
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
                nodeGraph.addNode(clickPoint, NodeType.STATION);
                drawingPanel.repaint();
            }
        });
        signalItem.addActionListener(e -> {
            Point clickPoint = drawingPanel.getLastRightClickPoint();
            if (clickPoint != null) {
                nodeGraph.addNode(clickPoint, NodeType.SIGNAL);
                drawingPanel.repaint();
            }
        });
        deleteItem.addActionListener(e -> {
            Point clickPoint = drawingPanel.getLastRightClickPoint();
            if (clickPoint != null) {
                nodeGraph.removeNodeAt(clickPoint);
                drawingPanel.repaint();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NodeCreator().setVisible(true));
    }
}

class Node {
    int id;
    Point position;
    NodeType type;

    public Node(int id, Point position, NodeType type) {
        this.id = id;
        this.position = position;
        this.type = type;
    }
}

class NodeGraph {
    static final int NODE_RADIUS = 20;
    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
    private int nextId = 0;

    public void addNode(Point position, NodeType type) {
        Node node = new Node(nextId++, position, type);
        nodes.put(node.id, node);
        adjacencyList.put(node.id, new ArrayList<>());
    }

    public void removeNodeAt(Point point) {
        Node nodeToRemove = findNodeAt(point);
        if (nodeToRemove != null) {
            // 移除节点
            nodes.remove(nodeToRemove.id);

            // 移除所有相关边
            adjacencyList.remove(nodeToRemove.id);
            for (List<Edge> edges : adjacencyList.values()) {
                edges.removeIf(edge -> edge.to == nodeToRemove.id);
            }
        }
    }

    public void addEdge(int from, int to, boolean isDirected) {
        if (nodes.containsKey(from) && nodes.containsKey(to)) {
            adjacencyList.get(from).add(new Edge(to, isDirected));
            if (!isDirected) {
                adjacencyList.get(to).add(new Edge(from, isDirected));
            }
        }
    }

    public Node findNodeAt(Point point) {
        for (Node node : nodes.values()) {
            if (node.position.distance(point) <= NODE_RADIUS) {
                return node;
            }
        }
        return null;
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public List<Edge> getEdges(int nodeId) {
        return adjacencyList.getOrDefault(nodeId, Collections.emptyList());
    }
}

class Edge {
    int to;
    boolean isDirected;

    public Edge(int to, boolean isDirected) {
        this.to = to;
        this.isDirected = isDirected;
    }
}

class DrawingPanel extends JPanel {
    private static final int ARROW_SIZE = 10;
    private final NodeGraph nodeGraph;
    private Point lastRightClickPoint;
    private Node dragStartNode;
    private Point currentDragPoint;

    public DrawingPanel(NodeGraph nodeGraph) {
        this.nodeGraph = nodeGraph;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastRightClickPoint = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragStartNode = nodeGraph.findNodeAt(e.getPoint());
                    currentDragPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragStartNode != null) {
                    Node endNode = nodeGraph.findNodeAt(e.getPoint());
                    if (endNode != null && endNode.id != dragStartNode.id) {
                        boolean isDirected = !e.isShiftDown();
                        nodeGraph.addEdge(dragStartNode.id, endNode.id, isDirected);
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

    private void drawNodes(Graphics g) {
        for (Node node : nodeGraph.getNodes()) {
            g.setColor(node.type.getColor());
            g.fillOval(
                node.position.x - NodeGraph.NODE_RADIUS / 2,
                node.position.y - NodeGraph.NODE_RADIUS / 2,
                NodeGraph.NODE_RADIUS,
                NodeGraph.NODE_RADIUS
            );
        }
    }

    private void drawConnections(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        for (Node node : nodeGraph.getNodes()) {
            for (Edge edge : nodeGraph.getEdges(node.id)) {
                Node toNode = nodeGraph.getNodes().stream()
                    .filter(n -> n.id == edge.to)
                    .findFirst()
                    .orElse(null);

                if (toNode != null) {
                    g2d.setColor(Color.BLACK);
                    drawArrowLine(g2d, node.position, toNode.position, edge.isDirected);
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
                dragStartNode.position.x,
                dragStartNode.position.y,
                currentDragPoint.x,
                currentDragPoint.y
            );
        }
    }
}
