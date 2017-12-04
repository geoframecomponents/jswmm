package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.time.Instant;
import java.util.LinkedHashMap;

public interface SubareaSetup {

    public Double subareaArea = null;
    public Double subareaStorage = null;
    public Double subareaDepthFactor = null;

    public LinkedHashMap<Instant, Double> subareaRainfallData = null;
    public LinkedHashMap<Instant, Double> subareaEvaporationData = null;
    public LinkedHashMap<Instant, Double> subareaInfiltrationData = null;
    public LinkedHashMap<Instant, Double> subareaDepth = null;
    public LinkedHashMap<Instant, Double> subareaFlowRate = null;

    public Double subareaExcessRainfall = null;

    public SubareaReceiver subareaReceiverRunoff = null;

    void evaluateSubareaDepth();
    Double evaluateAlpha();
    void upgradeFlowRateRunoff();
    Double getSubareaArea();

}
