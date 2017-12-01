package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.writeData.WriteDataToFile;

public class Storage extends AbstractNode {
    public Storage(ReadDataFromFile readDataFromFile, WriteDataToFile writeDataToFile, DryWeatherInflow dryWeatherInflow, RainfallDependentInfiltrationInflow rainfallDependentInfiltrationInflow, ProjectUnits nodeUnits, String nodeName, Double nodeElevation) {
        super(readDataFromFile, writeDataToFile, dryWeatherInflow, rainfallDependentInfiltrationInflow, nodeUnits, nodeName, nodeElevation);
    }
}
