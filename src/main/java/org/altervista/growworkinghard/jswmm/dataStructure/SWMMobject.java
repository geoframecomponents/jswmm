package org.altervista.growworkinghard.jswmm.dataStructure;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject.AbstractNode;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.AbstractRaingage;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.AbstractSubarea;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.AbstractSubcatchments;

public class SWMMobject {
    AbstractFilesData filesData;
    AbstractTimeseriesData timeseriesData;

    AbstractRaingage raingages;

    AbstractNode nodes;
    AbstractLinks links;
    AbstractSubcatchments subcatchments;
    AbstractSubarea subareas;
}
