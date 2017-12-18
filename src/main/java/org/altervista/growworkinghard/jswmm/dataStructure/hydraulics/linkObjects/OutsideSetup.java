package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import java.time.Instant;
import java.util.LinkedHashMap;

public class OutsideSetup {

    String nodeName;

    Double nodeOffset;
    //Double initialFlowRate; //TODO setup at initial value of flowRate
    Double maximumFlowRate;

    LinkedHashMap<Instant, Double> wetArea;
    LinkedHashMap<Instant, Double> flowRate;

    public OutsideSetup(String nodeName, Double nodeOffset, Double maximumFlowRate) {
        this.nodeName = nodeName;
        this.nodeOffset = nodeOffset;
        this.maximumFlowRate = maximumFlowRate;
    }

    public LinkedHashMap<Instant, Double> getWetArea() {
        return wetArea;
    }

    public LinkedHashMap<Instant, Double> getFlowRate() {
        return flowRate;
    }

    public void setWetArea(Instant time, Double value) {
        this.wetArea.put(time, value);
    }

    public void setFlowRate(LinkedHashMap<Instant, Double> flowRate) {
        this.flowRate = flowRate;
    }
}
