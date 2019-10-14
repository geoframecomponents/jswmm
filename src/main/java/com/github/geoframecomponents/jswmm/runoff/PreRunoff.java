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
import com.github.geoframecomponents.jswmm.dataStructure.formatData.readData.DataCollector;
import oms3.annotations.*;

import java.time.Instant;
import java.util.*;


/**
 * PreRunoff
 */


public class PreRunoff {

    /**
     * Name of the area for the runoff process
     * TODO use name of the area to select the raingage
     */
    @In
    public String areaName = null;

    /**
     * Step size used by runoff evaluator
     */
    private Long runoffStepSize;

    /**
     * Step size of the rainfall data
     */
    private Long rainfallStepSize;

    /**
     * Define the starting data/time simulation
     */
    private Instant initialTime;

    /**
     * Define the ending date/time simulation
     */
    private Instant totalTime;

    /**
     * Set of rainfall data over time
     */
    private LinkedHashMap<Instant, Double> rainfallData;

    /**
     * Coefficient $$a$$ of the equation $$I=a*tp^(n-1)$$
     */
    @In
    public Double aLPP = 60.4;

    /**
     * Coefficient $$n$$ of the equation $$I=a*tp^(n-1)$$
     */
    @In
    public Double nLPP = 0.61;

    /**
     * Number of curves to design the network
     * TODO must be global otherwise it crash? Please verify.
     */
    @In
    public Integer numberOfCurves = 3;

    //@In
    //public Long stormwaterInterval = null;

    @InNode
    @Out
    public SWMMobject dataStructure;

    /**
     * Rainfall data adapted over the runoff step size
     */
    @Out
    public HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData = new HashMap<>();

    /**
     * Infiltration data adapted over the runoff step size
     */
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

            DataCollector raingage = dataStructure.getAreas(areaName).getDataFromFile();

            this.runoffStepSize = dataStructure.getRunoffOptions().getRunoffStepSize();
            this.rainfallStepSize = raingage.getDatasetStepSize();
            this.initialTime = dataStructure.getDatetimeable().getProjectDate("initial");
            this.totalTime = dataStructure.getDatetimeable().getProjectDate("final");

            if(aLPP == null && nLPP == null) {
                String stationRaingage = raingage.getDatasetName();
                this.rainfallData = raingage.getDatasetData().get(stationRaingage);
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

    /**
     * Method to generate the curves to design from IDFs curves
     * @return HM with the ID of the curve as key and the HM of the design storm as value
     */
    private HashMap<Integer, LinkedHashMap<Instant, Double>> generateRainfall() {

        HashMap<Integer, LinkedHashMap<Instant, Double>> rainfallData = new HashMap<>();

        /*Long rainfallTimeInterval;
        if (stormwaterInterval == null) {
            rainfallTimeInterval = ( totalTime.getEpochSecond() - initialTime.getEpochSecond() ) / numberOfCurves;
        }
        else {
            rainfallTimeInterval = stormwaterInterval / numberOfCurves;
        }
         */

        //Instant finalRainfallTime = initialTime;
        //System.out.println("Number of rainfall times: " + numberOfCurves);
        for (int rainfallTimeId = 1; rainfallTimeId <= numberOfCurves; rainfallTimeId++) {

            //finalRainfallTime = finalRainfallTime.plusSeconds( rainfallTimeInterval );

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

    /**
     * Method to generate the constant intensity from IDFs curves
     * @param finalRainfallTime
     * @param currentTime
     * @return IDF related value of rainfall time
     */
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