package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.writeData.WriteDataToFile;
import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;

public abstract class AbstractNode {

    ReadDataFromFile readDataFromFile;
    WriteDataToFile writeDataToFile;
    ExternalInflow dryWeatherInflow;
    ExternalInflow rainfallDependentInfiltrationInflow;

    ProjectUnits nodeUnits;

    String nodeName;
    Double nodeElevation;

    Double[] nodeDepth;

    public AbstractNode(ReadDataFromFile readDataFromFile, WriteDataToFile writeDataToFile, ExternalInflow dryWeatherInflow,
                        ExternalInflow rainfallDependentInfiltrationInflow, ProjectUnits nodeUnits, String nodeName,
                        Double nodeElevation, Double[] nodeDepth) {

        this.readDataFromFile = readDataFromFile;
        this.writeDataToFile = writeDataToFile;
        this.dryWeatherInflow = dryWeatherInflow;
        this.rainfallDependentInfiltrationInflow = rainfallDependentInfiltrationInflow;
        this.nodeUnits = nodeUnits;
        this.nodeName = nodeName;
        this.nodeElevation = nodeElevation;
        this.nodeDepth = nodeDepth;
    }
}
