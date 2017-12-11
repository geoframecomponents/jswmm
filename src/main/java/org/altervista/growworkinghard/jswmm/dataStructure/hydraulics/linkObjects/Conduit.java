package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

abstract class Conduit extends AbstractLink {

    CrossSectionType crossSectionType;

    OutsideSetup upstreamOutside;
    OutsideSetup downstreamOutside;

    Double linkLength;
    Double linkRoughness;
    Double linkSlope;

    Double evaluateWetArea() {
        return routingMethod.evaluateWetArea();
    }
}
