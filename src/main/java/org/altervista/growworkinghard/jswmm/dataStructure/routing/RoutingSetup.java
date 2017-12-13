package org.altervista.growworkinghard.jswmm.dataStructure.routing;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

import java.time.Instant;

public interface RoutingSetup {

    public void evaluateWetArea(Instant currentTime, Long routingStepSize, OutsideSetup upstreamOutside,
                                OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                                CrossSectionType crossSectionType);

    public Instant getInitialTime();

    public Instant getTotalTime();

    public Long getRoutingStepSize();
}