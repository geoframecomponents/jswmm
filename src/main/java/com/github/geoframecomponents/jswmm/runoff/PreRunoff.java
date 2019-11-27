/*
 * JSWMM: Reimplementation of EPA SWMM in Java
 * Copyright (C) 2019 Daniele Dalla Torre (ftt01)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.geoframecomponents.jswmm.runoff;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import oms3.annotations.*;

import java.time.Instant;
import java.util.*;

/**
 * PreRunoff
 */
public class PreRunoff {

    /**
     * Name of the area for the runoff process
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
     */
    @In
    public Integer numberOfCurves;

    @InNode
    @Out
    public SWMMobject dataStructure;

    /**
     * Rainfall data adapted over the runoff step size
     */
    @Out
    public HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData = new HashMap<>();

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getAdaptedRainfallData() {
        return adaptedRainfallData;
    }

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        if(dataStructure != null) {

            this.initialTime = dataStructure.getProjectDateTime().getDateTime(AvailableDateTypes.startDate);
            this.totalTime = dataStructure.getProjectDateTime().getDateTime(AvailableDateTypes.endDate);

            this.runoffStepSize = dataStructure.getAreasDateTime().getDateTime(AvailableDateTypes.stepSize);
            this.rainfallStepSize = runoffStepSize;

            if(aLPP == null && nLPP == null) {
                throw new NullPointerException("Nothing implemented yet");
            }
            else{
                for (int rainfallTimeId = 1; rainfallTimeId <= numberOfCurves; rainfallTimeId++) {
                    adaptedRainfallData.put(rainfallTimeId, dataStructure.adaptDataSeries(runoffStepSize,
                            rainfallStepSize, generateRainfall().get(rainfallTimeId)) );
                }
            }
        }
        else {
            throw new NullPointerException("Data structure is null");//TODO
        }
    }

    /**
     * Method to generate the curves to design from IDFs curves
     * @return HM with the ID of the curve as key and the HM of the design storm as value
     */
    private HashMap<Integer, LinkedHashMap<Instant, Double>> generateRainfall() {

        HashMap<Integer, LinkedHashMap<Instant, Double>> rainfallData = new HashMap<>();

        for (int rainfallTimeId = 1; rainfallTimeId <= numberOfCurves; rainfallTimeId++) {

            LinkedHashMap<Instant, Double> rainfallValues = new LinkedHashMap<>();

            for (Long currentTime = initialTime.getEpochSecond(); currentTime<=totalTime.getEpochSecond(); currentTime+=rainfallStepSize) {

                if (rainfallTimeId == 1) {
                    rainfallValues.put(Instant.ofEpochSecond(currentTime),
                            constantRainfallData(Instant.ofEpochSecond(180L),
                                    Instant.ofEpochSecond(currentTime).minusSeconds(initialTime.getEpochSecond())) );
                }
                else if (rainfallTimeId == 2) {
                    rainfallValues.put(Instant.ofEpochSecond(currentTime),
                            constantRainfallData(Instant.ofEpochSecond(300L),
                                    Instant.ofEpochSecond(currentTime).minusSeconds(initialTime.getEpochSecond())) );
                }
                else if (rainfallTimeId == 3) {
                    rainfallValues.put(Instant.ofEpochSecond(currentTime),
                            constantRainfallData(Instant.ofEpochSecond(600L),
                                    Instant.ofEpochSecond(currentTime).minusSeconds(initialTime.getEpochSecond())) );
                }
                else {
                    throw new NullPointerException("numberOfCurves must be between 1 and 3");
                }
            }

            //TODO nullo
            rainfallData.put( rainfallTimeId, rainfallValues );

        }
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
    public void setDataStructure(SWMMobject data) {
        this.dataStructure = data;
    }
}