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

public class ImperviousWithoutStorage extends Subarea {

    Double totalImperviousArea;

    public ImperviousWithoutStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                    Double roughnessCoefficient, ProjectUnits projectUnits) {
        this(imperviousWStorageArea, imperviousWOStorageArea, roughnessCoefficient,
                null, null, projectUnits);
    }

    public ImperviousWithoutStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                    Double roughnessCoefficient, Double percentageRouted,
                                    List<Subarea> connections, ProjectUnits projectUnits) {
        this.subareaArea = imperviousWOStorageArea;
        this.totalImperviousArea = imperviousWStorageArea + imperviousWOStorageArea;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = connections;

        this.totalDepth = new HashMap<>();
        this.runoffDepth = new HashMap<>();
        this.flowRate = new HashMap<>();
        this.excessRainfall = new HashMap<>();
        this.depressionStorage = 0.0;

        this.projectUnits = projectUnits;
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

        if ( projectUnits.getProjectUnits() == CMS ) {
            double CMSdepthFactor = 3.6E-3;
            this.depthFactor = CMSdepthFactor * depthFactor; //return the q = depthFactor * d^(5/3) in [ mm/h ]
        }
    }

    @Override
    Double getWeightedFlowRate(Integer id, Instant currentTime) {
        return flowRate.get(id).get(currentTime) * subareaArea * percentageRouted;
    }

    @Override
    void evaluateNextStep(Integer id, Instant currentTime, RunoffSetup runoffSetup, Double rainfall, Double evaporation,
                          Double subareaSlope, Double characteristicWidth) {

        Long runoffStepSize = runoffSetup.getRunoffStepSize();
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
            Double exRainHeigth = excessRainfall.get(id) * runoffStepSize;
            if ( exRainHeigth == 0.0 ) {
                setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + exRainHeigth);
                setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime) + exRainHeigth);
                setAreaFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime) +
                        evaluateNextFlowRate(subareaSlope, characteristicWidth,
                                runoffDepth.get(id).get(nextTime)) );
            }
            else {
                runoffODEsolver(id, currentTime, nextTime, getExcessRainfall(id), runoffSetup);
                setAreaFlowRate( id, nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth,
                        runoffDepth.get(id).get(nextTime)) );
            }
        }
    }

    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        return Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (totalImperviousArea * roughnessCoefficient);
    }
}