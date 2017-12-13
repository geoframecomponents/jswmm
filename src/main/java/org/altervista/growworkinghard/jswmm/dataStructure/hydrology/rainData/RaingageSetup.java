package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData;

import java.time.Instant;
import java.util.LinkedHashMap;

public interface RaingageSetup {

    public LinkedHashMap<String, LinkedHashMap<Instant, Double>> getReadDataFromFile();

    //public ProjectUnits getRaingageUnits();
    public String getDataSourceName();

    public Long getRainfallStepSize();

}
