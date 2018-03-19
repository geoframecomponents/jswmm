/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.RaingageSetup;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class PreRunoff extends LinkedHashMap<Instant, Double> {

    @In
    public String areaName = null;

    private Long runoffStepSize;

    private Long rainfallStepSize;

    private Instant initialTime;

    private Instant totalTime;

    private LinkedHashMap<Instant, Double> rainfallData;

    @InNode
    @Out
    public SWMMobject dataStructure;

    @Out
    public LinkedHashMap<Instant, Double> adaptedRainfallData;

    @Out
    public LinkedHashMap<Instant, Double> adaptedInfiltrationData;

    public LinkedHashMap<Instant, Double> getAdaptedRainfallData() {
        return adaptedRainfallData;
    }

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        if(dataStructure != null) {

            //this.dataStructure = dataStructure;

            RaingageSetup raingage = dataStructure.getRaingages().get(areaName);

            this.runoffStepSize = dataStructure.getRunoffSetup().getRunoffStepSize();
            this.rainfallStepSize = raingage.getRainfallStepSize();
            this.initialTime = dataStructure.getTimeSetup().getStartDate();
            this.totalTime = dataStructure.getTimeSetup().getEndDate();

            String stationRaingage = raingage.getStationName();
            this.rainfallData = raingage.getReadDataFromFile().get(stationRaingage);
        }
        else {
            throw new NullPointerException();//TODO
        }

        adaptedRainfallData = adaptRainfallData(runoffStepSize, rainfallStepSize, totalTime.getEpochSecond(),
                initialTime.getEpochSecond(), rainfallData);
        //adaptInfiltrationData();
    }


    /**
     * Adapt runoffDS stepsize to total time
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

        for (Long currentTime = initialTime; currentTime<totalTime; currentTime+=runoffStepSize) {

            while(currentRainfallTime <= currentTime) {
                currentRainfallTime += rainfallStepSize;
            }

            Long upperTime = currentRainfallTime;
            Double upperRainfallData = 0.0;
            if(rainfallData.get(Instant.ofEpochSecond(upperTime)) != null) {
                upperRainfallData = rainfallData.get(Instant.ofEpochSecond(upperTime));
            }

            Long lowerTime = upperTime - rainfallStepSize;
            Double lowerRainfallData = 0.0;
            if(rainfallData.get(Instant.ofEpochSecond(lowerTime)) != null) {
                lowerRainfallData = rainfallData.get(Instant.ofEpochSecond(lowerTime));
            }

            Double currentRainfall = interpolateRainfall(currentTime, lowerTime, lowerRainfallData, upperTime, upperRainfallData);

            adaptedRainfallData.put(Instant.ofEpochSecond(currentTime), currentRainfall);
        }
        adaptedRainfallData.put(Instant.ofEpochSecond(totalTime), rainfallData.get(Instant.ofEpochSecond(totalTime)));

        return adaptedRainfallData;
    }

    private Double interpolateRainfall(Long currentRunoffTime, Long lowerTime, Double lowerTimeData, Long upperTime, Double upperTimeData) {
        Long rangeTime = upperTime - lowerTime;

        if( rangeTime == 0 ) { return lowerTimeData; }
        else {
            if (upperTimeData == null) {
                upperTimeData = 0.0;
            }
            if (lowerTimeData == null) {
                lowerTimeData = 0.0;
            }

            Double numerator = upperTimeData - lowerTimeData;

            return lowerTimeData + numerator / rangeTime * (currentRunoffTime - lowerTime);
        }
    }
}