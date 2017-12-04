package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.time.Instant;
import java.util.LinkedHashMap;

public interface SubareaSetup {

    Double storage = null;
    Double depthFactor = null;

    LinkedHashMap<Instant, Double> rainfallData = null;
    LinkedHashMap<Instant, Double> evaporationData = null;
    LinkedHashMap<Instant, Double> infiltrationData = null;
    LinkedHashMap<Instant, Double> depth = null;
    LinkedHashMap<Instant, Double> flowRate = null;

    Double excessRainfall = null;

    Double evaluateAlpha();
}
