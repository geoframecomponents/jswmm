package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

abstract class Subarea extends AbstractSubcatchments {

    String raingageName;
    SubcatchmentReceiverRunoff subcatchmentReceiverRunoff;

    Double imperviousPercentage;
    Double characteristicWidth;
    Double subareaSlope;
    Double percentageImperviousWOstorage;
    Double curbLength;

    Double subareaArea;
    Double depthFactor;
    Double roughnessCoefficient;

    List<Subarea> subareaConnnections;

    LinkedHashMap<Instant, Double> flowRate;

    abstract void setDepthFactor(Double subareaSlope, Double characteristicWidth);

    public Double getFlowRate(Instant currentTime){
        return flowRate.get(currentTime)*subareaArea;
    }

    void evaluateNextDepth(Double precipitation, Double evaporation, Instant currentTime) {

        Double tempPrecipitation = null;
        if (!subareaConnnections.isEmpty()) {
            for (Subarea connections : subareaConnnections) {
                connections.evaluateNextDepth(precipitation, evaporation, currentTime);
                tempPrecipitation += connections.getFlowRate(currentTime);
            }
            tempPrecipitation /= subareaArea;
            flowRate.put(currentTime, ODE());
        }
    }
}
