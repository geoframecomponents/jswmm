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

import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.RunoffSetup;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

public class ImperviousWithoutStorage extends Subarea {

    Double totalImperviousArea;

    public ImperviousWithoutStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea, Double roughnessCoefficient) {
        this(imperviousWStorageArea, imperviousWOStorageArea, roughnessCoefficient, null, null);
    }

    public ImperviousWithoutStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                    Double roughnessCoefficient, Double percentageRouted, List<Subarea> connections) {
        this.subareaArea = imperviousWOStorageArea;
        this.totalImperviousArea = imperviousWStorageArea + imperviousWOStorageArea;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = connections;

        this.totalDepth = new LinkedHashMap<>();
        this.runoffDepth = new LinkedHashMap<>();
        this.flowRate = new LinkedHashMap<>();
        this.depressionStorage = 0.0;
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        if (subareaConnections != null) {
            for (Subarea connections : subareaConnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);
                this.depthFactor = Math.pow(subareaSlope, 0.5) *
                        characteristicWidth / (roughnessCoefficient * totalImperviousArea);
            }
        }
        else {
            this.depthFactor = (Math.pow(subareaSlope, 0.5) * characteristicWidth) / (roughnessCoefficient * totalImperviousArea);
        }
    }

    @Override
    Double getWeightedFlowRate(Instant currentTime) {
        return flowRate.get(currentTime) * subareaArea * percentageRouted;
    }

    @Override
    void evaluateNextStep(Instant currentTime, RunoffSetup runoffSetup, Double rainfall, Double evaporation,
                          Double subareaSlope, Double characteristicWidth) {

        Long runoffStepSize = runoffSetup.getRunoffStepSize();

        Instant nextTime = currentTime.plusSeconds(runoffStepSize);

        Double moistureVolume = rainfall * runoffStepSize + runoffDepth.get(currentTime);

        if(evaporation != 0.0) {
            evaporation = Math.max(evaporation, totalDepth.get(currentTime) / runoffStepSize);
        }

        excessRainfall = rainfall - evaporation;

        if(evaporation * runoffStepSize >= moistureVolume) {
            totalDepth.put(nextTime, totalDepth.get(currentTime) + 0.0);
            runoffDepth.put(nextTime, runoffDepth.get(currentTime) + 0.0);
            flowRate.put(nextTime, flowRate.get(currentTime) + 0.0);
        }
        else {
            runoffODEsolver(currentTime, nextTime, excessRainfall, runoffSetup);
            flowRate.put( nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth, runoffDepth.get(nextTime)) );
        }
    }

    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        return Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (totalImperviousArea * roughnessCoefficient);
    }
}