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
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.RunoffSolver;

import org.altervista.growworkinghard.jswmm.inpparser.DataFromFile;
import org.altervista.growworkinghard.jswmm.inpparser.INPConfiguration;
import org.altervista.growworkinghard.jswmm.inpparser.INPparser;
import org.altervista.growworkinghard.jswmm.inpparser.objects.GeneralINP;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Build the data structure starting from the inp file
 * TODO create the INPparser and import it
 */
public class SWMMobject extends INPparser {

    DataFromFile interfaceINP;

    private Unitable projectUnits;
    private Datetimeable projectDateTime;

    //TODO  report stuff
    private Datetimeable reportDateTime;

    private Datetimeable linksDateTime;
    private Map<String, Conduit> conduits = new ConcurrentHashMap<>();
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
        return conduits.get(conduitName);
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
        Long reportStep = 30L;
        reportDateTime = new PeriodStep(reportStartDate, reportEndDate, reportStep);

        //Setup routing solver
        Long routingStep = 30L;
        double routingTol = 0.0015;
        linksDateTime = new RoutingDateTime(startDate, endDate, routingStep, routingTol);
        routingSolver = new SteadyOptions();

        //Setup runoff solver
        Long runoffStep = 30L;
        double minStepSize = 1.0e-8;
        double maxStepSize = 1.0e+3;
        double absoluteTolerance = 1.0e-5;
        double relativeTolerance = 1.0e-5;
        areasDateTime = new PeriodStep(startDate, endDate, runoffStep);
        runoffSolver = new DormandPrince54(minStepSize, maxStepSize, absoluteTolerance, relativeTolerance);

        //Setup for JUnit test//////////////////////////////////////////////////////////////////////////////////////////
        setSubcatchments();
        setNodes();
        setLinks();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Setup raingage

        //DataCollector junctionReadDataFromFile = new SWMM5RainfallFile("ciao");

