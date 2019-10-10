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

import com.github.geoframecomponents.jswmm.dataStructure.options.units.ProjectUnits;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.AbstractRunoffSolver;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class Pervious extends Subarea {

    Double infiltration = 0.0; //TODO temporary 0.0

    public Pervious(Double subareaArea, Double depressionStoragePervious,
                    Double roughnessCoefficient, ProjectUnits projectUnits) {
        this(subareaArea, depressionStoragePervious, roughnessCoefficient, null, null,
                projectUnits);
    }

    public Pervious(Double subareaArea, Double depressionStoragePervious, Double roughnessCoefficient,
                    Double percentageRouted, List<Subarea> connections, ProjectUnits projectUnits) {

        super(projectUnits);
        this.subareaArea = subareaArea;
        this.depressionStorage = depressionStoragePervious;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = connections;

        this.totalDepth = new HashMap<>();
        this.runoffDepth = new HashMap<>();
        this.flowRate = new HashMap<>();
        this.excessRainfall = new HashMap<>();
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        double depthFactor = 0.0;
        if (subareaConnections != null) {
            for (Subarea connections : subareaConnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);
                depthFactor = Math.pow(subareaSlope, 0.5) *
                        characteristicWidth / (roughnessCoefficient * subareaArea);
            }
        }
        else {
            depthFactor = (Math.pow(subareaSlope, 0.5) * characteristicWidth) / (roughnessCoefficient * subareaArea);
        }

        if ( super.getUnits().equals(UnitsSWMM.CMS) ) {
            this.depthFactor = 1E-6 * depthFactor; // [ mm^(-2/3)/s ]
        }
    }

    @Override
    Double getWeightedFlowRate(Integer identifier, Instant currentTime) {
        double weightedFlowRate = flowRate.get(identifier).get(currentTime) * subareaArea * percentageRouted;
//        if (projectUnits.getProjectUnits() == CMS) {
//            weightedFlowRate = weightedFlowRate * 1E10;      // [mm/s]
//        }
        return weightedFlowRate;
    }

    @Override
    void evaluateNextStep(Integer id, Instant currentTime, AbstractRunoffSolver runoffSolver, Double rainfall, Double evaporation,
                          Double subareaSlope, Double characteristicWidth) {

        Long runoffStepSize = runoffSolver.getRunoffStepSize();

        Instant nextTime = currentTime.plusSeconds(runoffStepSize);

        double totalDepthCurrent = totalDepth.get(id).get(currentTime);
        double runoffDepthCurrent = runoffDepth.get(id).get(currentTime);
        double areaFlowRateCurrent = getFlowRate().get(id).get(currentTime);

        Double moistureVolume = rainfall * runoffStepSize + totalDepthCurrent;

        if(evaporation != 0.0) {
            evaporation = Math.max(evaporation, totalDepthCurrent/runoffStepSize);
        }
        //infiltration
        //excessRainfall = rainfall - evaporation - infiltration;

        setExcessRainfall(id, rainfall - evaporation);

        if(evaporation * runoffStepSize >= moistureVolume) {
            setTotalDepth(id, nextTime, totalDepthCurrent);
            setRunoffDepth(id, nextTime, runoffDepthCurrent);
            setAreaFlowRate(id, nextTime, areaFlowRateCurrent);
        }
        else {
            if (excessRainfall.get(id) * runoffStepSize <= depressionStorage - totalDepth.get(id).get(currentTime)) {
                setTotalDepth(id, nextTime, totalDepthCurrent + getExcessRainfall(id) * runoffStepSize);
                setRunoffDepth(id, nextTime, runoffDepthCurrent);
                setAreaFlowRate(id, nextTime, areaFlowRateCurrent);
            } else {
                runoffODEsolver(id, currentTime, nextTime, getExcessRainfall(id), runoffSolver);
                setAreaFlowRate(id, nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth,
                        runoffDepth.get(id).get(nextTime)));

            }
        }
    }

    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        double unitsFactor = 1.0;
        if (super.getUnits().equals(UnitsSWMM.CMS) ) {
            unitsFactor = 1E-6; //[mm/s]
        }
        return unitsFactor * Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (subareaArea * roughnessCoefficient);
    }
}
