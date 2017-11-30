package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.writeData.WriteDataToFile;
import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;

public abstract class AbstractNode {

    ReadDataFromFile readDataFromFile;
    WriteDataToFile writeDataToFile;
    DryWeatherInflow dryWeatherInflow;
    RainfallDependentInfiltrationInflow rainfallDependentInfiltrationInflow;

    ProjectUnits nodeUnits;

    String nodeName;
    Double nodeElevation;
}
