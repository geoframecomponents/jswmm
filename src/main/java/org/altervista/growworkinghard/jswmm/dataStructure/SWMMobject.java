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

package org.altervista.growworkinghard.jswmm.dataStructure;

import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadSWMM5RainfallFile;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.Conduit;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.Circular;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject.Junction;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject.Outfall;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.GlobalRaingage;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.RaingageSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.*;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.ReceiverRunoff;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.CubicMetersperSecond;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.GlobalTimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.routingDS.RoutingKinematicWaveSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.routingDS.RoutingSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.RunoffSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.SWMM5RunoffSetup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SWMMobject {

    private TimeSetup timeSetup;
    private RunoffSetup runoffSetup;
    private RoutingSetup routingSetup;
    private HashMap<String, RaingageSetup> raingageSetup = new HashMap<>();
    private HashMap<String, Area> areas = new HashMap<>();
    private Map<String, Junction> junctions = new ConcurrentHashMap<>();
    private HashMap<String, Outfall> outfalls = new HashMap<>();
    private Map<String, Conduit> conduit = new ConcurrentHashMap<>();
    private LinkedHashMap<Instant, Double> downstreamFlowRate;

    public SWMMobject(String inpFileName) {
        setTime();
        setRunoff();
        setRouting();
        setRaingages();
        setSubcatchments();
        setNodes();
        setLinks();
    }

    public SWMMobject() {
        setTime();
        setRunoff();
        setRouting();
        setRaingages();
        setSubcatchments();
        setNodes();
        setLinks();
        setInitialValues(1);
    }

    public TimeSetup getTimeSetup() {
        return timeSetup;
    }

    public RunoffSetup getRunoffSetup() {
        return new SWMM5RunoffSetup(runoffSetup);
    }

    public RoutingSetup getRoutingSetup() { return routingSetup; }

    public RaingageSetup getRaingage(String areaName) {
        return raingageSetup.get(areaName);
    }

    public Area getAreas(String areaName) {
        return areas.get(areaName);
    }

    public Conduit getConduit(String conduitName) {
        return conduit.get(conduitName);
    }

    private void setTime() {
        Instant startDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2018-01-01T00:14:30Z");
        Instant reportStartDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant reportEndDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant sweepStart = Instant.parse("2018-01-01T00:14:30Z");
        Instant sweepEnd = Instant.parse("2018-01-01T00:00:00Z");
        Integer dryDays = 0;

        this.timeSetup = new GlobalTimeSetup(startDate, endDate, reportStartDate, reportEndDate,
                sweepStart, sweepEnd, dryDays);
    }

    private void setUnits() {
        String units = "CMS";

        if (units == "CMS") {
            ProjectUnits projectUnits = new CubicMetersperSecond();
        }
    }

    private void setRunoff() {
        Long runoffStepSize = 30L; //must be in seconds!!

        Double minimumStepSize = 1.0e-8;
        Double maximumStepSize = 1.0e+3;
        Double absoluteRunoffTolerance = 1.0e-5;
        Double relativeRunoffTolerance = 1.0e-5;

        Instant initialTime = timeSetup.getStartDate();
        Instant totalTime = timeSetup.getEndDate();

        this.runoffSetup = new SWMM5RunoffSetup(initialTime, totalTime, runoffStepSize, minimumStepSize, maximumStepSize, absoluteRunoffTolerance, relativeRunoffTolerance);
    }

    private void setRouting() {

        Instant initialTime = timeSetup.getStartDate();
        Instant totalTime = timeSetup.getEndDate();

        Long routingStepSize = 30L;
        Double toleranceMethod = 0.0015;

        //TODO need change to parallelize
        routingSetup = new RoutingKinematicWaveSetup(routingStepSize, toleranceMethod);
    }

    private void setRaingages() {

        //for (each raingage)
        ReadDataFromFile readDataFromFile = null;
        try {
            readDataFromFile = new ReadSWMM5RainfallFile("./data/rainfall.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ProjectUnits raingageUnits = new CubicMetersperSecond();
        String raingageName = "RG1";
        String dataSourceName = "rainfall.txt";
        String stationName = "RG1";
        Long rainfallStepSize = 60L;
        //TODO FORMATDATA
        //Instant rainfallStartDate = Instant.parse("2000-04-04T00:00Z");
        //Instant rainfallEndDate = Instant.parse("2000-04-04T00:00Z");
        //Double snowpack = 0.0;

        raingageSetup.put(raingageName, new GlobalRaingage(readDataFromFile, dataSourceName, stationName, rainfallStepSize));

        ReadDataFromFile readDataFromFile2 = null;
        try {
            readDataFromFile2 = new ReadSWMM5RainfallFile("./data/rainfall2.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ProjectUnits raingageUnits = new CubicMetersperSecond();
        String raingageName2 = "RG2";
        String dataSourceName2 = "rainfall2.txt";
        String stationName2 = "RG2";
        Long rainfallStepSize2 = 60L;

        raingageSetup.put(raingageName2, new GlobalRaingage(readDataFromFile2, dataSourceName2, stationName2, rainfallStepSize2));
    }

    private void setSubcatchments() {
        //for (each subcatchment)
        setAreas();
    }

    private void setAreas() {
        //ReadDataFromFile subcatchmentReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        //AcquiferSetup acquiferSetup = new Acquifer();
        //SnowPackSetup subcatchmentSnowpack = new SnowPack();
        //ProjectUnits subcatchmentUnits = new CubicMetersperSecond();
        //String subcatchmentName = "Sub1";
        String areaName = "Sub1";
        Double subcatchmentArea = 10000.0; //meters*meters

        Double imperviousPercentage = 1.0;
        Double imperviousWOstoragePercentage = 1.0;

        Double depressionStorageImpervious = 0.0;
        Double depressionStoragePervious = 0.0;

        String perviousTo = "OUTLET";
        Double percentageFromPervious = 0.0;

        String imperviousTo = "OUTLET";
        Double percentageFromImpervious = 0.0;

        Double roughnessCoefficientPervious = 0.1;
        Double roughnessCoefficientImpervious = 0.01;

        Double characteristicWidth = 500.0;
        Double areaSlope = 0.05;
        Double curbLength = 0.0;

        String raingageName = "STA01";
        ReceiverRunoff receiverSubcatchment = null;

        List<Subarea> subareas = divideAreas(imperviousPercentage, subcatchmentArea,
                imperviousWOstoragePercentage, depressionStoragePervious, depressionStorageImpervious,
                roughnessCoefficientPervious, roughnessCoefficientImpervious,
                perviousTo, imperviousTo, percentageFromPervious, percentageFromImpervious);

        areas.put(areaName, new Area(subcatchmentArea, raingageSetup.get(areaName),
                characteristicWidth, areaSlope, subareas));

        //

        String areaName2 = "Sub2";
        Double subcatchmentArea2 = 10000.0;

        Double imperviousPercentage2 = 1.0;
        Double imperviousWOstoragePercentage2 = 0.0;

        Double depressionStorageImpervious2 = 0.0;
        Double depressionStoragePervious2 = 0.0;

        String perviousTo2 = "OUTLET";
        Double percentageFromPervious2 = 0.0;

        String imperviousTo2 = "OUTLET";
        Double percentageFromImpervious2 = 0.0;

        Double roughnessCoefficientPervious2 = 0.1;
        Double roughnessCoefficientImpervious2 = 0.01;

        Double characteristicWidth2 = 500.0;
        Double areaSlope2 = 0.05;
        Double curbLength2 = 0.0;

        String raingageName2 = "STA01";
        ReceiverRunoff receiverSubcatchment2 = null;

        List<Subarea> subareas2 = divideAreas(imperviousPercentage2, subcatchmentArea2,
                imperviousWOstoragePercentage2, depressionStoragePervious2, depressionStorageImpervious2,
                roughnessCoefficientPervious2, roughnessCoefficientImpervious2,
                perviousTo2, imperviousTo2, percentageFromPervious2, percentageFromImpervious2);

        areas.put(areaName2, new Area(subcatchmentArea2, raingageSetup.get(areaName2),
                characteristicWidth2, areaSlope2, subareas2));
    }

    private void setNodes() {
        setJunctions();
        setOutfalls();
    }

    private void setJunctions() {
        //for (each junction)
        //ReadDataFromFile junctionReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        //WriteDataToFile writeDataToFile = new WriteSWMM5RainfallToFile();
        //ExternalInflow dryWeatherInflow = new DryWeatherInflow();
        //ExternalInflow RDII = new RainfallDependentInfiltrationInflow();
        //ProjectUnits nodeUnits = new CubicMetersperSecond();

        String nodeName = "N1";
        Double nodeElevation = 2.0;
        Double maximumDepthNode = 3.0;
        Double initialDepthNode = 0.0;
        Double maximumDepthSurcharge = 1.0;
        Double nodePondingArea = 200.0;

        junctions.put(nodeName, new Junction(nodeElevation, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));

        String nodeName2 = "N2";
        Double nodeElevation2 = 2.0;
        Double maximumDepthNode2 = 3.0;
        Double initialDepthNode2 = 0.0;
        Double maximumDepthSurcharge2 = 1.0;
        Double nodePondingArea2 = 0.0;

        junctions.put(nodeName2, new Junction(nodeElevation2, maximumDepthNode2, initialDepthNode2,
                maximumDepthSurcharge2, nodePondingArea2));
    }

    private void setOutfalls() {
        //for (each outfall)
        //ReadDataFromFile outfallReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        //WriteDataToFile outfallWriteDataToFile = new WriteSWMM5RainfallToFile();
        //ExternalInflow outfallDryWeatherInflow = new DryWeatherInflow();
        //ExternalInflow outfallRDII = new RainfallDependentInfiltrationInflow();
        //ProjectUnits outfallNodeUnits = new CubicMetersperSecond();
        String nodeName = "Out1";
        Double nodeElevation = 0.0;
        Double fixedStage = 0.0;
        LinkedHashMap<Instant, Double> tidalCurve = null;
        LinkedHashMap<Instant, Double> stageTimeseries = null;
        boolean gated = false;
        String routeTo = "";

        outfalls.put(nodeName, new Outfall(nodeElevation, fixedStage, tidalCurve,stageTimeseries,
                gated, routeTo));
    }

    private void setLinks() {
        //for (each link) TODO check if present
        setConduit();
    }

    private void setConduit() {

        String linkName = "L1";
        Double x = 0.0;
        Double y = 0.0;
        double upElevation = 1000;
        double downElevation = 999;
        String upstreamNodeName = "N1";
        String downstreamNodeName = "Out1";
        Double linkLength = 100.0;
        Double linkRoughness = 0.01;
        Double upstreamOffset = 0.0;
        Double downstreamOffset = 0.0;
        Double initialFlowRate = 0.0;
        Double maximumFlowRate = 0.0;
        Double linkSlope = 10.0/linkLength; //TODO how SWMM evaluate it?
        Double diameter = 0.8;

        CrossSectionType crossSectionType = new Circular(diameter);
        //ProjectUnits linkUnits = new CubicMetersperSecond();

        OutsideSetup upstreamOutside = new OutsideSetup(upstreamNodeName, upstreamOffset, maximumFlowRate, x, y, upElevation);
        OutsideSetup downstreamOutside = new OutsideSetup(downstreamNodeName, downstreamOffset, maximumFlowRate, x, y, downElevation);

        conduit.put(linkName, new Conduit(routingSetup, crossSectionType, upstreamOutside, downstreamOutside, linkLength,
                linkRoughness, linkSlope));
    }



    private List<Subarea> divideAreas(Double imperviousPercentage, Double subcatchmentArea,
                                      Double imperviousWOstoragePercentage, Double depressionStoragePervious, Double depressionStorageImpervious,
                                      Double roughnessCoefficientPervious, Double roughnessCoefficientImpervious,
                                      String perviousTo, String imperviousTo, Double percentageFromPervious, Double percentageFromImpervious) {

        Double imperviousWOStorageArea = subcatchmentArea * imperviousPercentage * imperviousWOstoragePercentage;
        Double imperviousWStorageArea = subcatchmentArea * imperviousPercentage  - imperviousWOStorageArea;
        Double perviousArea = subcatchmentArea * (1-imperviousPercentage);

        List<Subarea> tmpSubareas = new LinkedList<>();
        if(imperviousPercentage == 0.0) {
            tmpSubareas.add(new Pervious(perviousArea, depressionStoragePervious, roughnessCoefficientImpervious));
        }
        else if(imperviousPercentage == 1.0) {
            if (imperviousWOstoragePercentage != 0.0) {
                tmpSubareas.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious));
            }
            if (imperviousWOstoragePercentage != 1.0) {
                tmpSubareas.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious));
            }

        }
        else {
            if (perviousTo.equals("IMPERVIOUS")) {
                tmpSubareas.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious));

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new Pervious(perviousArea, depressionStoragePervious, roughnessCoefficientPervious));

                tmpSubareas.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, percentageFromPervious, tmpConnections));
            }
            else if(perviousTo.equals("OUTLET")) {
                tmpSubareas.add(new Pervious(perviousArea, depressionStoragePervious, roughnessCoefficientPervious));
            }

            if (imperviousTo.equals("PERVIOUS")) {

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious));
                tmpConnections.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, percentageFromPervious, tmpConnections));

                tmpSubareas.add(new Pervious(perviousArea, depressionStoragePervious, roughnessCoefficientPervious,
                        percentageFromImpervious, tmpConnections));
            }
            else if (imperviousTo.equals("OUTLET")) {
                tmpSubareas.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious));
                tmpSubareas.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious));
            }
        }
        return tmpSubareas;
    }

    //TODO add at each subcatchment!
    private void setInitialValues(Integer id) {
        for(Subarea subarea : areas.get("Sub1").getSubareas()) {
            subarea.setFlowRate(id, timeSetup.getStartDate(), 0.0);
            subarea.setRunoffDepth(id, timeSetup.getStartDate(), 0.0);
            subarea.setTotalDepth(id, timeSetup.getStartDate(), 0.0);
        }

        for(Subarea subarea : areas.get("Sub2").getSubareas()) {
            subarea.setFlowRate(id, timeSetup.getStartDate(), 0.0);
            subarea.setRunoffDepth(id, timeSetup.getStartDate(), 0.0);
            subarea.setTotalDepth(id, timeSetup.getStartDate(), 0.0);
        }

        Instant time = timeSetup.getStartDate();
        while (time.isBefore(timeSetup.getEndDate())) {
            conduit.get("L1").setInitialUpFlowRate(id, time, 0.0);
            conduit.get("L1").setInitialUpWetArea(id, time, 0.0);
            time = time.plusSeconds(routingSetup.getRoutingStepSize());
        }
        conduit.get("L1").setInitialUpFlowRate(id, time, 0.0);
        conduit.get("L1").setInitialUpWetArea(id, time, 0.0);
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

    public void setNodeFlowRate(String nodeName, HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate) {
        junctions.get(nodeName).sumFlowRate(flowRate);
    }

    public void setLinkFlowRate(String linkName, HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate) {
        conduit.get(linkName).sumUpstreamFlowRate(flowRate);
    }

    public void upgradeSubtrees(String outLink, HashMap<Integer, List<Integer>> subtrees) {

        double downstreamDepthOut = getConduit(outLink).getDownstreamOutside().getWaterDepth();
        double maxDepth = downstreamDepthOut;

        for (Integer subtreeId : subtrees.keySet()) {
            double downstreamDepth = getConduit(String.valueOf(subtreeId)).getDownstreamOutside().getWaterDepth();
            if (downstreamDepth > maxDepth) {
                maxDepth = downstreamDepth;
            }
        }

        List<Integer> outLinks = null;
        outLinks.add(Integer.decode(outLink));
        if (downstreamDepthOut - maxDepth != 0.0) {
            upgradeStream(outLinks, downstreamDepthOut - maxDepth);
        }

        for (List<Integer> subtreeList : subtrees.values()) {
            int firstSon = subtreeList.size();
            double downstreamDepth = getConduit(String.valueOf(firstSon)).getDownstreamOutside().getWaterDepth();
            if (downstreamDepth - maxDepth != 0.0) {
                upgradeStream(subtreeList, downstreamDepth - maxDepth);
            }
        }
    }

    private void upgradeStream(List<Integer> subtreeList, double delta) {
        for (Integer subtreeLink : subtreeList) {
            OutsideSetup upstream = getConduit(String.valueOf(subtreeLink)).getUpstreamOutside();
            OutsideSetup downstream = getConduit(String.valueOf(subtreeLink)).getDownstreamOutside();

            upstream.upgradeOffset(delta);
            downstream.upgradeOffset(delta);
        }
    }
}