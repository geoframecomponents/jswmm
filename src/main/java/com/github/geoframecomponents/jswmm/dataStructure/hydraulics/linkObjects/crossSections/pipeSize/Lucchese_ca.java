package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import it.blogspot.geoframe.utils.GEOunitsTransform;

import java.util.LinkedHashMap;
import java.util.Map;

public class Lucchese_ca extends CommercialPipeSize {

    public Lucchese_ca() {
        pipe = new LinkedHashMap() {{
            put(400.0, 510.0);
            put(500.0, 620.0);
            put(600.0, 740.0);
            put(700.0, 850.0);
            put(800.0, 970.0);
            put(1000.0, 1200.0);
            put(1200.0, 1440.0);
        }};
    }
}