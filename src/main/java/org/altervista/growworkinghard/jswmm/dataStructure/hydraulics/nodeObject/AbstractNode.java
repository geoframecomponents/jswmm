package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.writeData.WriteDataToFile;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.AbstractReceiver;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;

import java.time.Instant;
import java.util.LinkedHashMap;

public abstract class AbstractNode extends AbstractReceiver {

    ReadDataFromFile readDataFromFile;
    WriteDataToFile writeDataToFile;
    ExternalInflow dryWeatherInflow;
    ExternalInflow rainfallDependentInfiltrationInflow;

    ProjectUnits nodeUnits;

    String nodeName;
    Double nodeElevation;
    LinkedHashMap<Instant, Double> runoffInflow;

    LinkedHashMap<Instant, Double> nodeDepth;

    public LinkedHashMap<Instant, Double> getRunoffInflow() {
        return runoffInflow;
    }
}
