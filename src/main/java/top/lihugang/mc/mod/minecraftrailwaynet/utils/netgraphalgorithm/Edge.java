package top.lihugang.mc.mod.minecraftrailwaynet.utils.netgraphalgorithm;

public class Edge {
    public int to;
    public boolean isDirected;

    public Edge(int to, boolean isDirected) {
        this.to = to;
        this.isDirected = isDirected;
    }
}
