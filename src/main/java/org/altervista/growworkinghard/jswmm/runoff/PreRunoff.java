package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Initialize;
import oms3.annotations.Out;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.RaingageSetup;

import java.time.Instant;
import java.util.*;

public class PreRunoff {

    @In
    String areaName;

    @In
    public Long runoffStepSize;

    @In
    public Long rainfallStepSize;

    @In
    Instant initialTime;

    @In
    Instant totalTime;

    @In
    LinkedHashMap<Instant, Double> rainfallData;

    @In
    SWMMobject dataStructure = null;

    @Out
    LinkedHashMap<Instant, Double> adaptedRainfallData;

    @Out
    LinkedHashMap<Instant, Double> adaptedInfiltrationData;

    @Initialize
    void initializePreRunoff() {
        if(dataStructure != null) {
            RaingageSetup raingage = dataStructure.getRaingages().get(areaName);

            this.runoffStepSize = dataStructure.getRunoffSetup().getRunoffStepSize();
            this.rainfallStepSize = raingage.getRainfallStepSize();
            this.initialTime = dataStructure.getTimeSetup().getStartDate();
            this.totalTime = dataStructure.getTimeSetup().getEndDate();

            String stationRaingage = raingage.getStationName();
            this.rainfallData = raingage.getReadDataFromFile().get(stationRaingage);
        }
    }

    @Execute
    public void run() {
        adaptedRainfallData = adaptRainfallData(runoffStepSize, rainfallStepSize, totalTime.getEpochSecond(),
                initialTime.getEpochSecond(), rainfallData);
        //adaptInfiltrationData();
    }


    /**
     * Adapt runoff stepsize to total time
     */
    //private Double adaptRunoffStepSize(Long runoffStepSize, Long totalTime) {
    //    Long tempFactor = totalTime/runoffStepSize;
    //    return totalTime / (double)tempFactor;
    //}

    /**
     * Adapt rainfall data
     */
    private LinkedHashMap<Instant, Double> adaptRainfallData(Long runoffStepSize, Long rainfallStepSize, Long totalTime,
                                                             Long initialTime, LinkedHashMap<Instant, Double> rainfallData) {

        LinkedHashMap<Instant, Double> adaptedRainfallData = new LinkedHashMap<>();


        Long currentRainfallTime = initialTime;

        for (Long currentRunoffTime = initialTime; currentRunoffTime<totalTime; currentRunoffTime+=runoffStepSize) {

            while(currentRainfallTime <= currentRunoffTime) {
                currentRainfallTime += rainfallStepSize;
            }
            Long upperTime = currentRainfallTime;
            Long lowerTime = upperTime - rainfallStepSize;

            Double currentRainfall = interpolateRainfall(currentRunoffTime, lowerTime, rainfallData.get(lowerTime),
                    upperTime, rainfallData.get(upperTime));

            adaptedRainfallData.put(Instant.ofEpochSecond(currentRunoffTime), currentRainfall);
        }
        adaptedRainfallData.put(Instant.ofEpochSecond(totalTime), rainfallData.get(totalTime));

        return adaptedRainfallData;
    }

    private Double interpolateRainfall(Long currentRunoffTime, Long lowerTime, Double lowerTimeData, Long upperTime, Double upperTimeData) {
        Long rangeTime = upperTime - lowerTime;

        Long numerator  = rangeTime*(currentRunoffTime - lowerTime);

        if( upperTimeData == null ) { upperTimeData = 0.0; }
        if( lowerTimeData == null ) { lowerTimeData = 0.0; }

        Double denominator = upperTimeData - lowerTimeData;

        return lowerTimeData + numerator/denominator;
    }
}