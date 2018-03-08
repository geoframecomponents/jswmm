package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import java.time.Instant;
import java.util.LinkedHashMap;

public class Junction extends AbstractNode {

    Double maximumDepthNode;
    Double initialDepthnode;
    Double maximumDepthSurcharge;
    Double pondingArea;

    public Junction(Double nodeElevation, Double maximumDepthNode, Double initialDepthnode,
                    Double maximumDepthSurcharge, Double pondingArea) {
        this.nodeElevation = nodeElevation;
        this.maximumDepthNode = maximumDepthNode;
        this.initialDepthnode = initialDepthnode;
        this.maximumDepthSurcharge = maximumDepthSurcharge;
        this.pondingArea = pondingArea;
    }
}


