package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData;

import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

import java.time.Instant;

public class GlobalRaingage extends AbstractRaingage {

    public GlobalRaingage(ReadDataFromFile readDataFromFile, ProjectUnits raingageUnits, String raingageName,
                String dataSourceName, String stationName, Instant rainfallStartDate, Instant rainfallEndDate,
                Double snowpack) {
            this.readDataFromFile = readDataFromFile;
            this.raingageUnits = raingageUnits;
            this.raingageName = raingageName;
            this.dataSourceName = dataSourceName;
            this.stationName = stationName;
            this.rainfallStartDate = rainfallStartDate;
            this.rainfallEndDate = rainfallEndDate;
            this.snowpack = snowpack;
    }
}
