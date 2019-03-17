/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.geoframecomponents.jswmm.dataStructure.hydrology.rainData;

import com.github.geoframecomponents.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

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
