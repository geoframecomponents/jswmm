package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.util.List;

public class ImperviousWithoutStorage extends Subarea {

    Double totalImperviousArea;

    public ImperviousWithoutStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea, Double roughnessCoefficient) {
        this(imperviousWStorageArea, imperviousWOStorageArea, roughnessCoefficient, null);
    }

    public ImperviousWithoutStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                 Double roughnessCoefficient, List<Subarea> connections) {
        this.subareaArea = imperviousWStorageArea;
        this.totalImperviousArea = imperviousWStorageArea + imperviousWOStorageArea;
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