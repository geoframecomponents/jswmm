package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

public class Conduit extends AbstractLink {

    CrossSectionType crossSectionType;

    OutsideSetup upstreamOutside;
    OutsideSetup downstreamOutside;

    Double linkLength;
    Double linkRoughness;
    Double linkSlope;

    public Conduit(CrossSectionType crossSectionType, OutsideSetup upstreamOutside, OutsideSetup downstreamOutside,
                   Double linkLength, Double linkRoughness, Double linkSlope) {

        this.crossSectionType = crossSectionType;
        this.upstreamOutside = upstreamOutside;
        this.downstreamOutside = downstreamOutside;
        this.linkLength = linkLength;
        this.linkRoughness = linkRoughness;
        this.linkSlope = linkSlope;
    }
}
