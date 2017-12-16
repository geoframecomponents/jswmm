package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

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