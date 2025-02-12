package top.lihugang.mc.mod.minecraftrailwaynet.utils.netgraphalgorithm;

import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

public class Node {
    private int id;
    public Coord coordinate;

    public Node(int id, NodeType type, Coord coordinate) {
        this.id = id;
        if (type == NodeType.STATION) {
            this.id |= (1 << 31);
        }
        this.coordinate = coordinate;
    }

    public int getId() {
        return id & ~(1 << 31);
    }

    public NodeType getType() {
        return id < 0 ? NodeType.STATION : NodeType.SIGNAL;
    }
}
