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

package com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment;

import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffSolver;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class ImperviousWithoutStorage extends Subarea {

    Double totalImperviousArea;

    public ImperviousWithoutStorage(String name, Unitable units, Datetimeable time, Double imperviousWStorageArea,
                                    Double imperviousWOStorageArea, Double roughnessCoefficient,
                                    Double percentageRouted, List<Subarea> connections) {

        super(name, units, time, imperviousWOStorageArea, null, roughnessCoefficient, percentageRouted, connections);
        this.totalImperviousArea = imperviousWStorageArea + imperviousWOStorageArea;
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        double depthFactor = 0.0;
        if (subareaConnections != null) {

            for (Subarea connections : subareaConnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);

                depthFactor = Math.pow(subareaSlope, 0.5) *
                        characteristicWidth / (roughnessCoefficient * totalImperviousArea);
            }
        }
        else {
            depthFactor = (Math.pow(subareaSlope, 0.5) * characteristicWidth) / (roughnessCoefficient * totalImperviousArea);
        }

        //if ( super.getUnits().equals(UnitsSWMM.CMS) ){
            double CMSdepthFactor = 1E-6;
            this.depthFactor = CMSdepthFactor * depthFactor; // [ mm^(-2/3)/s ]
        //}
    }

    @Override
    Double getWeightedFlowRate(Integer identifier, Instant currentTime) {
        double weightedFlowRate = flowRate.get(identifier).get(currentTime) * subareaArea * percentageRouted;
        /*if (projectUnits.getProjectUnits() == CMS) {
            weightedFlowRate = weightedFlowRate * 1E10;      // [mm^3/s]
        }*/
        return weightedFlowRate;
    }

    @Override
    void evaluateNextStep(Integer id, Instant currentTime, RunoffSolver runoffSolver, Double rainfall, Double evaporation,
                          Double subareaSlope, Double characteristicWidth) {

        Long runoffStepSize = getSubcatchmentTime().getDateTime(AvailableDateTypes.stepSize);
        Instant nextTime = currentTime.plusSeconds(runoffStepSize);
        Double moistureVolume = rainfall * runoffStepSize + runoffDepth.get(id).get(currentTime);

        if(evaporation != 0.0) {
            evaporation = Math.max(evaporation, totalDepth.get(id).get(currentTime) / runoffStepSize);
        }

        setExcessRainfall(id, rainfall - evaporation);

        if(evaporation * runoffStepSize >= moistureVolume) {
            setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime));
            setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime));
            setAreaFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime));
        }
        else {
            runoffODEsolver(id, currentTime, nextTime, getExcessRainfall(id), runoffSolver);
            setAreaFlowRate( id, nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth,
                    runoffDepth.get(id).get(nextTime)) );
        }
    }

    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        double unitsFactor = 1.0;
        //if (super.getUnits().equals(UnitsSWMM.CMS) ){
            unitsFactor = 1E-6; //[mm/s]
        //}
        return unitsFactor * Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (totalImperviousArea * roughnessCoefficient);
    }
}
