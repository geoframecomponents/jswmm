package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

import java.time.Instant;

public class GlobalRaingage extends AbstractRaingage {

    public GlobalRaingage(ReadDataFromFile readDataFromFile, ProjectUnits raingageUnits, String raingageName,
                          String dataSourceName, String stationName, Instant rainfallStartDate,
                          Instant rainfallEndDate, Double snowpack) {

        super(readDataFromFile, raingageUnits, raingageName, dataSourceName, stationName,
                rainfallStartDate, rainfallEndDate, snowpack);
    }
}
