package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

abstract class AbstractSubarea extends AbstractSubcatchments {

    SubareaSetup subareaSetup;

    String raingageName;
    String receiverRunoffName;

    Double imperviousPercentage;
    Double characteristicWidth;
    Double subareaSlope;
    Double curbLength;
}
