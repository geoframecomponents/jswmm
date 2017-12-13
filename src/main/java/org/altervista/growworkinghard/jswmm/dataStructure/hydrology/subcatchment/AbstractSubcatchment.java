package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.RaingageSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

public abstract class AbstractSubcatchment {

    ReadDataFromFile readDataFromFile;
    AcquiferSetup acquiferSetup;
    SnowPackSetup snowpack;
    ProjectUnits subcatchmentUnits;

    RaingageSetup raingageSetup;

    String subcatchmentName;
    Double subcatchmentArea;
}