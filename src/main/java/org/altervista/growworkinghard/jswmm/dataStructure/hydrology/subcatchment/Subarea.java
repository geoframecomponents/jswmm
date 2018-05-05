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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Subarea {

    Double subareaArea;
    Double depthFactor;
    Double roughnessCoefficient;
    Double percentageRouted;
    Double depressionStorage;

    List<Subarea> subareaConnections;

    HashMap<Integer, LinkedHashMap<Instant, Double>> totalDepth;
    HashMap<Integer, LinkedHashMap<Instant, Double>> runoffDepth;
    HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate;
    HashMap<Integer, Double> excessRainfall;

    public void setTotalDepth(Integer id, Instant time, Double depthValue) {
        LinkedHashMap<Instant, Double> temp = new LinkedHashMap<>();
        temp.put(time, depthValue);
        this.totalDepth.put(id, temp);
    }

    public void setRunoffDepth(Integer id, Instant time, Double depthValue) {
        LinkedHashMap<Instant, Double> temp = new LinkedHashMap<>();
        temp.put(time, depthValue);
        this.runoffDepth.put(id, temp);
    }

    public void setFlowRate(Integer id, Instant time, Double flowValue) {
        LinkedHashMap<Instant, Double> temp = new LinkedHashMap<>();
        temp.put(time, flowValue);
        this.flowRate.put(id, temp);
    }

    public void setExcessRainfall(Integer id, Double value) {
        this.excessRainfall.put(id, value);
    }

    public Double getExcessRainfall(Integer id) {
        return this.excessRainfall.get(id);
    }

    public abstract void setDepthFactor(Double subareaSlope, Double characteristicWidth);

    abstract Double getWeightedFlowRate(Integer identifier, Instant currentTime);

    public void evaluateFlowRate(Integer identifier, Double rainfall, Double evaporation, Instant currentTime,
                                 RunoffSetup runoffSetup, Double subareaSlope, Double characteristicWidth) {

        Double tempPrecipitation = rainfall;
        if (subareaConnections != null) {
            tempPrecipitation = null;
            for (Subarea connections : subareaConnections) {
                connections.evaluateFlowRate(identifier, rainfall, evaporation, currentTime,
                        runoffSetup, subareaSlope, characteristicWidth);
                tempPrecipitation += connections.getWeightedFlowRate(identifier, currentTime);
                //infiltration
            }
            tempPrecipitation /= subareaArea;
        }
        evaluateNextStep(identifier, currentTime, runoffSetup, tempPrecipitation, evaporation,
                subareaSlope, characteristicWidth);
    }

    abstract void evaluateNextStep(Integer identifier, Instant currentTime, RunoffSetup runoffSetup, Double rainfall, Double evaporation,
                                   Double subareaArea, Double characteristicWidth);

    void runoffODEsolver(Integer id, Instant currentTime, Instant nextTime, Double rainfall, RunoffSetup runoffSetup) {
        double[] inputValues = new double[1];
        inputValues[0] = runoffDepth.get(id).get(currentTime);
        double[] outputValues = new double[1];

        Double initialTime = (double) currentTime.getEpochSecond();
        Double finalTime = (double) nextTime.getEpochSecond();

        runoffSetup.setOde(rainfall, depthFactor);
        runoffSetup.getFirstOrderIntegrator().integrate(runoffSetup.getOde(), initialTime, inputValues, finalTime, outputValues);

        setRunoffDepth(id, nextTime, outputValues[0]);
        setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + (outputValues[0]-inputValues[0]));
    }

    abstract Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth);}