        String raingageName = "RG1";
        Long rainfallStepSize = 60L;
    }

    public SWMMobject(String INPfile) throws ConfigurationException {
        this(INPfile, 1);
    }

    public SWMMobject(String INPfile, int numberOfCurves) throws ConfigurationException {
        this.numberOfCurves = numberOfCurves;

        load(INPfile);
        INPConfiguration config = getConfiguration(INPfile);
        interfaceINP = new GeneralINP(INPfile);

        //Setup units
        String units = ((GeneralINP) interfaceINP).projectUnits(INPfile);
        projectUnits = new SWMMunits(units);

        //Setup simulation dates
        String startDateStr = ((GeneralINP) interfaceINP).dateTime(INPfile, AvailableDateTypes.startDate.toString());
        Instant startDate = Instant.parse(startDateStr);
        String endDateStr = ((GeneralINP) interfaceINP).dateTime(INPfile, AvailableDateTypes.endDate.toString());
        Instant endDate = Instant.parse(endDateStr);
        projectDateTime = new Period(startDate, endDate);

        //Setup report
        String reportStartDateStr = ((GeneralINP) interfaceINP).dateTime(INPfile, "reportStart");
        Instant reportStartDate = Instant.parse(reportStartDateStr);
        Instant reportEndDate = endDate;
        Long reportStep = ((GeneralINP) interfaceINP).dateTime(INPfile, "reportStep");
        reportDateTime = new PeriodStep(reportStartDate, reportEndDate, reportStep);

        //Setup routing solver
        Long routingStep = ((GeneralINP) interfaceINP).routingSolver(INPfile, "step");
        double routingTol = 0.0015;
        linksDateTime = new RoutingDateTime(startDate, endDate, routingStep, routingTol);

        String routingMethod =  ((GeneralINP) interfaceINP).routingSolver(INPfile, "name");
        switch ( routingMethod ) {
            case "STEADY":
                routingSolver = new SteadyOptions();
                break;
            case "KINWAVE":
                throw new NullPointerException("Nothing implemented yet");
            case "DYNWAVE":
                throw new NullPointerException("Nothing implemented yet");
            default:
                throw new InvalidParameterException("Solver not valid");
        }

        //Setup raingage
        String raingageName;
        Long rainfallStepSize;
        //DataCollector junctionReadDataFromFile = new SWMM5RainfallFile("ciao");

        //for () {
        // now I take just the first one!
        raingageName = ((GeneralINP) interfaceINP).raingage(INPfile);
        rainfallStepSize = ((GeneralINP) interfaceINP).raingage(INPfile, raingageName, "step");
        //}

        //Setup runoff solver
        Long runoffStep = ((GeneralINP) interfaceINP).runoffSolver(INPfile, "step");
        double minStepSize = 1.0e-8;
        double maxStepSize = 1.0e+3;
        double absoluteTolerance = 1.0e-5;
        double relativeTolerance = 1.0e-5;
        areasDateTime = new PeriodStep(startDate, endDate, runoffStep);

        String runoffSolverName = ((GeneralINP) interfaceINP).runoffSolver(INPfile, "name");
        switch ( runoffSolverName ) {
            case "DP54":
                runoffSolver = new DormandPrince54(minStepSize, maxStepSize, absoluteTolerance, relativeTolerance);
                break;
            default:
                throw new InvalidParameterException("Solver not valid");
        }

        //Link properties
        String linkName;
        Iterator<String> conduitsList = config.getSection("CONDUITS").getKeys();

        for (Iterator<String> it = conduitsList; it.hasNext(); ) {
            linkName = it.next();

            for (int curveId=1; curveId<=numberOfCurves; curveId++) {
                conduits.put(linkName, new Conduit(linkName, curveId, linksDateTime, projectUnits, routingSolver, INPfile));
            }
        }

        //Setup Junctions
        String nodeName;
        Iterator<String> junctionsList = config.getSection("JUNCTIONS").getKeys();

        for (Iterator<String> it = junctionsList; it.hasNext(); ) {
            nodeName = it.next();
            junctions.put(nodeName, new Junction(nodeName, projectUnits, INPfile));
        }

        //Setup Outfalls as Junctions
        Iterator<String> outfallList = config.getSection("OUTFALLS").getKeys();

        for (Iterator<String> it = outfallList; it.hasNext(); ) {
            nodeName = it.next();
            junctions.put(nodeName, new Junction(nodeName, projectUnits, INPfile, true));
        }

        //Areas properties
        String areaName;
        Iterator<String> areasList = config.getSection("SUBCATCHMENTS").getKeys();

        for (Iterator<String> it = areasList; it.hasNext(); ) {
            areaName = it.next();

            for (int curveId = 1; curveId<=numberOfCurves; curveId++) {
                areas.put(areaName, new Area(areaName, curveId, projectUnits, areasDateTime, runoffSolver, INPfile));
            }
        }
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

        Instant initialTimeInstant = getProjectDateTime().getDateTime(AvailableDateTypes.startDate);
        long initialTime = initialTimeInstant.getEpochSecond();
        Instant finalTimeInstant = getProjectDateTime().getDateTime(AvailableDateTypes.endDate);
        long finalTime = finalTimeInstant.getEpochSecond();

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
        conduits.get(linkName).getUpstreamOutside().sumStreamFlowRate(flowRate);
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


    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////                         /////////////////////////////
    ///////////////////////////  TODO: OLD CODE UPDATE! /////////////////////////////
    ///////////////////////////                         /////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////



    private void setSubcatchments() {
        //for (each subcatchment)
        setAreas("1", 1.937);
        setAreas("2", 1.731);
        setAreas("3", 0.481);
        setAreas("4", 0.547);
        setAreas("5", 2.141);
        setAreas("6", 0.383);
        setAreas("7", 0.353);
        setAreas("8", 0.999);
        setAreas("9", 1.583);
        setAreas("10", 0.633);
    }

    private void setAreas(String areaName, double subcatchmentArea) {
        //ReadDataFromFile subcatchmentReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        //AcquiferSetup acquiferSetup = new Acquifer();
        //SnowPackSetup subcatchmentSnowpack = new SnowPack();
        //ProjectUnits subcatchmentUnits = new CubicMetersperSecond();
        //String subcatchmentName = "Sub1";

        Double imperviousPercentage = 0.75;
        Double imperviousWOstoragePercentage = 0.25;

        Double depressionStorageImpervious = 0.00005;
        Double depressionStoragePervious = 0.00005;

        String perviousTo = "OUTLET";
        Double percentageFromPervious = 0.0;

        String imperviousTo = "OUTLET";
        Double percentageFromImpervious = 0.0;

        Double roughnessCoefficientPervious = 0.1;      //Manning number
        Double roughnessCoefficientImpervious = 0.01;   //Manning number

        Double characteristicWidth = 100.0;             // [m]
        Double areaSlope = 0.01;
        Double curbLength = 0.0;

        String raingageName = "RG1";

        HashMap<Integer, List<Subarea>> subareas = new LinkedHashMap<>();
        for (int id = 1; id<=numberOfCurves; id++) {
            subareas.put(id, divideAreas(areaName, imperviousPercentage, subcatchmentArea,
                    imperviousWOstoragePercentage, depressionStoragePervious, depressionStorageImpervious,
                    roughnessCoefficientPervious, roughnessCoefficientImpervious,
                    perviousTo, imperviousTo, percentageFromPervious, percentageFromImpervious) );
        }
        areas.put(areaName, new Area(areaName, 1, projectUnits, areasDateTime, runoffSolver, characteristicWidth,
                areaSlope, subareas, true) );
        areas.put(areaName, new Area(areaName, 2, projectUnits, areasDateTime, runoffSolver, characteristicWidth,
                areaSlope, subareas, true) );
        areas.put(areaName, new Area(areaName, 3, projectUnits, areasDateTime, runoffSolver, characteristicWidth,
                areaSlope, subareas, true) );
    }

    private void setNodes() {
        setJunctions("J1", 0.0);
        setJunctions("J2", 0.0);
        setJunctions("J3", 0.0);
        setJunctions("J4", 0.0);
        setJunctions("J5", 0.0);
        setJunctions("J6", 0.0);
        setJunctions("J7", 0.0);
        setJunctions("J8", 0.0);
        setJunctions("J9", 0.0);
        setJunctions("J10", 0.0);
        setJunctions("J11", 0.0);
    }

    private void setJunctions(String nodeName, double nodeElevation) {
        //for (each junction)
        //ReadDataFromFile junctionReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        //WriteDataToFile writeDataToFile = new WriteSWMM5RainfallToFile();
        //ExternalInflow dryWeatherInflow = new DryWeatherInflow();
        //ExternalInflow RDII = new RainfallDependentInfiltrationInflow();
        //ProjectUnits nodeUnits = new CubicMetersperSecond();

        Double maximumDepthNode = 3.0;
        Double initialDepthNode = 2.0;
        Double maximumDepthSurcharge = 1.0;
        Double nodePondingArea = 200.0;

        junctions.put(nodeName, new Junction(nodeName, projectUnits, nodeElevation, maximumDepthNode,
                initialDepthNode, maximumDepthSurcharge, nodePondingArea));
    }

    private void setLinks() {
        //for (each link) TODO check if present
        setConduit("11", 120.0,"J1", -239.0, 197.0, 0.0,
                "J3", -119.0, 197.0, 0);
        setConduit("12", 122,  "J2",-119.0, 319.0, 0.0,
                "J3",-119.0, 197.0, 0);
        setConduit("13", 119,  "J3",-119.0, 197.0, 0.0,
                "J4",0.0, 197.0, 0);
        setConduit("14", 43,   "J5",111.0, 240.0, 0.0,
                "J7",111.0, 197.0, 0);
        setConduit("15", 92,   "J6",203.0, 197.0, 0.0,
                "J7",111.0, 197.0, 0);
        setConduit("16", 111,   "J7",111.0, 197.0, 0.0,
                "J4",0.0, 197.0, 0);
        setConduit("17", 81,   "J4",  0.0, 197.0, 0.0,
                "J8",0.0, 116.0, 0);
        setConduit("18", 150,   "J9",150.0, 116.0, 0.0,
                "J8",0.0, 116.0, 0);
        setConduit("19", 134,  "J10",-134.0, 116.0, 0.0,
                "J8",0.0, 116.0, 0);
        setConduit("20", 116,   "J8",  0.0, 116.0, 0.0,
                "J11",0.0,   0.0, 0);
    }

    private void setConduit(String linkName, double linkLength, String upName, double upX, double upY, double upZ,
                            String downName, double downX, double downY, double downZ) {

        Double linkRoughness = 120.0;
        Double upstreamOffset = 0.0;
        Double downstreamOffset = 0.0;
        Double fillCoefficient = 0.9;
        Double diameter = 1.0;

        CrossSectionType crossSectionType = new Circular(diameter);

        OutsideSetup upstreamOutside = new OutsideSetup(upName, upstreamOffset,
                fillCoefficient, upX, upY, upZ);
        OutsideSetup downstreamOutside = new OutsideSetup(downName, downstreamOffset,
                fillCoefficient, downX, downY, downZ);

        conduits.put(linkName, new Conduit(linkName, 1, projectUnits, linksDateTime, routingSolver, crossSectionType,
                upstreamOutside, downstreamOutside, linkLength, linkRoughness, true) );
        conduits.put(linkName, new Conduit(linkName, 2, projectUnits, linksDateTime, routingSolver, crossSectionType,
                upstreamOutside, downstreamOutside, linkLength, linkRoughness, true) );
        conduits.put(linkName, new Conduit(linkName, 3, projectUnits, linksDateTime, routingSolver, crossSectionType,
                upstreamOutside, downstreamOutside, linkLength, linkRoughness, true) );
    }

    private List<Subarea> divideAreas(String name, Double imperviousPercentage, Double subcatchmentArea,
                                      Double imperviousWOstoragePercentage, Double depressionStoragePervious, Double depressionStorageImpervious,
                                      Double roughnessCoefficientPervious, Double roughnessCoefficientImpervious,
                                      String perviousTo, String imperviousTo, Double percentageFromPervious, Double percentageFromImpervious) {

        Double imperviousWOStorageArea = subcatchmentArea * imperviousPercentage * imperviousWOstoragePercentage;
        Double imperviousWStorageArea = subcatchmentArea * imperviousPercentage  - imperviousWOStorageArea;
        Double perviousArea = subcatchmentArea * (1-imperviousPercentage);

        List<Subarea> tmpSubareas = new LinkedList<>();
        if(imperviousPercentage == 0.0) {
            tmpSubareas.add(new Pervious(name, projectUnits, areasDateTime, perviousArea, depressionStoragePervious,
                    roughnessCoefficientPervious, null, null, 0.0) );
        }
        else if(imperviousPercentage == 1.0) {
            if (imperviousWOstoragePercentage != 0.0) {
                tmpSubareas.add(new ImperviousWithoutStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, roughnessCoefficientImpervious, null, null));
            }
            if (imperviousWOstoragePercentage != 1.0) {
                tmpSubareas.add(new ImperviousWithStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, depressionStorageImpervious,
                        roughnessCoefficientImpervious, null, null));
            }

        }
        else {
            if (perviousTo.equals("IMPERVIOUS")) {
                tmpSubareas.add(new ImperviousWithoutStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, roughnessCoefficientImpervious, null, null));

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new Pervious(name, projectUnits, areasDateTime, perviousArea, depressionStoragePervious,
                        roughnessCoefficientPervious, null, null, 0.0));

                tmpSubareas.add(new ImperviousWithStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, depressionStorageImpervious, roughnessCoefficientImpervious,
                        percentageFromPervious, tmpConnections));
            }
            else if(perviousTo.equals("OUTLET")) {
                tmpSubareas.add(new Pervious(name, projectUnits, areasDateTime, perviousArea,
                        depressionStoragePervious, roughnessCoefficientPervious, null, null, 0.0));
            }

            if (imperviousTo.equals("PERVIOUS")) {

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new ImperviousWithoutStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, roughnessCoefficientImpervious, null, null));
                tmpConnections.add(new ImperviousWithStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, depressionStorageImpervious, roughnessCoefficientImpervious,
                        percentageFromPervious, tmpConnections));

                tmpSubareas.add(new Pervious(name, projectUnits, areasDateTime, perviousArea, depressionStoragePervious,
                        roughnessCoefficientPervious, percentageFromImpervious, tmpConnections, 0.0));
            }
            else if (imperviousTo.equals("OUTLET")) {
                tmpSubareas.add(new ImperviousWithStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, depressionStorageImpervious,
                        roughnessCoefficientImpervious, null, null));
                tmpSubareas.add(new ImperviousWithoutStorage(name, projectUnits, areasDateTime, imperviousWStorageArea,
                        imperviousWOStorageArea, roughnessCoefficientImpervious, null, null));
            }
        }
        return tmpSubareas;
    }
}