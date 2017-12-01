package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

public class Conduit extends AbstractLink {

    Double linkLength;
    Double linkManningRoughness;
    Double upstreamOffset;
    Double downstreamOffset;
    Double initialFlowRate;
    Double maximumFlowRate;

    public Conduit(CrossSectionType crossSectionType, ProjectUnits linkUnits, String linkName, String upstreamNodeName,
                   String downstreamNodeName, Double linkLength, Double linkManningRoughness, Double upstreamOffset,
                   Double downstreamOffset, Double initialFlowRate, Double maximumFlowRate) {

        super(crossSectionType, linkUnits, linkName, upstreamNodeName, downstreamNodeName);
        this.linkLength = linkLength;
        this.linkManningRoughness = linkManningRoughness;
        this.upstreamOffset = upstreamOffset;
        this.downstreamOffset = downstreamOffset;
        this.initialFlowRate = initialFlowRate;
        this.maximumFlowRate = maximumFlowRate;
    }
}
