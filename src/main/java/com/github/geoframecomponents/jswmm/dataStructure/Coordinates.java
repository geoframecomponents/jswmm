package com.github.geoframecomponents.jswmm.dataStructure;

public class Coordinates {
    public double x;
    public double y;
    public double z;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(double x, double y, double z) {
        this(x,y);
        this.z = z;
    }
}
