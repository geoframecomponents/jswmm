package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.writeData.WriteDataToFile;

import java.time.Instant;
import java.util.LinkedHashMap;

public class Outfall extends AbstractNode {

    Double fixedStage;//TODO verify from where
    LinkedHashMap<Instant, Double> tidalCurve;
    LinkedHashMap<Instant, Double> stageTimeseries;
    boolean gated;
    String routeTo;

    //TODO solve the conflict with tidal/timeseries

    public Outfall(ReadDataFromFile readDataFromFile, WriteDataToFile writeDataToFile, ExternalInflow dryWeatherInflow,
                   ExternalInflow rainfallDependentInfiltrationInflow, ProjectUnits nodeUnits,
                   String nodeName, Double nodeElevation, Double fixedStage, LinkedHashMap<Instant, Double> tidalCurve,
                   LinkedHashMap<Instant, Double> stageTimeseries, boolean gated, String routeTo) {

        super(readDataFromFile, writeDataToFile, dryWeatherInflow, rainfallDependentInfiltrationInflow, nodeUnits, nodeName, nodeElevation);

        this.fixedStage = fixedStage;
        this.tidalCurve = tidalCurve;
        this.stageTimeseries = stageTimeseries;
        this.gated = gated;
        this.routeTo = routeTo;
    }
}
