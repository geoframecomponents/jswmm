package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData;

import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

import java.time.Instant;
import java.util.LinkedHashMap;

public class GlobalRaingage implements RaingageSetup {

    ReadDataFromFile readDataFromFile;
    //ProjectUnits raingageUnits;

    String dataSourceName;
    String stationName;
    Long rainfallStepSize;

    //Instant rainfallStartDate;
    //Instant rainfallEndDate;

    //Double snowpack; TODO where is used?

    public GlobalRaingage(ReadDataFromFile readDataFromFile, String dataSourceName,
                          String stationName, Long rainfallStepSize) {
            this.readDataFromFile = readDataFromFile;
            this.dataSourceName = dataSourceName;
            this.stationName = stationName;
            this.rainfallStepSize = rainfallStepSize;
    }


    @Override
    public LinkedHashMap<String, LinkedHashMap<Instant, Double>> getReadDataFromFile() {
        return readDataFromFile.getData();
    }

    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }

    @Override
    public String getStationName() {
        return stationName;
    }

    @Override
    public Long getRainfallStepSize() {
        return rainfallStepSize;
    }
}
