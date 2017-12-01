package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

public abstract class AbstractSubcatchments {

    ReadDataFromFile readDataFromFile;
    AcquiferSetup acquiferSetup;
    SnowPackSetup snowpack;
    ProjectUnits subcatchmentUnits;

    String subcatchmentName;
    Double subcatchmentArea;

    public AbstractSubcatchments(ReadDataFromFile readDataFromFile, AcquiferSetup acquiferSetup, SnowPackSetup snowpack,
                                 ProjectUnits subcatchmentUnits, String subcatchmentName, Double subcatchmentArea) {

        this.readDataFromFile = readDataFromFile;
        this.acquiferSetup = acquiferSetup;
        this.snowpack = snowpack;
        this.subcatchmentUnits = subcatchmentUnits;
        this.subcatchmentName = subcatchmentName;
        this.subcatchmentArea = subcatchmentArea;
    }
}
