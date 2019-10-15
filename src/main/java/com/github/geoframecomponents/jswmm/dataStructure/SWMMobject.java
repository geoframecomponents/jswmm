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

package com.github.geoframecomponents.jswmm.dataStructure;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.Conduit;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.Circular;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.nodeObject.Junction;
import com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment.*;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Period;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.PeriodStep;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.SWMMunits;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutingDateTime;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutingSolver;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.SteadyOptions;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.DormandPrince54;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffDateTime;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffSolver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Build the data structure starting from the inp file
 * TODO create the INPparser and import it
 */
public class SWMMobject{

    private Unitable projectUnits;
    private Datetimeable projectDateTime;

    private Datetimeable reportDateTime;

    private Datetimeable linksDateTime;
    private Map<String, Conduit> conduit = new ConcurrentHashMap<>();
    private RoutingSolver routingSolver;

    private Datetimeable areasDateTime;
    private HashMap<String, Area> areas = new HashMap<>();
    private RunoffSolver runoffSolver;

    private Map<String, Junction> junctions = new ConcurrentHashMap<>();

    private int numberOfCurves;

    public Datetimeable getProjectDateTime() {
        return projectDateTime;
    }

    public Datetimeable getLinksDateTime() {
        return linksDateTime;
    }

    public Datetimeable getAreasDateTime() {
        return areasDateTime;
    }

    public Conduit getConduit(String conduitName) {
        return conduit.get(conduitName);
    }

    public Area getAreas(String areaName) {
        return areas.get(areaName);
    }

