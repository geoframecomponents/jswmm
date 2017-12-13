package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.runoff.RunoffODE;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Pervious extends Subarea {

    Double infiltration;

    LinkedHashMap<Instant, Double> depressionStorage;

    public Pervious(Double subareaArea, Double roughnessCoefficient, FirstOrderIntegrator firstOrderIntegrator) {
        this(subareaArea, roughnessCoefficient, null, null, firstOrderIntegrator);
    }

    public Pervious(Double subareaArea, Double roughnessCoefficient, Double percentageRouted, List<Subarea> connections,
                    FirstOrderIntegrator firstOrderIntegrator) {

        this.subareaArea = subareaArea;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = connections;
        this.firstOrderIntegrator = firstOrderIntegrator;
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        if (!subareaConnections.isEmpty()) {
            for (Subarea connections : subareaConnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);
                this.depthFactor = Math.pow(subareaSlope, 0.5) *
                        characteristicWidth / (roughnessCoefficient * subareaArea);
            }
        }
    }

    @Override
    Double getWeightedFlowRate(Instant currentTime) {
        return flowRate.get(currentTime) * subareaArea * percentageRouted;
    }

    @Override
    void evaluateNextDepth(Instant currentTime, long runoffStepSize, Double rainfall, Double evaporation) {

        Instant nextTime = currentTime.plus(runoffStepSize, SECONDS);
        Double moistureVolume = rainfall * runoffStepSize +
                runoffDepth.get(currentTime) + depressionStorage.get(currentTime);

        evaporation = Math.max(evaporation, (runoffDepth.get(currentTime) + depressionStorage.get(currentTime))/runoffStepSize);

        excessRainfall = rainfall - evaporation - infiltration;

        if(evaporation * runoffStepSize >= moistureVolume) {
            runoffDepth.put(nextTime, 0.0);
            flowRate.put(nextTime, 0.0);
        }
        else {
            if(excessRainfall <= depressionStorage.get(currentTime)) {
                runoffDepth.put(nextTime, runoffDepth.get(currentTime) + rainfall * runoffStepSize);
                flowRate.put(nextTime, 0.0);
            }
            else {
                runoffODEsolver(currentTime, nextTime, rainfall);
            }
        }
    }
}