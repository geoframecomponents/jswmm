package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.util.List;

public class Pervious extends Subarea {

    Double infiltrationData;
    Double depressionStorage;

    public Pervious(Double subareaArea, Double roughnessCoefficient, Double depressionStorage) {
        this(subareaArea, depressionStorage, roughnessCoefficient,null);
    }

    public Pervious(Double subareaArea, Double depressionStorage,
                    Double roughnessCoefficient, List<Subarea> connections) {

        this.subareaArea = subareaArea;
        this.depressionStorage = depressionStorage;
        this.roughnessCoefficient = roughnessCoefficient;
        this.subareaConnnections = connections;
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        if (!subareaConnnections.isEmpty()) {
            for (Subarea connections : subareaConnnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);
                this.depthFactor = Math.pow(subareaSlope, 0.5) *
                        characteristicWidth / (roughnessCoefficient * subareaArea);
            }
        }
    }
}