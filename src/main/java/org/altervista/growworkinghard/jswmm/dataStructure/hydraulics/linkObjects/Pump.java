package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

class Pump extends AbstractLink {
    public Pump(CrossSectionType crossSectionType, ProjectUnits linkUnits, String linkName,
                String upstreamNodeName, String downstreamNodeName) {

        super(crossSectionType, linkUnits, linkName, upstreamNodeName, downstreamNodeName);
    }
}
