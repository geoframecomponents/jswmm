package org.altervista.growworkinghard.jswmm.dataStructure.routing;

import oms3.annotations.In;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

import java.time.Instant;
import java.util.HashMap;

public interface RoutingSetup {

    public void evaluateFlowRate(Instant currentTime, Long routingStepSize, OutsideSetup upstreamOutside,
                                 OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                                 CrossSectionType crossSectionType);

    public Instant getInitialTime();

    public Instant getTotalTime();

    public Long getRoutingStepSize();

    public HashMap<Instant, Double> getDownstreamFlowRate();
}