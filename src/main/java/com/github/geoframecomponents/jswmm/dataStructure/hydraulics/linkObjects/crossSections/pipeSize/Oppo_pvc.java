package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import it.blogspot.geoframe.utils.GEOunitsTransform;

import java.util.LinkedHashMap;
import java.util.Map;

public class Oppo_pvc extends CommercialPipeSize {

    public Oppo_pvc() {
        pipe = new LinkedHashMap() {{
            put(153.6,160.0);
            put(192.2,200.0);
            put(240.2,250.0);
            put(302.6,315.0);
            put(384.2,400.0);
            put(480.4,500.0);
            put(605.4,630.0);
            put(1005.4,1000.0);
        }};
    }
}
