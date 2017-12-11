package org.altervista.growworkinghard.jswmm.dataStructure.routing;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

import java.time.Instant;

public class RoutingKinematicWaveSetup implements RoutingSetup {

    Instant routingStepSize;

    @Override
    public void evaluateWetArea(Instant currentTime, long routingStepSize, OutsideSetup upstreamOutside, OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness, CrossSectionType crossSectionType) {

    }

    @Override
    public void fillTables() {

    }
}
