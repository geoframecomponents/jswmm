package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

public class Area extends AbstractSubcatchment {

    String raingageName;
    SubcatchmentReceiverRunoff receiverSubcatchment;

    Double imperviousPercentage; //TODO evaluate from subareas
    Double percentageImperviousWOstorage; //TODO evaluate from subareas

    Double characteristicWidth;
    Double areaSlope;
    //Double curbLength;

    List<Subarea> subareas;
    LinkedHashMap<Instant, Double> totalAreaFlowRate = new LinkedHashMap<>();

    public Area(Double subcatchmentArea, String raingageName, SubcatchmentReceiverRunoff receiverSubcatchment,
                Double characteristicWidth, Double areaSlope, List<Subarea> subareas) {
        this.subcatchmentArea = subcatchmentArea;
        this.raingageName = raingageName;
        this.receiverSubcatchment = receiverSubcatchment;
        this.characteristicWidth = characteristicWidth;
        this.areaSlope = areaSlope;
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
