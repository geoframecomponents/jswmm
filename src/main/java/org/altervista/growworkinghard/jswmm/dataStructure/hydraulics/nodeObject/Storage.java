package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import java.time.Instant;
import java.util.LinkedHashMap;

public class Storage extends AbstractNode {
    @Override
    public void addRoutingFlowRate(LinkedHashMap<Instant, Double> newFlowRate) {

    }

    public void addRunoffFlowRate(LinkedHashMap<Instant, Double> newAreaFlowRate) {

    }

    @Override
    public LinkedHashMap<Instant, Double> getNodeFlowRate() {
        throw new NullPointerException("Nothing implemented yet");
    }
}
