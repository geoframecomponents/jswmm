package org.altervista.growworkinghard.jswmm.dataStructure.routingDS;

import java.time.Instant;

public class RoutedFlow {
    Instant time;
    double value;

    public RoutedFlow(Instant time, double value) {
        this.time = time;
        this.value = value;
    }

    public Instant getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
