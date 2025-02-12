package top.lihugang.mc.mod.minecraftrailwaynet.utils.netgraphalgorithm;

import java.awt.*;

public enum NodeType {
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

