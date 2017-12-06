package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.runoff.RunoffODE;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public abstract class Subarea {

    Double subareaArea;
    Double depthFactor;
    Double roughnessCoefficient;
    Double percentageRouted;

    List<Subarea> subareaConnections;

    LinkedHashMap<Instant, Double> runoffDepth;
    LinkedHashMap<Instant, Double> flowRate;
    Double excessRainfall;

    FirstOrderDifferentialEquations ode;
    FirstOrderIntegrator firstOrderIntegrator;

    public abstract void setDepthFactor(Double subareaSlope, Double characteristicWidth);

    abstract Double getWeightedFlowRate(Instant currentTime);

    public void evaluateFlowRate(Double rainfall, Double evaporation, Instant currentTime,
                                 long runoffStepSize, Double subareaSlope, Double characteristicWidth) {

        Double tempPrecipitation = null;
        if (!subareaConnections.isEmpty()) {
            for (Subarea connections : subareaConnections) {
                connections.evaluateFlowRate(rainfall, evaporation, currentTime,
                        runoffStepSize, subareaSlope, characteristicWidth);
                tempPrecipitation += connections.getWeightedFlowRate(currentTime);
                //infiltration
            }
            tempPrecipitation /= subareaArea;

            evaluateNextDepth(currentTime, runoffStepSize, tempPrecipitation, evaporation);

            flowRate.put(currentTime.plus(runoffStepSize, SECONDS),
                    evaluateNextFlowRate(subareaSlope, characteristicWidth,
                            runoffDepth.get(currentTime.plus(runoffStepSize, SECONDS))));
        }
    }

    abstract void evaluateNextDepth(Instant currentTime, long runoffStepSize, Double rainfall, Double evaporation);

    void runoffODEsolver(Instant currentTime, Instant nextTime, Double rainfall) {
        double[] inputValues = new double[0];
        inputValues[0] = flowRate.get(nextTime);
        double[] outputValues = new double[0];

        Double initialTime = (double) currentTime.getEpochSecond();
        Double finalTime = (double) nextTime.getEpochSecond();

        ode = new RunoffODE(rainfall, depthFactor);
        firstOrderIntegrator.integrate(ode, initialTime, inputValues, finalTime, outputValues);

        runoffDepth.put(nextTime, outputValues[0]);
    }

    private Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        return Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (subareaArea * roughnessCoefficient);
    }
}