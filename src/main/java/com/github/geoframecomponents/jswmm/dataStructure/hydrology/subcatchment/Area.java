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

package com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment;

import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.ReceiverRunoff.ReceiverRunoff;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffSolver;
import org.altervista.growworkinghard.jswmm.inpparser.objects.AreaINP;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Area extends AbstractSubcatchment {

    List<ReceiverRunoff> receivers;

    RunoffSolver runoffSolver;

    Double characteristicWidth;
    Double areaSlope;
    //Double curbLength;

    HashMap<Integer, List<Subarea>> subareas;
    HashMap<Integer, LinkedHashMap<Instant, Double>> totalAreaFlowRate;

    public Area(String areaName, Integer curveId, Unitable units, Datetimeable dateTime, RunoffSolver runoffSolver,
                Double characteristicWidth, Double areaSlope, HashMap<Integer, List<Subarea>> subareas, boolean report) {

        super(areaName);
        this.setSubcatchmentUnits(units);
        this.setSubcatchmentTime(dateTime);

        this.runoffSolver = runoffSolver;

        this.characteristicWidth = characteristicWidth;
        this.areaSlope = areaSlope;
        this.subareas = new HashMap<>(subareas);
        this.totalAreaFlowRate = new LinkedHashMap<>();

        Instant startSimDate = this.getSubcatchmentTime().getDateTime(AvailableDateTypes.startDate);
        for (Subarea subarea : this.subareas.get(curveId)) {
            subarea.setAreaFlowRate(curveId, startSimDate, 0.0);
            subarea.setRunoffDepth(curveId, startSimDate, 0.0);
            subarea.setTotalDepth(curveId, startSimDate, 0.0);
        }
    }

    public Area(String areaName, Integer numberOfCurves, Unitable units, Datetimeable dateTime,
                RunoffSolver runoffSolver, String INPfile) throws ConfigurationException {

        super(areaName);

        interfaceINP = new AreaINP(INPfile);

        this.setSubcatchmentUnits(units);
        this.setSubcatchmentTime(dateTime);

        this.runoffSolver = runoffSolver;

        this.characteristicWidth = Double.parseDouble( ((AreaINP) interfaceINP).width(INPfile, name) );
        this.areaSlope = Double.parseDouble( ((AreaINP) interfaceINP).slope(INPfile, name) );

        double subcatchmentArea = Double.parseDouble( ((AreaINP) interfaceINP).subcatchArea(INPfile, name) );
        double imperviousPercentage = Double.parseDouble( ((AreaINP) interfaceINP).impPerc(INPfile, name) );
        double imperviousWOstoragePercentage = Double.parseDouble( ((AreaINP) interfaceINP).impWOstoPerc(INPfile, name) );
        double depressionStoragePervious = Double.parseDouble( ((AreaINP) interfaceINP).dsPerv(INPfile, name) );
        double depressionStorageImpervious = Double.parseDouble( ((AreaINP) interfaceINP).dsImperv(INPfile, name) );
        double roughnessCoefficientPervious = Double.parseDouble( ((AreaINP) interfaceINP).roughPerv(INPfile, name) );
        double roughnessCoefficientImpervious = Double.parseDouble( ((AreaINP) interfaceINP).roughImperv(INPfile, name) );

        String routeTo = ((AreaINP) interfaceINP).routeTo(INPfile, name);
        String perviousTo;
        String imperviousTo;
        double percentageFromPervious;
        double percentageFromImpervious;

        switch (routeTo) {
            case "IMPERVIOUS":
                perviousTo = "IMPERVIOUS";
                imperviousTo = "OUTLET";
                percentageFromPervious = Double.parseDouble( ((AreaINP) interfaceINP).routeToPerc(name, INPfile) );
                percentageFromImpervious = 1.0;
                break;
            case "PERVIOUS":
                perviousTo = "OUTLET";
                imperviousTo = "PERVIOUS";
                percentageFromImpervious = Double.parseDouble( ((AreaINP) interfaceINP).routeToPerc(name, INPfile) );
                percentageFromPervious = 1.0;
                break;
            default:
                perviousTo = "OUTLET";
                imperviousTo = "OUTLET";
                percentageFromPervious = 1.0;
                percentageFromImpervious = 1.0;
        }

        this.subareas = new HashMap<>();
        for (int curveId = 1; curveId<=numberOfCurves; curveId++) {
            this.subareas.put(curveId, divideAreas(imperviousPercentage, subcatchmentArea,
                    imperviousWOstoragePercentage, depressionStoragePervious, depressionStorageImpervious,
                    roughnessCoefficientPervious, roughnessCoefficientImpervious,
                    perviousTo, imperviousTo, percentageFromPervious, percentageFromImpervious));

            Instant startSimDate = this.getSubcatchmentTime().getDateTime(AvailableDateTypes.startDate);
            for (Subarea subarea : this.subareas.get(curveId)) {
                subarea.setAreaFlowRate(curveId, startSimDate, 0.0);
                subarea.setRunoffDepth(curveId, startSimDate, 0.0);
                subarea.setTotalDepth(curveId, startSimDate, 0.0);
            }
        }
        this.totalAreaFlowRate = new LinkedHashMap<>();

        //TODO report!!!
    }

    public LinkedHashMap<Instant, Double> evaluateTotalFlowRate(Integer id) {
        //check if totalArea contain the rainfallTimeId
        if (!totalAreaFlowRate.containsKey(id)) {
            totalAreaFlowRate.put(id, new LinkedHashMap<>());
        }
        //sum the volume of each subarea as product of the flowRate and the subarea's area
        for(Subarea subarea : subareas.get(id)) {

            LinkedHashMap<Instant, Double> subareaFlowRate = subarea.getFlowRate().get(id);
            for (Instant time : subareaFlowRate.keySet()) {
                Double oldFLowRate = totalAreaFlowRate.get(id).get(time);
                double value;
                if (oldFLowRate == null) {
                    value = subareaFlowRate.get(time) * subarea.subareaArea * 10.0;// [m^3/s]
                } else {
                    value = oldFLowRate + subareaFlowRate.get(time) * subarea.subareaArea * 10.0;// [m^3/s]
                }
                LinkedHashMap<Instant, Double> upgradedLHM = totalAreaFlowRate.get(id);
                upgradedLHM.put(time, value);
                totalAreaFlowRate.put(id, upgradedLHM);
            }
        }
        return totalAreaFlowRate.get(id);
    }

    public HashMap<Integer, List<Subarea>> getSubareas() {
        return subareas;
    }

    public void evaluateRunoffFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData) {

        Instant startTime = getSubcatchmentTime().getDateTime(AvailableDateTypes.startDate);
        Instant totalTime = getSubcatchmentTime().getDateTime(AvailableDateTypes.endDate);
        long runoffStep = getSubcatchmentTime().getDateTime(AvailableDateTypes.stepSize);

        for ( Instant currentTime = startTime; currentTime.isBefore(totalTime); currentTime = currentTime.plusSeconds(runoffStep)) {

            for (Integer identifier : adaptedRainfallData.keySet()) {

                double rainfall = adaptedRainfallData.get(identifier).get(currentTime);
                for (Subarea subarea : subareas.get(identifier)) {
                    subarea.setDepthFactor(areaSlope, characteristicWidth);
                    subarea.evaluateFlowRate(identifier, rainfall, 0.0,
                            currentTime, runoffSolver, areaSlope, characteristicWidth); //TODO evaporation!!
                }
            }
        }
    }

    private List<Subarea> divideAreas(Double imperviousPercentage, Double subcatchmentArea,
                                      Double imperviousWOstoragePercentage, Double depressionStoragePervious, Double depressionStorageImpervious,
                                      Double roughnessCoefficientPervious, Double roughnessCoefficientImpervious,
                                      String perviousTo, String imperviousTo, Double percentageFromPervious, Double percentageFromImpervious) {

        Double imperviousWOStorageArea = subcatchmentArea * imperviousPercentage * imperviousWOstoragePercentage;
        Double imperviousWStorageArea = subcatchmentArea * imperviousPercentage  - imperviousWOStorageArea;
        double perviousArea = subcatchmentArea * (1-imperviousPercentage);

        List<Subarea> tmpSubareas = new LinkedList<>();
        if(imperviousPercentage == 0.0) {
            tmpSubareas.add(new Pervious(name, subcatchmentUnits, subcatchmentTime, perviousArea, depressionStoragePervious,
                    roughnessCoefficientImpervious, null, null, null));
        }
        else if(imperviousPercentage == 1.0) {
            if (imperviousWOstoragePercentage != 0.0) {
                tmpSubareas.add(new ImperviousWithoutStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));
            }
            if (imperviousWOstoragePercentage != 1.0) {
                tmpSubareas.add(new ImperviousWithStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, null, null));
            }

        }
        else {
            if (perviousTo.equals("IMPERVIOUS")) {
                tmpSubareas.add(new ImperviousWithoutStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new Pervious(name, subcatchmentUnits, subcatchmentTime, perviousArea, depressionStoragePervious,
                        roughnessCoefficientPervious, null, null, null));

                tmpSubareas.add(new ImperviousWithStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, percentageFromPervious,
                        tmpConnections));
            }
            else if(perviousTo.equals("OUTLET")) {
                tmpSubareas.add(new Pervious(name, subcatchmentUnits, subcatchmentTime, perviousArea, depressionStoragePervious,
                        roughnessCoefficientPervious, null, null, null));
            }

            if (imperviousTo.equals("PERVIOUS")) {

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new ImperviousWithoutStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));
                tmpConnections.add(new ImperviousWithStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, percentageFromPervious,
                        tmpConnections));

                tmpSubareas.add(new Pervious(name, subcatchmentUnits, subcatchmentTime, perviousArea, depressionStoragePervious, roughnessCoefficientPervious,
                        percentageFromImpervious, tmpConnections, null));
            }
            else if (imperviousTo.equals("OUTLET")) {
                tmpSubareas.add(new ImperviousWithStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, null, null));
                tmpSubareas.add(new ImperviousWithoutStorage(name, subcatchmentUnits, subcatchmentTime, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));
            }
        }
        return tmpSubareas;
    }
}