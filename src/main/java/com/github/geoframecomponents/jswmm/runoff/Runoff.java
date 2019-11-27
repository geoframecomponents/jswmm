/*
 * JSWMM: Reimplementation of EPA SWMM in Java
 * Copyright (C) 2019 Daniele Dalla Torre (ftt01)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.geoframecomponents.jswmm.runoff;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment.Area;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import oms3.annotations.*;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffSolver;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class Runoff {

    @In
    public HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData; // [mm/hour]

    /**
     * Name of the area of current simulation
     */
    @In
    public String areaName = null;

    /**
     * Name of the node where area drains in the current simulation
     */
    @In
    public String nodeName = null;

    /**
     * Area properties
     */
    private Area area;

    /**
     * Data structure
     */
    @In
    public SWMMobject dataStructure;

    /**
     * HM of the flowrate over time that drains into the node,
     * ID of the rainfall curve is the key and the resulting runoff are the values
     */
    @OutNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> runoffFlowRate = new HashMap<>();

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        if (dataStructure != null && areaName != null) {
            //TODO add evaporation
            this.area = dataStructure.getAreas(areaName);
        }
        else {
            throw new NullPointerException("Runoff over" + areaName + "fails setup.");
        }
        area.evaluateRunoffFlowRate(adaptedRainfallData);

        for (Integer curveId : adaptedRainfallData.keySet()) {
            runoffFlowRate.put(curveId, area.evaluateTotalFlowRate(curveId)); //[m^3/s]
        }
    }
}
