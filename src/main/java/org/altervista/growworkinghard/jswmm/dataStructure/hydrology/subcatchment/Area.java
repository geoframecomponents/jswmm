package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

public class Area extends AbstractSubcatchments {

    String raingageName;
    SubcatchmentReceiverRunoff receiverSubcatchment;

    Double imperviousPercentage;
    Double percentageImperviousWOstorage;

    Double characteristicWidth;
    Double areaSlope;
    //Double curbLength;

    List<Subarea> subareas;
    LinkedHashMap<Instant, Double> totalAreaFlowRate = new LinkedHashMap<>();

    public Area(List<Subarea> subareas) {
        this.subareas = subareas;
    }

    public void evaluateTotalFlowRate() {
        for(Subarea subarea : subareas) {
            subarea.flowRate.forEach((k, v) -> totalAreaFlowRate.merge(k, v*subarea.subareaArea, Double::sum));
        }
    }

    public LinkedHashMap<Instant, Double> getTotalAreaFlowRate() {
        return totalAreaFlowRate;
    }

    public List<Subarea> getSubareas() {
        return subareas;
    }

    public void setTotalAreaFlowRate(LinkedHashMap<Instant, Double> totalAreaFlowRate) {
        this.totalAreaFlowRate = totalAreaFlowRate;
    }

    public Double getCharacteristicWidth() {
        return characteristicWidth;
    }

    public Double getAreaSlope() {
        return areaSlope;
    }
}