    public SWMMobject() {

        //Setup units
        String units = "CMS";
        projectUnits = new SWMMunits(units);

        //Setup simulation dates
        Instant startDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2018-01-01T01:00:00Z");
        projectDateTime = new Period(startDate, endDate);

        //Setup simulation options
        numberOfCurves = 3;

        //Setup report
        Instant reportStartDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant reportEndDate = Instant.parse("2018-01-01T01:00:00Z");
        double reportStep = 30L;
        reportDateTime = new PeriodStep(reportStartDate, reportEndDate, reportStep);

        //Setup junctions


        //Setup conduits
        double routingStep = 30L;
        double routingTol = 0.0015;
        linksDateTime = new RoutingDateTime(startDate, endDate, routingStep, routingTol);
        routingSolver = new SteadyOptions();

        double linkRoughness = 120.0;
        double upstreamOffset = 0.0;
        double downstreamOffset = 0.0;
        double fillCoefficient = 0.9;
        double diameter = 1.0;

        String linkName = "11";
        double linkLength = 100.0;
        String upName = "J1";
        String downName = "J1";
        double upX = -239.0;
        double upY = 197.0;
        double upZ = 0.0;
        double downX = -119.0;
        double downY = 197.0;
        double downZ = 0.0;

        CrossSectionType crossSectionType = new Circular(diameter);

        OutsideSetup upstreamOutside = new OutsideSetup(upName, upstreamOffset,
                fillCoefficient, upX, upY, upZ);
        OutsideSetup downstreamOutside = new OutsideSetup(downName, downstreamOffset,
                fillCoefficient, downX, downY, downZ);

        for (int curveId=1; curveId<=numberOfCurves; curveId++) {
            conduit.put(linkName, new Conduit(curveId, projectUnits, linksDateTime, routingSolver, crossSectionType, upstreamOutside,
                    downstreamOutside, linkLength, linkRoughness, true));
        }

        //Setup raingage

        //DataCollector junctionReadDataFromFile = new SWMM5RainfallFile("ciao");

        String raingageName = "RG1";
        Long rainfallStepSize = 60L;

        //Setup areas
        double runoffStep = 30L;
        double minStepSize = 1.0e-8;
        double maxStepSize = 1.0e+3;
        double absoluteTolerance = 1.0e-5;
        double relativeTolerance = 1.0e-5;
        areasDateTime = new RunoffDateTime(startDate, endDate, runoffStep,
                minStepSize, maxStepSize, absoluteTolerance, relativeTolerance);
        runoffSolver = new DormandPrince54();

        String areaName = "1";
        double subcatchmentArea = 1.0;

        double imperviousPercentage = 0.75;
        double imperviousWOstoragePercentage = 0.25;

        double depressionStorageImpervious = 0.00005;
        double depressionStoragePervious = 0.00005;

        String perviousTo = "OUTLET";
        double percentageFromPervious = 0.0;

        String imperviousTo = "OUTLET";
        double percentageFromImpervious = 0.0;

        double roughnessCoefficientPervious = 0.1;      //Manning number
        double roughnessCoefficientImpervious = 0.01;   //Manning number

        double characteristicWidth = 100.0;             // [m]
        double areaSlope = 0.01;

        HashMap<Integer, List<Subarea>> subareas = new LinkedHashMap<>();
        for (int curveId = 1; curveId<=numberOfCurves; curveId++) {
            subareas.put(curveId, divideAreas(projectUnits, areasDateTime, imperviousPercentage, subcatchmentArea,
                    imperviousWOstoragePercentage, depressionStoragePervious, depressionStorageImpervious,
                    roughnessCoefficientPervious, roughnessCoefficientImpervious,
                    perviousTo, imperviousTo, percentageFromPervious, percentageFromImpervious));

            areas.put(areaName, new Area(curveId, projectUnits, areasDateTime, runoffSolver,
                    characteristicWidth, areaSlope, subareas, true));
        }

        //Setup Junctions
        String nodeName = "J1";
        double nodeElevation = 0.0;

        Double maximumDepthNode = 3.0;
        Double initialDepthNode = 0.0;
        Double maximumDepthSurcharge = 1.0;
        Double nodePondingArea = 200.0;

        junctions.put(nodeName, new Junction(projectUnits, nodeElevation, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
    }

    private List<Subarea> divideAreas(Unitable units, Datetimeable time, Double imperviousPercentage, Double subcatchmentArea,
                                      Double imperviousWOstoragePercentage, Double depressionStoragePervious, Double depressionStorageImpervious,
                                      Double roughnessCoefficientPervious, Double roughnessCoefficientImpervious,
                                      String perviousTo, String imperviousTo, Double percentageFromPervious, Double percentageFromImpervious) {

        Double imperviousWOStorageArea = subcatchmentArea * imperviousPercentage * imperviousWOstoragePercentage;
        Double imperviousWStorageArea = subcatchmentArea * imperviousPercentage  - imperviousWOStorageArea;
        double perviousArea = subcatchmentArea * (1-imperviousPercentage);

        List<Subarea> tmpSubareas = new LinkedList<>();
        if(imperviousPercentage == 0.0) {
            tmpSubareas.add(new Pervious(units, time, perviousArea, depressionStoragePervious,
                    roughnessCoefficientImpervious, null, null, null));
        }
        else if(imperviousPercentage == 1.0) {
            if (imperviousWOstoragePercentage != 0.0) {
                tmpSubareas.add(new ImperviousWithoutStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));
            }
            if (imperviousWOstoragePercentage != 1.0) {
                tmpSubareas.add(new ImperviousWithStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, null, null));
            }

        }
        else {
            if (perviousTo.equals("IMPERVIOUS")) {
                tmpSubareas.add(new ImperviousWithoutStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new Pervious(units, time, perviousArea, depressionStoragePervious,
                        roughnessCoefficientPervious, null, null, null));

                tmpSubareas.add(new ImperviousWithStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, percentageFromPervious,
                        tmpConnections));
            }
            else if(perviousTo.equals("OUTLET")) {
                tmpSubareas.add(new Pervious(units, time, perviousArea, depressionStoragePervious,
                        roughnessCoefficientPervious, null, null, null));
            }

            if (imperviousTo.equals("PERVIOUS")) {

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new ImperviousWithoutStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));
                tmpConnections.add(new ImperviousWithStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, percentageFromPervious,
                        tmpConnections));

                tmpSubareas.add(new Pervious(units, time, perviousArea, depressionStoragePervious, roughnessCoefficientPervious,
                        percentageFromImpervious, tmpConnections, null));
            }
            else if (imperviousTo.equals("OUTLET")) {
                tmpSubareas.add(new ImperviousWithStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, null, null));
                tmpSubareas.add(new ImperviousWithoutStorage(units, time, imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, null, null));
            }
        }
        return tmpSubareas;
    }

    public List<Double> readFileList(String fileName) {
        String line;

        List<Double> testingValues = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                testingValues.add(Double.parseDouble(line));
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }

        return testingValues;
    }

   public LinkedHashMap<Instant, Double> adaptDataSeries(Long toStepSize, Long fromStepSize, LinkedHashMap<Instant, Double> HMData) {

        Long initialTime = getProjectDateTime().getDateTime(AvailableDateTypes.startDate);
        Long finalTime= getProjectDateTime().getDateTime(AvailableDateTypes.endDate);

        return  adaptDataSeries(toStepSize, fromStepSize, finalTime, initialTime,  HMData);
    }

    /**
     * Method that adapts data to a defined step size and over the period between initialTime and finalTime
     * @param toStepSize
     * @param fromStepSize
     * @param finalTime
     * @param initialTime
     * @param HMData
     * @return the HM of data over data/time
     */
    public LinkedHashMap<Instant, Double> adaptDataSeries(Long toStepSize, Long fromStepSize, Long finalTime,
                                                          Long initialTime, LinkedHashMap<Instant, Double> HMData) {

        LinkedHashMap<Instant, Double> adaptedData = new LinkedHashMap<>();
        Long currentDataTime = initialTime;

        Long currentTime = initialTime;
        while (currentTime<finalTime) {

            double currentData;
            if (currentDataTime.equals(currentTime)) {
                currentData = HMData.get(Instant.ofEpochSecond(currentTime));
            }
            else {
                while(currentDataTime <= currentTime) {
                    currentDataTime += fromStepSize;
                }

                Long upperTime = currentDataTime;

                Double upperData = HMData.get(Instant.ofEpochSecond(upperTime));
                if (upperData == null) {
                    upperData = HMData.get(Instant.ofEpochSecond(finalTime));
                }

                Long lowerTime = upperTime - fromStepSize;
                double lowerData = HMData.get(Instant.ofEpochSecond(lowerTime));

                currentData = interpolateData(currentTime, lowerTime, lowerData,
                        upperTime, upperData);
            }
            adaptedData.put(Instant.ofEpochSecond(currentTime), currentData);
            currentTime+=toStepSize;
        }
        adaptedData.put(Instant.ofEpochSecond(currentTime), HMData.get(Instant.ofEpochSecond(finalTime)));

//        for (Instant time : adaptedData.keySet()) {
//            System.out.println("Time " + time + " value " + adaptedData.get(time));
//        }

        return adaptedData;
    }

    private Double interpolateData(Long currentRunoffTime, Long lowerTime,
                                   Double lowerTimeData, Long upperTime, Double upperTimeData) {
        Long rangeTime = upperTime - lowerTime;

        if( rangeTime == 0 ) { return lowerTimeData; }
        else {
            Double numerator = upperTimeData - lowerTimeData;

            return lowerTimeData + numerator / rangeTime * (currentRunoffTime - lowerTime);
        }
    }

    public void setNodeFlowRate(String nodeName, HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate) {
        junctions.get(nodeName).sumFlowRate(flowRate);
    }

    public void setLinkFlowRate(String linkName, HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate) {
        conduit.get(linkName).getUpstreamOutside().sumStreamFlowRate(flowRate);
    }

    public void upgradeSubtrees(String outLink, HashMap<Integer, List<Integer>> subtrees) {

        double downstreamDepthOut = getConduit(outLink).getUpstreamOutside().getWaterDepth();
        double maxDepth = downstreamDepthOut;
        Integer maxId = Integer.parseInt(outLink);

        for (Integer subtreeId : subtrees.keySet()) {
            if (getConduit(String.valueOf(subtreeId)) != null) {
                double downstreamDepth = getConduit(String.valueOf(subtreeId)).getDownstreamOutside().getWaterDepth();
                if (downstreamDepth > maxDepth) {
                    maxDepth = downstreamDepth;
                    maxId = subtreeId;
                }

            }
        }

        if (maxId != Integer.parseInt(outLink)) {
            upgradeStream(outLink, downstreamDepthOut - maxDepth);
        }

        //System.out.println("Part 3");

        for (List<Integer> subtreeList : subtrees.values()) {
            String firstSon = String.valueOf(subtreeList.get(subtreeList.size() - 1));

            //System.out.println("firstSon " + firstSon);

            if (getConduit(firstSon) != null) {
                double downstreamDepth = getConduit(firstSon).getDownstreamOutside().getWaterDepth();
                if (downstreamDepth - maxDepth != 0.0) {

                    //System.out.println("subtreeList " + subtreeList );
                    //System.out.println("downstreamDepth - maxDepth " + (downstreamDepth - maxDepth) );

                    upgradeStream(subtreeList, maxDepth - downstreamDepth);
                }

            }

            //System.out.println("END part 3");
        }
    }

    private void upgradeStream(List<Integer> subtreeList, double delta) {
        for (Integer subtreeLink : subtreeList) {
            String currentLink = String.valueOf(subtreeLink);
            upgradeStream(currentLink, delta);
        }

        //System.out.println("END For loop upgradeStream");

    }

    private void upgradeStream(String currentLink, double delta) {

            if (getConduit(currentLink) != null) {
                OutsideSetup upstream = getConduit(currentLink).getUpstreamOutside();
                OutsideSetup downstream = getConduit(currentLink).getDownstreamOutside();

                //System.out.println("upstream " + upstream );
                upstream.upgradeOffset(delta);

                //System.out.println("downstream " + downstream );
                //System.out.println("delta " + delta );
                downstream.upgradeOffset(delta);

                //System.out.println("END UPSTREAM upgrade!");
            }
    }
}