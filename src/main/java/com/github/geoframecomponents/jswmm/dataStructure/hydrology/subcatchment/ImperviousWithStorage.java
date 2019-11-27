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

public class ImperviousWithStorage extends Subarea {

    Double totalImperviousArea;

    public ImperviousWithStorage(String name, Unitable units, Datetimeable time, Double imperviousWStorageArea,
                                 Double imperviousWOStorageArea, Double depressionStorageImpervious,
                                 Double roughnessCoefficient, Double percentageRouted, List<Subarea> connections) {

        super(name, units, time, imperviousWStorageArea, depressionStorageImpervious, roughnessCoefficient, percentageRouted, connections);
        this.totalImperviousArea = imperviousWStorageArea + imperviousWOStorageArea;
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        double depthFactor = 1.0;

        if (subareaConnections != null) {
            for (Subarea connections : subareaConnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);
                depthFactor = ( Math.sqrt(subareaSlope) * characteristicWidth ) /
                        (roughnessCoefficient * totalImperviousArea);
            }
        }
        else {
            depthFactor = (Math.sqrt(subareaSlope) * characteristicWidth) / (roughnessCoefficient * totalImperviousArea);
        }

        this.depthFactor = 1E-6 * depthFactor; // [ mm^(-2/3)/s ]
    }

    @Override
    Double getWeightedFlowRate(Integer identifier, Instant currentTime) {

        double weightedFlowRate = flowRate.get(identifier).get(currentTime) * subareaArea * percentageRouted;
        return weightedFlowRate;
    }

    @Override
    void evaluateNextStep(Integer id, Instant currentTime, RunoffSolver runoffSolver, Double rainfall,
                          Double evaporation, Double subareaSlope, Double characteristicWidth) {

        Long runoffStepSize =getSubcatchmentTime().getDateTime(AvailableDateTypes.stepSize);
        Instant nextTime = currentTime.plusSeconds(runoffStepSize);

        double totalDepthCurrent = totalDepth.get(id).get(currentTime);
        double runoffDepthCurrent = runoffDepth.get(id).get(currentTime);
        double areaFlowRateCurrent = getFlowRate().get(id).get(currentTime);

        Double moistureVolume = rainfall * runoffStepSize + totalDepthCurrent;

        if(evaporation != 0.0) {
            evaporation = Math.max(evaporation, totalDepthCurrent/runoffStepSize);
        }

        setExcessRainfall(id, rainfall - evaporation);

        if(evaporation * runoffStepSize >= moistureVolume) {
            setTotalDepth(id, nextTime, totalDepthCurrent);
            setRunoffDepth(id, nextTime, runoffDepthCurrent);
            setAreaFlowRate(id, nextTime, areaFlowRateCurrent);
        }
        else {
            if (getExcessRainfall(id) * runoffStepSize <= depressionStorage - totalDepthCurrent) {

                setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) +
                        getExcessRainfall(id) * runoffStepSize);
                setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime) + 0.0);
                setAreaFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime) + 0.0);
            } else {
                runoffODEsolver(id, currentTime, nextTime, getExcessRainfall(id), runoffSolver);
                setAreaFlowRate(id, nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth,
                        runoffDepth.get(id).get(nextTime)));
            }
        }
    }

    @Override
    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        double unitsFactor = 1E-6; //[mm/s]

        return unitsFactor * ( Math.sqrt(subareaSlope) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) ) / (totalImperviousArea * roughnessCoefficient);
    }
}
