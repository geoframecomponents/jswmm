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

package com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment;

import com.github.geoframecomponents.jswmm.dataStructure.formatData.readData.DataCollector;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.ProjectUnits;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class AbstractSubcatchment {

    ProjectUnits projectUnits;

    AcquiferSetup acquiferSetup;
    SnowPackSetup snowpack;
    ProjectUnits subcatchmentUnits;

    DataCollector dataFromFile;

    String subcatchmentName;
    Double subcatchmentArea;

    HashMap<Integer, LinkedHashMap<Instant, Double>> rainfallData;

    public abstract void setProjectUnits(ProjectUnits projectUnits);

    public DataCollector getDataFromFile() {
        return dataFromFile;
    }
}
