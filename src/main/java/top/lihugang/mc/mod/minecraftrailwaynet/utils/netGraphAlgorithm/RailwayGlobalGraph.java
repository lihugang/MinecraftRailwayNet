package top.lihugang.mc.mod.minecraftrailwaynet.utils.netGraphAlgorithm;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

import java.util.*;

public class RailwayGlobalGraph {
    public static final int NODE_RADIUS = 20;
    private static final Map<RegistryKey<World>, RailwayGlobalGraph> graphs = new HashMap<>();

    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
    private int nextId = 0;

    private RailwayGlobalGraph() {

    }

    public static RailwayGlobalGraph getOrCreate(RegistryKey<World> world) {
        if (graphs.containsKey(world))
            return graphs.get(world);
        RailwayGlobalGraph newGraph = new RailwayGlobalGraph();
        graphs.put(world, newGraph);
        return newGraph;
    }

    public void addNode(NodeTypeEnum type, Coord coord) {
        Node node = new Node(nextId++, type, coord);
        nodes.put(node.getId(), node);
        adjacencyList.put(node.getId(), new ArrayList<>());
    }

    public void removeNodeAt(Coord coord) {
        Node nodeToRemove = findNodeAt(coord);
        if (nodeToRemove != null) {
            // 移除节点
            nodes.remove(nodeToRemove.getId());

            // 移除所有相关边
            adjacencyList.remove(nodeToRemove.getId());
            for (List<Edge> edges : adjacencyList.values()) {
                edges.removeIf(edge -> edge.to == nodeToRemove.getId());
            }
        }
    }

    public void addEdge(int from, int to, boolean isDirected) {
        if (nodes.containsKey(from) && nodes.containsKey(to)) {
            adjacencyList.get(from).add(new Edge(to, 1));
            if (!isDirected) {
                adjacencyList.get(to).add(new Edge(from, 1));
            }
        }
    }

    public Node findNodeAt(Coord coord) {
        for (Node node : nodes.values()) {
            if (node.coordinate.distanceTo(coord) <= NODE_RADIUS) {
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
