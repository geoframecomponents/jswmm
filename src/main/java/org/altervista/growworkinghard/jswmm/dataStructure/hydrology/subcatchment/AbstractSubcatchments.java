package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

public abstract class AbstractSubcatchments {
    String subcatchmentID;
    String raingaigeID;
    String outputToNode;
    String outputToSubcatchment;

    Double area;
    Double idealWidth;
    Double slope;

    Double imperviousPercentage;

    Double[] rainfall;
    Double[] evaporation;
    Double[] infiltration;

    Double[] runonFromSubcatchment;

    Double curbLength;
    Double initialBuildup;

}
