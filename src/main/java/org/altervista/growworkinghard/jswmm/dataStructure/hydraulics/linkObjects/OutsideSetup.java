package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import java.time.Instant;
import java.util.LinkedHashMap;

public class OutsideSetup {

    String nodeName;

    Double nodeOffset;
    //Double initialStreamFlowRate;
    Double maximumStreamFlowRate;

    LinkedHashMap<Instant, Double> streamWetArea;
    LinkedHashMap<Instant, Double> streamFlowRate;

    public OutsideSetup(String nodeName, Double nodeOffset, Double maximumFlowRate) {
        this.nodeName = nodeName;
        this.nodeOffset = nodeOffset;
        this.maximumStreamFlowRate = maximumFlowRate;
    }

    public LinkedHashMap<Instant, Double> getStreamWetArea() {
        return streamWetArea;
    }

    public LinkedHashMap<Instant, Double> getStreamFlowRate() {
        return streamFlowRate;
    }

    public String getNodeName() {
        return nodeName;
    }

    	public synchronized void setWetArea(Instant time, Double value) {
        this.streamWetArea.put(time, value);
    }

    public synchronized void setFlowRate(Instant time, Double flowRate) {
        this.streamFlowRate.put(time, flowRate);
    }
}
