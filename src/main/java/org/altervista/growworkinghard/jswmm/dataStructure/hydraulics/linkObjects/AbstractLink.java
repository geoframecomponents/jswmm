package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

public abstract class AbstractLink {

    CrossSectionType crossSectionType;
    ProjectUnits linkUnits;

    String linkName;
    String upstreamNodeName;
    String downstreamNodeName;

    public AbstractLink(CrossSectionType crossSectionType, ProjectUnits linkUnits, String linkName,
                        String upstreamNodeName, String downstreamNodeName) {

        this.crossSectionType = crossSectionType;
        this.linkUnits = linkUnits;
        this.linkName = linkName;
        this.upstreamNodeName = upstreamNodeName;
        this.downstreamNodeName = downstreamNodeName;
    }
}