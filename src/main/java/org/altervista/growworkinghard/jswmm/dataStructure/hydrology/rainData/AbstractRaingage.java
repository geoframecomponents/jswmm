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

    //Double snowpack; TODO where is used?
}
