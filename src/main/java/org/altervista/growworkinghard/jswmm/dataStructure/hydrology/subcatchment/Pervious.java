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

package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.RunoffSetup;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.altervista.growworkinghard.jswmm.dataStructure.options.units.UnitsSWMM.CMS;

public class Pervious extends Subarea {

    Double infiltration = 0.0; //TODO temporary 0.0

    public Pervious(Double subareaArea, Double depressionStoragePervious,
                    Double roughnessCoefficient, ProjectUnits projectUnits) {
        this(subareaArea, depressionStoragePervious, roughnessCoefficient, null, null,
                projectUnits);
    }

    public Pervious(Double subareaArea, Double depressionStoragePervious, Double roughnessCoefficient,
                    Double percentageRouted, List<Subarea> connections, ProjectUnits projectUnits) {

        this.subareaArea = subareaArea;
        this.depressionStorage = depressionStoragePervious;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = connections;

        this.totalDepth = new HashMap<>();
        this.runoffDepth = new HashMap<>();
        this.flowRate = new HashMap<>();
        this.excessRainfall = new HashMap<>();

        this.projectUnits = projectUnits;
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

        if ( projectUnits.getProjectUnits() == CMS ) {
            this.depthFactor = 1E-6 * depthFactor; //return the depth in [ mm/s ]
        }
    }

    @Override
    Double getWeightedFlowRate(Integer identifier, Instant currentTime) {
        double weightedFlowRate = flowRate.get(identifier).get(currentTime) * subareaArea * percentageRouted;
//        if (projectUnits.getProjectUnits() == CMS) {
//            weightedFlowRate = weightedFlowRate * 1E10;      // [mm^3/s]
//        }
        return weightedFlowRate;
    }

    @Override
    void evaluateNextStep(Integer id, Instant currentTime, RunoffSetup runoffSetup, Double rainfall, Double evaporation,
                          Double subareaSlope, Double characteristicWidth) {

        Long runoffStepSize = runoffSetup.getRunoffStepSize();

        Instant nextTime = currentTime.plusSeconds(runoffStepSize);

        Double moistureVolume = rainfall * runoffStepSize + totalDepth.get(id).get(currentTime);

        if(evaporation != 0.0) {
            evaporation = Math.max(evaporation, totalDepth.get(id).get(currentTime)/runoffStepSize);
        }
        //infiltration
        //excessRainfall = rainfall - evaporation - infiltration;

        setExcessRainfall(id, rainfall - evaporation);

        if(evaporation * runoffStepSize >= moistureVolume) {
            setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime));
            setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime));
            setAreaFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime));
        }
        else {
            Double exRainHeigth = excessRainfall.get(id) * runoffStepSize;
            if ( exRainHeigth == 0.0 ) {
                if ( depressionStorage - totalDepth.get(id).get(currentTime) >= 0.0 ) {
                    setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + exRainHeigth);
                    setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime));
                    setAreaFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime));
                }
                else {
                    setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + exRainHeigth);
                    setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime) + exRainHeigth);
                    setAreaFlowRate( id, nextTime, getFlowRate().get(id).get(currentTime) +
                            evaluateNextFlowRate(subareaSlope, characteristicWidth,
                                    runoffDepth.get(id).get(nextTime)) );
                }
            }
            else {
                if( depressionStorage - totalDepth.get(id).get(currentTime) >= 0.0 ) {
                    setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + exRainHeigth);
                    setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime));
                    setAreaFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime));
                }
                else {
                    runoffODEsolver(id, currentTime, nextTime, getExcessRainfall(id), runoffSetup);
                    setAreaFlowRate( id, nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth,
                            runoffDepth.get(id).get(nextTime)) );
                }
            }
        }
    }

    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        double unitsFactor = 1.0;
        if (projectUnits.getProjectUnits() == CMS) {
            unitsFactor = 1E-6; //[mm/s]
        }
        return unitsFactor * Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (subareaArea * roughnessCoefficient);
    }
}