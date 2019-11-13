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

import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffSolver;

import java.time.Instant;
import java.util.*;

public abstract class Subarea extends AbstractSubcatchment {

    Double subareaArea;
    Double depthFactor;
    Double roughnessCoefficient;
    Double percentageRouted;
    Double depressionStorage;

    List<Subarea> subareaConnections;

    HashMap<Integer, LinkedHashMap<Instant, Double>> totalDepth;    //[mm]
    HashMap<Integer, LinkedHashMap<Instant, Double>> runoffDepth;   //[mm]

    HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate;      //[mm/s]
    HashMap<Integer, Double> excessRainfall;

    public Subarea(String areaName, Unitable units, Datetimeable time, Double subareaArea, Double depressionStorage, Double roughnessCoefficient,
                   Double percentageRouted, List<Subarea> subareaConnections) {

        super(areaName);
        this.subcatchmentUnits = units;
        this.subcatchmentTime = time;

        this.subareaArea = subareaArea;
        this.depressionStorage = depressionStorage;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = subareaConnections;

        this.totalDepth = new HashMap<>();
        this.runoffDepth = new HashMap<>();
        this.flowRate = new HashMap<>();
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getFlowRate() {
        return flowRate;
    }

    public void setTotalDepth(Integer id, Instant time, Double depthValue) {
        if (!totalDepth.containsKey(id)) {
            totalDepth.put(id, new LinkedHashMap<>());
        }
        LinkedHashMap<Instant, Double> oldLHM = totalDepth.get(id);
        oldLHM.put(time, depthValue);
        totalDepth.put(id, oldLHM);
    }

    public void setRunoffDepth(Integer id, Instant time, Double depthValue) {
        if (!runoffDepth.containsKey(id)) {
            runoffDepth.put(id, new LinkedHashMap<>());
        }
        LinkedHashMap<Instant, Double> oldLHM = runoffDepth.get(id);
        oldLHM.put(time, depthValue);
        runoffDepth.put(id, oldLHM);
    }

    public void setAreaFlowRate(Integer id, Instant time, Double flowValue) { //[mm/s]
        double unitsFactor = 1.0;
//        if ( projectUnits.getProjectUnits() == CMS ) {
//            unitsFactor = 1.0E-5;
//        }
        if (!flowRate.containsKey(id)) {
            flowRate.put(id, new LinkedHashMap<>());
        }
        LinkedHashMap<Instant, Double> oldLHM = flowRate.get(id);
        oldLHM.put(time, flowValue * unitsFactor);
        flowRate.put(id, oldLHM);
    }

    public void setExcessRainfall(Integer id, Double value) {
        if (excessRainfall == null) {
            this.excessRainfall = new HashMap<>();
        }
        this.excessRainfall.put(id, value);
    }

    public Double getExcessRainfall(Integer id) {
        return this.excessRainfall.get(id);
    }

    public abstract void setDepthFactor(Double subareaSlope, Double characteristicWidth);

    abstract Double getWeightedFlowRate(Integer identifier, Instant currentTime);

    public void evaluateFlowRate(Integer identifier, Double rainfall, Double evaporation, Instant currentTime,
                                 RunoffSolver runoffSolver, Double subareaSlope, Double characteristicWidth) {
        Double tempPrecipitation = rainfall;
        if (subareaConnections != null) {
            tempPrecipitation = null;
            for (Subarea connections : subareaConnections) {
                connections.evaluateFlowRate(identifier, rainfall, evaporation, currentTime,
                        runoffSolver, subareaSlope, characteristicWidth);
                tempPrecipitation += connections.getWeightedFlowRate(identifier, currentTime);
                //infiltration
            }
            tempPrecipitation /= subareaArea;
        }
        evaluateNextStep(identifier, currentTime, runoffSolver, tempPrecipitation, evaporation,
                subareaSlope, characteristicWidth);
    }


    /**
     * Method to evaluate the totalDepth, the runoffDepth and the flowRate at the node.
     * It takes into account the type of Subarea through override.
     */
    abstract void evaluateNextStep(Integer identifier, Instant currentTime, RunoffSolver runoffSolver, Double rainfall,
                                   Double evaporation, Double subareaArea, Double characteristicWidth);

    /**
     * Evaluate the flowrate into the node from currentDepth and area properties
     *
     * @param subareaSlope
     * @param characteristicWidth
     * @param currentDepth
     * @return the evaluated flow rate
     */
    abstract Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth);

    void runoffODEsolver(Integer id, Instant currentTime, Instant nextTime, Double rainfall, RunoffSolver runoffSolver) {
        double[] inputValues = new double[1];
        inputValues[0] = runoffDepth.get(id).get(currentTime);
        double[] outputValues = new double[1];

        Double initialTime = (double) currentTime.getEpochSecond();
        Double finalTime = (double) nextTime.getEpochSecond();

        runoffSolver.setOde(rainfall, depthFactor);

        runoffSolver.getFirstOrderIntegrator().integrate(runoffSolver.getOde(), initialTime, inputValues,
                finalTime, outputValues);

        setRunoffDepth(id, nextTime, outputValues[0]);
        setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + (outputValues[0] - inputValues[0]));
    }
}