package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Subarea {

    Double subareaArea;
    Double depthFactor;
    Double roughnessCoefficient;
    Double percentageRouted;
    Double depressionStorage;

    List<Subarea> subareaConnections;

    LinkedHashMap<Instant, Double> totalDepth;
    LinkedHashMap<Instant, Double> runoffDepth;
    LinkedHashMap<Instant, Double> flowRate;
    Double excessRainfall;

    public void setTotalDepth(Instant time, Double depthValue) {
        this.totalDepth.put(time, depthValue);
    }

    public void setRunoffDepth(Instant time, Double depthValue) {
        this.runoffDepth.put(time, depthValue);
    }

    public void setFlowRate(Instant time, Double flowValue) {
        this.flowRate.put(time, flowValue);
    }

    public abstract void setDepthFactor(Double subareaSlope, Double characteristicWidth);

    abstract Double getWeightedFlowRate(Instant currentTime);

    public void evaluateFlowRate(Double rainfall, Double evaporation, Instant currentTime,
                                 RunoffSetup runoffSetup, Double subareaSlope, Double characteristicWidth) {

        Double tempPrecipitation = rainfall;
        if (subareaConnections != null) {
            tempPrecipitation = null;
            for (Subarea connections : subareaConnections) {
                connections.evaluateFlowRate(rainfall, evaporation, currentTime,
                        runoffSetup, subareaSlope, characteristicWidth);
                tempPrecipitation += connections.getWeightedFlowRate(currentTime);
                //infiltration
            }
            tempPrecipitation /= subareaArea;
        }
        evaluateNextStep(currentTime, runoffSetup, tempPrecipitation, evaporation, subareaSlope, characteristicWidth);
    }

    abstract void evaluateNextStep(Instant currentTime, RunoffSetup runoffSetup, Double rainfall, Double evaporation,
                                   Double subareaArea, Double characteristicWidth);

    void runoffODEsolver(Instant currentTime, Instant nextTime, Double rainfall, RunoffSetup runoffSetup) {
        double[] inputValues = new double[1];
        inputValues[0] = runoffDepth.get(currentTime);
        double[] outputValues = new double[1];

        Double initialTime = (double) currentTime.getEpochSecond();
        Double finalTime = (double) nextTime.getEpochSecond();

        runoffSetup.setOde(rainfall, depthFactor);
        runoffSetup.getFirstOrderIntegrator().integrate(runoffSetup.getOde(), initialTime, inputValues, finalTime, outputValues);

        runoffDepth.put(nextTime, outputValues[0]);
        totalDepth.put(nextTime, totalDepth.get(currentTime) + (outputValues[0]-inputValues[0]));
    }

    abstract Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth);
}