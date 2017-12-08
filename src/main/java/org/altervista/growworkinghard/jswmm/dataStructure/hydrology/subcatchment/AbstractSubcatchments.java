package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

import java.util.List;

public abstract class AbstractSubcatchments {

    ReadDataFromFile readDataFromFile;
    AcquiferSetup acquiferSetup;
    SnowPackSetup snowpack;
    ProjectUnits subcatchmentUnits;

    String subcatchmentName;
    Double subcatchmentArea;
}
/*
    public void evaluateSubareaDepth(SubareaSetup subarea, Instant currentTime, long runoffStepSize) {

        Instant nextTime = currentTime.plus(runoffStepSize, SECONDS);
        Double perviousMoistureVolume = subarea.rainfallData.get(currentTime)*runoffStepSize + perviousDepth.get(currentTime);

        perviousEvaporationData.put(currentTime, Math.max(perviousEvaporationData.get(currentTime), perviousDepth.get(currentTime)/runoffStepSize));

        //upgradeInfiltrationOnPervious(rainfall, depth)

        if((perviousEvaporationData.get(currentTime) + perviousInfiltrationData.get(currentTime))*runoffStepSize >= perviousMoistureVolume) {
            perviousDepth.put(nextTime, 0.0);
            perviousFlowRate.put(nextTime, 0.0);
        }
        else {

            perviousExcessRainfall = perviousRainfallData.get(currentTime) - perviousEvaporationData.get(currentTime) -
                    perviousInfiltrationData.get(currentTime);

            if(perviousExcessRainfall <= perviousStorage) {
                perviousDepth.put(nextTime, perviousDepth.get(currentTime) + perviousRainfallData.get(currentTime)*runoffStepSize);
                perviousFlowRate.put(nextTime, 0.0);
            }

            if(perviousExcessRainfall > perviousStorage) {
                Double nextDepth = evaluateFlowRate(perviousRainfallData.get(currentTime),
                        perviousEvaporationData.get(currentTime), perviousInfiltrationData.get(currentTime),
                        perviousDepthFactor, currentTime, nextTime, perviousDepth.get(currentTime));

                perviousDepth.put(nextTime, nextDepth);
                upgradeFlowRateRunoff(); //TODO
            }
        }

    }
    */
