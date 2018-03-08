package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import java.time.Instant;
import java.util.LinkedHashMap;

public class OutsideSetup {

    String nodeName;

    Double nodeOffset;
    //Double initialFlowRate; //TODO setup at initial value of streamFlowRate
    Double maximumFlowRate;

    LinkedHashMap<Instant, Double> streamWetArea;
    LinkedHashMap<Instant, Double> streamFlowRate;

    public OutsideSetup(String nodeName, Double nodeOffset, Double maximumFlowRate) {
        this.nodeName = nodeName;
        this.nodeOffset = nodeOffset;
        this.maximumFlowRate = maximumFlowRate;
    }

    public LinkedHashMap<Instant, Double> getStreamWetArea() {
        return streamWetArea;
    }

    public LinkedHashMap<Instant, Double> getStreamFlowRate() {
        return streamFlowRate;
    }

    public void setWetArea(Instant time, Double value) {
        this.streamWetArea.put(time, value);
    }

    public void setFlowRate(Instant time, Double flowRate) {
        this.streamFlowRate.put(time, flowRate);
    }

    public String getNodeName() {
        return nodeName;
    }
}
