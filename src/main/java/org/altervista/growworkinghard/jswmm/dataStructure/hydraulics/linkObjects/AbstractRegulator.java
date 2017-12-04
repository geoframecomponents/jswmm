package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

abstract class AbstractRegulator extends AbstractLink {

    public AbstractRegulator(CrossSectionType crossSectionType, ProjectUnits linkUnits, String linkName,
                             String upstreamNodeName, String downstreamNodeName) {

        super(crossSectionType, linkUnits, linkName, upstreamNodeName, downstreamNodeName);
    }
}
