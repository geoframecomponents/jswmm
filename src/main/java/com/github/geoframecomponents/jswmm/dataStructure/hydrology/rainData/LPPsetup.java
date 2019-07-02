package com.github.geoframecomponents.jswmm.dataStructure.hydrology.rainData;

import java.time.Instant;
import java.util.LinkedHashMap;

public class LPPsetup implements RaingageSetup {

    Long rainfallStepSize;

    //Double snowpack; TODO where is used?

    public LPPsetup(Long rainfallStepSize) {
        this.rainfallStepSize = rainfallStepSize;
    }


    @Override
    public LinkedHashMap<String, LinkedHashMap<Instant, Double>> getReadDataFromFile() {
        throw new NullPointerException("Nothing assigned");
    }

    @Override
    public String getDataSourceName() {
        throw new NullPointerException("Nothing assigned");
    }

    @Override
    public String getStationName() {
        throw new NullPointerException("Nothing assigned");
    }

    @Override
    public Long getRainfallStepSize() {
        return rainfallStepSize;
    }
}