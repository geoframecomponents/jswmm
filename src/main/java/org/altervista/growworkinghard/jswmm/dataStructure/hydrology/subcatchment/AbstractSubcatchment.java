package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.RaingageSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.AbstractReceiver;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

import java.time.Instant;
import java.util.LinkedHashMap;

public abstract class AbstractSubcatchment extends AbstractReceiver {

    ReadDataFromFile readDataFromFile;
    AcquiferSetup acquiferSetup;
    SnowPackSetup snowpack;
    ProjectUnits subcatchmentUnits;

    RaingageSetup raingageSetup;

    String subcatchmentName;
    Double subcatchmentArea;

    LinkedHashMap<Instant, Double> runoffInflow;

    public LinkedHashMap<Instant, Double> getRunoffInflow() {
        return runoffInflow;
    }
}