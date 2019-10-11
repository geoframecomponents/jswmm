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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.nodeObject;

import com.github.geoframecomponents.jswmm.dataStructure.formatData.readData.DataCollector;
import com.github.geoframecomponents.jswmm.dataStructure.formatData.writeData.WriteDataToFile;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class AbstractNode {

    DataCollector dataCollector;
    WriteDataToFile writeDataToFile;
    ExternalInflow dryWeatherInflow;
    ExternalInflow rainfallDependentInfiltrationInflow;

    Unitable nodeUnits;

    String nodeName;
    Double nodeElevation;

    HashMap<Integer, LinkedHashMap<Instant, Double>> nodeFlowRate = new HashMap<>();
    LinkedHashMap<Instant, Double> nodeDepth;

    public AbstractNode(Unitable units) {
        super(units);
    }

    public abstract void sumFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate);

    public abstract HashMap<Integer, LinkedHashMap<Instant, Double>> getFlowRate();
}


