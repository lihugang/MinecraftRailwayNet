package top.lihugang.mc.mod.minecraftrailwaynet.utils.netgraphalgorithm;

import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

public class Node {
    public int id;
    public NodeType type;
    public Coord coordinate;

    public Node(int id, NodeType type, Coord coordinate) {
        this.id = id;
        this.type = type;
        this.coordinate = coordinate;
    }
}
