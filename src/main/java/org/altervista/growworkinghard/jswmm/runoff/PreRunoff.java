package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import java.time.Instant;
import java.util.*;

public class PreRunoff {

    @In
    //SWMMobject dataStructure;
    public Long runoffStepSize;
    Long rainfallStepSize;

    Long initialTime;
    Long totalTime;

    LinkedHashMap<Instant, Double> rainfallData;

    //@Out
    //Double adaptedRunoffStepSize;

    @Out
    LinkedHashMap<Instant, Double> adaptedRainfallData;

    @Out
    LinkedHashMap<Instant, Double> adaptedInfiltrationData;

    @Execute
    public void run() {
        adaptedRainfallData = adaptRainfallData(runoffStepSize, rainfallStepSize, totalTime, initialTime, rainfallData);
        //adaptInfiltrationData();
    }


    /**
     * Adapt runoff stepsize to total time
     */
    //private Double adaptRunoffStepSize(long runoffStepSize, long totalTime) {
    //    long tempFactor = totalTime/runoffStepSize;
    //    return totalTime / (double)tempFactor;
    //}

    /**
     * Adapt rainfall data
     */
    private LinkedHashMap<Instant, Double> adaptRainfallData(long runoffStepSize, long rainfallStepSize, long totalTime,
                                                             long initialTime, LinkedHashMap<Instant, Double> rainfallData) {

        LinkedHashMap<Instant, Double> adaptedRainfallData = new LinkedHashMap<>();


        long currentRainfallTime = initialTime;

        for (long currentRunoffTime = initialTime; currentRunoffTime<totalTime; currentRunoffTime+=runoffStepSize) {

            while(currentRainfallTime <= currentRunoffTime) {
                currentRainfallTime += rainfallStepSize;
            }
            long upperTime = currentRainfallTime;
            long lowerTime = upperTime - rainfallStepSize;

            Double currentRainfall = interpolateRainfall(currentRunoffTime, lowerTime, rainfallData.get(lowerTime),
                    upperTime, rainfallData.get(upperTime));

            adaptedRainfallData.put(Instant.ofEpochSecond(currentRunoffTime), currentRainfall);
        }
        adaptedRainfallData.put(Instant.ofEpochSecond(totalTime), rainfallData.get(totalTime));

        return adaptedRainfallData;
    }

    private Double interpolateRainfall(long currentRunoffTime, long lowerTime, Double lowerTimeData, long upperTime, Double upperTimeData) {
        long rangeTime = upperTime - lowerTime;

        long numerator  = rangeTime*(currentRunoffTime - lowerTime);
        Double denominator = upperTimeData - lowerTimeData;

        return lowerTimeData + numerator/denominator;
    }
}