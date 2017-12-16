package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;
import org.altervista.growworkinghard.jswmm.runoff.RunoffODE;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Pervious extends Subarea {

    Double infiltration = 0.0; //TODO temporary 0.0

    public Pervious(Double subareaArea, Double depressionStoragePervious, Double roughnessCoefficient) {
        this(subareaArea, depressionStoragePervious, roughnessCoefficient, null, null);
    }

    public Pervious(Double subareaArea, Double depressionStoragePervious, Double roughnessCoefficient,
                    Double percentageRouted, List<Subarea> connections) {

        this.subareaArea = subareaArea;
        this.depressionStorage = depressionStoragePervious;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = connections;

        this.totalDepth = new LinkedHashMap<>();
        this.runoffDepth = new LinkedHashMap<>();
        this.flowRate = new LinkedHashMap<>();
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        if (subareaConnections != null) {
            for (Subarea connections : subareaConnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);
                this.depthFactor = Math.pow(subareaSlope, 0.5) *
                        characteristicWidth / (roughnessCoefficient * subareaArea);
            }
        }
        else {
            this.depthFactor = (Math.pow(subareaSlope, 0.5) * characteristicWidth) / (roughnessCoefficient * subareaArea);
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

        Double moistureVolume = rainfall * runoffStepSize + totalDepth.get(currentTime);

        if(evaporation != 0.0) {
            evaporation = Math.max(evaporation, totalDepth.get(currentTime)/runoffStepSize);
        }
        //infiltration
        //excessRainfall = rainfall - evaporation - infiltration;

        excessRainfall = rainfall - evaporation;

        if(evaporation * runoffStepSize >= moistureVolume) {
            totalDepth.put(nextTime, totalDepth.get(currentTime) + 0.0);
            runoffDepth.put(nextTime, runoffDepth.get(currentTime) + 0.0);
            flowRate.put(nextTime, flowRate.get(currentTime) + 0.0);
        }
        else {
            if(excessRainfall * runoffStepSize <= depressionStorage - totalDepth.get(currentTime)) {
                totalDepth.put(nextTime, totalDepth.get(currentTime) + excessRainfall * runoffStepSize);
                runoffDepth.put(nextTime, runoffDepth.get(currentTime) + 0.0);
                flowRate.put(nextTime, flowRate.get(currentTime) + 0.0);
            }
            else {
                runoffODEsolver(currentTime, nextTime, excessRainfall, runoffSetup);
                flowRate.put( nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth, runoffDepth.get(nextTime)) );
            }
        }
    }

    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        return Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (subareaArea * roughnessCoefficient);
    }
}