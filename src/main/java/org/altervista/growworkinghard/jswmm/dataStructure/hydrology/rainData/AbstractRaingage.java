package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

import java.time.Instant;

public abstract class AbstractRaingage {

    ReadDataFromFile readDataFromFile;
    ProjectUnits raingageUnits;

    String raingageName;
    String dataSourceName;
    String stationName;

    Instant rainfallStartDate;
    Instant rainfallEndDate;

    Double snowpack; //TODO where is used?

    public ReadDataFromFile getReadDataFromFile() {
        return readDataFromFile;
    }

    public ProjectUnits getRaingageUnits() {
        return raingageUnits;
    }

    public String getRaingageName() {
        return raingageName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public String getStationName() {
        return stationName;
    }

    public Instant getRainfallStartDate() {
        return rainfallStartDate;
    }

    public Instant getRainfallEndDate() {
        return rainfallEndDate;
    }

    public Double getSnowpack() {
        return snowpack;
    }
}
