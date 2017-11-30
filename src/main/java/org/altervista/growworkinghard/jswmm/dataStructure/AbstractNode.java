package org.altervista.growworkinghard.jswmm.dataStructure;

public abstract class AbstractNode {

    ReadDataFromFile readDataFromFile;
    WriteDataToFile writeDataToFile;
    DryWeatherFlow dryWeatherInlow;
    RainfallDependentInfiltrationInflow rainfallDependentInfiltrationInflow;

    ProjectUnits nodeUnits;

    String nodeName;
    Double nodeElevation;
}
