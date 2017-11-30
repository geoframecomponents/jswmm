package org.altervista.growworkinghard.jswmm.dataStructure;

public abstract class AbstractNodes {

    ReadDataFromFile readDataFromFile;
    WriteDataToFile writeDataToFile;
    DryWeatherFlow dryWeatherFlow;
    RainfallDependentInfiltrationInflow rainfallDependentInfiltrationInflow;

    ProjectUnits nodeUnits;

    String nodeName;
    Double nodeElevation;
}
