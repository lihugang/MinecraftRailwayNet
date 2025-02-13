package top.lihugang.mc.mod.minecraftrailwaynet.utils.netGraphAlgorithm;

import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

public class Node {
    private int id;
    public Coord coordinate;

    public Node(int id, NodeTypeEnum type, Coord coordinate) {
        this.id = id;
        if (type == NodeTypeEnum.STATION) {
            this.id |= (1 << 31);
        }
        this.coordinate = coordinate;
    }

    public int getId() {
        return id & ~(1 << 31);
    }

    public NodeTypeEnum getType() {
        return id < 0 ? NodeTypeEnum.STATION : NodeTypeEnum.SIGNAL;
    }
}
