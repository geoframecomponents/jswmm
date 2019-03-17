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

package com.github.geoframecomponents.jswmm.runoff;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import oms3.annotations.*;
import com.github.geoframecomponents.jswmm.dataStructure.hydrology.rainData.RaingageSetup;

import java.time.Instant;
import java.util.*;

public class PreRunoff {

    @In
    public String areaName = null;

    private Long runoffStepSize;

    private Long rainfallStepSize;

    private Instant initialTime;

    private Instant totalTime;

    private LinkedHashMap<Instant, Double> rainfallData;

    @In
    public Double aLPP = 60.4;

    @In
    public Double nLPP = 0.61;

    @In
    public Integer numberOfCurves = 3;

    @In
    public Long stormwaterInterval = null;

    @InNode
    @Out
    public SWMMobject dataStructure;

    @Out
    public HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData = new HashMap<>();

    @Out
    public LinkedHashMap<Instant, Double> adaptedInfiltrationData;

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getAdaptedRainfallData() {
        return adaptedRainfallData;
    }

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        if(dataStructure != null) {

            //this.dataStructure = dataStructure;

            //TODO ocio al raingage
            RaingageSetup raingage = dataStructure.getRaingage("RG1");

            this.runoffStepSize = dataStructure.getRunoffSetup().getRunoffStepSize();
            this.rainfallStepSize = raingage.getRainfallStepSize();
            this.initialTime = dataStructure.getTimeSetup().getStartDate();
            this.totalTime = dataStructure.getTimeSetup().getEndDate();

            if(aLPP == null && nLPP == null) {
                String stationRaingage = raingage.getStationName();
                this.rainfallData = raingage.getReadDataFromFile().get(stationRaingage);
                adaptedRainfallData.put( 1, dataStructure.adaptDataSeries(runoffStepSize, rainfallStepSize,
                        totalTime.getEpochSecond(), initialTime.getEpochSecond(), rainfallData) );
            }
            else{
                for (int rainfallTimeId = 1; rainfallTimeId <= numberOfCurves; rainfallTimeId++) {
                    adaptedRainfallData.put(rainfallTimeId, dataStructure.adaptDataSeries(runoffStepSize,
                            rainfallStepSize, totalTime.getEpochSecond(), initialTime.getEpochSecond(),
                            generateRainfall().get(rainfallTimeId)) );
                }
            }
        }
        else {
            throw new NullPointerException("Data structure is null");//TODO
        }

        //adaptInfiltrationData();
    }

    private HashMap<Integer, LinkedHashMap<Instant, Double>> generateRainfall() {

        HashMap<Integer, LinkedHashMap<Instant, Double>> rainfallData = new HashMap<>();

        Long rainfallTimeInterval;
        if (stormwaterInterval == null) {
            rainfallTimeInterval = ( totalTime.getEpochSecond() - initialTime.getEpochSecond() ) / numberOfCurves;
        }
        else {
            rainfallTimeInterval = stormwaterInterval / numberOfCurves;
        }

        Instant finalRainfallTime = initialTime;
        //System.out.println("Number of rainfall times: " + numberOfCurves);
        for (int rainfallTimeId = 1; rainfallTimeId <= numberOfCurves; rainfallTimeId++) {

            finalRainfallTime = finalRainfallTime.plusSeconds( rainfallTimeInterval );

            LinkedHashMap<Instant, Double> rainfallValues = new LinkedHashMap<>();

            for (Long currentTime = initialTime.getEpochSecond(); currentTime<=totalTime.getEpochSecond(); currentTime+=rainfallStepSize) {

//              rainfallValues.put(Instant.ofEpochSecond(currentTime),
//                        constantRainfallData(finalRainfallTime.minusSeconds(initialTime.getEpochSecond()),
//                        Instant.ofEpochSecond(currentTime).minusSeconds(initialTime.getEpochSecond())) );
                if (rainfallTimeId == 1) {
                    rainfallValues.put(Instant.ofEpochSecond(currentTime),
                            constantRainfallData(Instant.ofEpochSecond(180L),
                                    Instant.ofEpochSecond(currentTime).minusSeconds(initialTime.getEpochSecond())) );
                }
                if (rainfallTimeId == 2) {
                    rainfallValues.put(Instant.ofEpochSecond(currentTime),
                            constantRainfallData(Instant.ofEpochSecond(300L),
                                    Instant.ofEpochSecond(currentTime).minusSeconds(initialTime.getEpochSecond())) );
                }
                if (rainfallTimeId == 3) {
                    rainfallValues.put(Instant.ofEpochSecond(currentTime),
                            constantRainfallData(Instant.ofEpochSecond(600L),
                                    Instant.ofEpochSecond(currentTime).minusSeconds(initialTime.getEpochSecond())) );
                }

            }

            //TODO nullo
            rainfallData.put( rainfallTimeId, rainfallValues );

        }

        /*for (Map.Entry<Integer, LinkedHashMap<Instant, Double>> entry : rainfallData.entrySet()) {
            LinkedHashMap<Instant, Double> val = entry.getValue();
            for (Instant time : val.keySet() ) {
                System.out.println(time);
                System.out.println(val.get(time));
            }
        }*/

        return rainfallData;
    }

    private Double constantRainfallData(Instant finalRainfallTime, Instant currentTime) {

        Double rainfallValue = 0.0;

        if ( currentTime.isBefore(finalRainfallTime) ) {
            double timeInHours = finalRainfallTime.getEpochSecond() / 3600.0;
            if (timeInHours == 0.0) {
                timeInHours = 1.0;//TODO better way?
            }
            rainfallValue = aLPP * Math.pow(timeInHours, nLPP - 1.0) / 3600.0; // [mm/s]
        }

        return rainfallValue;
    }
}