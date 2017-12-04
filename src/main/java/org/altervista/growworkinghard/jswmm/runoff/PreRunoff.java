package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.In;
import oms3.annotations.Out;

import java.time.Instant;
import java.util.*;

public class PreRunoff {

    @In
    //SWMMobject dataStructure;
    long runoffStepSize;
    long rainfallStepSize;
    long totalTime;
    long initialTime;
    LinkedHashMap<Instant, Double> rainfallData;


    //@Out
    //Double adaptedRunoffStepSize;

    @Out
    LinkedHashMap<Instant, Double> adaptedRainfallData;

    @Out
    LinkedHashMap<Instant, Double> adaptedInfiltrationData;

    public void run() {
        adaptRainfallData(runoffStepSize, rainfallStepSize, totalTime, initialTime, rainfallData);
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



















/**
 * Size of the step used to
 */
    //@Out
    //Double runoffStepSize;

    /**
     * Rainfall data adapted to the runoff step size
     */
    //@Out
    //LinkedHashMap<Instant, Double> adaptedRainfallData;

    /**
     * Evaporation data adapted to the runoff step size
     */
    //@Out
    //Map<Double, Double> adaptedEvaporationData;


    //adaptRunoffStep();

    /**
     * Take the rainfall data from file and save it in an LinkedHashMap that has elapsed seconds as key and
     * rainfall value as value.
     * <p>
     * Based on FILE data of SWMM with the following structure:
     * ;Station   Year   Month   Day   Hour   Minutes   Value
     * STA01      2004     6     12     00      00      0.12
     */
    //void adaptRainfallData(LinkedHashMap<String, LinkedHashMap<Instant, Double>> rainfallFromFileData,
    //                       Double rainfallStepSize, String rainfallStation) {

    //    adaptedRainfallData = rainfallFromFileData.get(rainfallStation);
    //}

//      throws IOException {

//evaluateEvaporation();


/*

        for(Double seconds=0.0; seconds<=simulationTotalTime; seconds+=rainfallStepSize){
            while(seconds < nextDataTime){
                adaptedRainfallData.put(seconds, 0.0);
            }

        }*/
//readFileLines()
//transformTimeToSeconds()
//saveValue()