package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.util.List;

public class ImperviousWithStorage extends Subarea {

    Double depressionStorage;
    Double totalImperviousArea;

    public ImperviousWithStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                 Double depressionStorage, Double roughnessCoefficient) {
        this(imperviousWStorageArea, imperviousWOStorageArea, depressionStorage, roughnessCoefficient, null);
    }

    public ImperviousWithStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                 Double depressionStorage, Double roughnessCoefficient, List<Subarea> connections) {
        this.subareaArea = imperviousWStorageArea;
        this.totalImperviousArea = imperviousWStorageArea + imperviousWOStorageArea;
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
                        characteristicWidth / (roughnessCoefficient * totalImperviousArea);
            }
        }
    }
}