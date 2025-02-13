package top.lihugang.mc.mod.minecraftrailwaynet.utils.netGraphAlgorithm;

import java.awt.*;

public enum NodeTypeEnum {
    STATION(Color.RED),
    SIGNAL(Color.BLUE);

    private final Color color;

    NodeTypeEnum(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}

