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
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffSolver;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class AbstractSubcatchment {

    Unitable subcatchmentUnits;
    Datetimeable subcatchmentTime;

    RunoffSolver runoffSolver;

    DataCollector dataFromFile;

    public void setSubcatchmentUnits(Unitable subcatchmentUnits) {
        this.subcatchmentUnits = subcatchmentUnits;
    }

    public void setSubcatchmentTime(Datetimeable subcatchmentTime) {
        this.subcatchmentTime = subcatchmentTime;
    }

    public Unitable getSubcatchmentUnits() {
        return subcatchmentUnits;
    }

    public Datetimeable getSubcatchmentTime() {
        return subcatchmentTime;
    }

    HashMap<Integer, LinkedHashMap<Instant, Double>> rainfallData;

    public DataCollector getDataFromFile() {
        return dataFromFile;
    }
}
