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
        Instant endDate = Instant.parse("2018-01-01T02:00:00Z");
        Instant reportStartDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant reportEndDate = Instant.parse("2018-01-01T00:02:00Z");
        Instant sweepStart = Instant.parse("2018-01-01T00:00:00Z");
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
        Long runoffStepSize = 300L; //must be in seconds!!

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
            readDataFromFile = new ReadSWMM5RainfallFile("./data/rainfallNetwork.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ProjectUnits raingageUnits = new CubicMetersperSecond();
        String raingageName = "RG1";
        String dataSourceName = "rainfallNetwork.txt";
        String stationName = "RG1";
        Long rainfallStepSize = 60L;
        //TODO FORMATDATA
        //Instant rainfallStartDate = Instant.parse("2000-04-04T00:00Z");
        //Instant rainfallEndDate = Instant.parse("2000-04-04T00:00Z");
        //Double snowpack = 0.0;

        raingageSetup.put(raingageName, new GlobalRaingage(readDataFromFile, dataSourceName, stationName, rainfallStepSize));
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

        String areaName1 = "1";
        String areaName2 = "2";
        String areaName3 = "3";
        String areaName4 = "4";
        String areaName5 = "5";
        String areaName6 = "6";

        Double subcatchmentArea = 10000.0; //meters*meters

        Double imperviousPercentage = 0.25;
        Double imperviousWOstoragePercentage = 0.25;

        Double depressionStorageImpervious = 0.00005;
        Double depressionStoragePervious = 0.00005;

        String perviousTo = "OUTLET";
        Double percentageFromPervious = 0.0;

        String imperviousTo = "OUTLET";
        Double percentageFromImpervious = 0.0;

        Double roughnessCoefficientPervious = 0.1;
        Double roughnessCoefficientImpervious = 0.01;

        Double characteristicWidth = 100.0;
        Double areaSlope = 0.005;
        Double curbLength = 0.0;

        String raingageName = "RG1";
        ReceiverRunoff receiverSubcatchment = null;

        List<Subarea> subareas = divideAreas(imperviousPercentage, subcatchmentArea,
                imperviousWOstoragePercentage, depressionStoragePervious, depressionStorageImpervious,
                roughnessCoefficientPervious, roughnessCoefficientImpervious,
                perviousTo, imperviousTo, percentageFromPervious, percentageFromImpervious);

        areas.put(areaName1, new Area(subcatchmentArea, raingageSetup.get(areaName1),
                characteristicWidth, areaSlope, subareas));
        areas.put(areaName2, new Area(subcatchmentArea, raingageSetup.get(areaName2),
                characteristicWidth, areaSlope, subareas));
        areas.put(areaName3, new Area(subcatchmentArea, raingageSetup.get(areaName3),
                characteristicWidth, areaSlope, subareas));
        areas.put(areaName4, new Area(subcatchmentArea, raingageSetup.get(areaName4),
                characteristicWidth, areaSlope, subareas));
        areas.put(areaName5, new Area(subcatchmentArea, raingageSetup.get(areaName5),
                characteristicWidth, areaSlope, subareas));
        areas.put(areaName6, new Area(subcatchmentArea, raingageSetup.get(areaName6),
                characteristicWidth, areaSlope, subareas));
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

        String nodeName1 = "N1";
        String nodeName2 = "N2";
        String nodeName3 = "N3";
        String nodeName4 = "N4";
        String nodeName5 = "N5";
        String nodeName6 = "N6";

        Double nodeElevation1 = 5.0;
        Double nodeElevation2 = 4.80;
        Double nodeElevation3 = 5.0;
        Double nodeElevation4 = 4.80;
        Double nodeElevation5 = 4.50;
        Double nodeElevation6 = 4.20;

        Double maximumDepthNode = 3.0;
        Double initialDepthNode = 0.0;
        Double maximumDepthSurcharge = 1.0;
        Double nodePondingArea = 200.0;

        junctions.put(nodeName1, new Junction(nodeElevation1, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
        junctions.put(nodeName2, new Junction(nodeElevation2, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
        junctions.put(nodeName3, new Junction(nodeElevation3, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
        junctions.put(nodeName4, new Junction(nodeElevation4, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
        junctions.put(nodeName5, new Junction(nodeElevation5, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
        junctions.put(nodeName6, new Junction(nodeElevation6, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
    }

    private void setOutfalls() {
        //for (each outfall)
        //ReadDataFromFile outfallReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        //WriteDataToFile outfallWriteDataToFile = new WriteSWMM5RainfallToFile();
        //ExternalInflow outfallDryWeatherInflow = new DryWeatherInflow();
        //ExternalInflow outfallRDII = new RainfallDependentInfiltrationInflow();
        //ProjectUnits outfallNodeUnits = new CubicMetersperSecond();
//        String nodeName = "Out1";
//        Double nodeElevation = 0.0;
//        Double fixedStage = 0.0;
//        LinkedHashMap<Instant, Double> tidalCurve = null;
//        LinkedHashMap<Instant, Double> stageTimeseries = null;
//        boolean gated = false;
//        String routeTo = "";
//
//        outfalls.put(nodeName, new Outfall(nodeElevation, fixedStage, tidalCurve,stageTimeseries,
//                gated, routeTo));
    }

    private void setLinks() {
        //for (each link) TODO check if present
        setConduit();
    }

    private void setConduit() {

        String linkName8 = "8";
        String linkName9 = "9";
        String linkName10 = "10";
        String linkName11 = "11";
        String linkName12 = "12";

        double terrain8up = 5.0;
        double terrain8down = 4.80;
        double terrain9up = 5.0;
        double terrain9down = 4.80;
        double terrain10up = 4.80;
        double terrain10down = 4.50;
        double terrain11up = 4.80;
        double terrain11down = 4.50;
        double terrain12up = 4.50;
        double terrain12down = 4.20;

        Double x8up = 0.0;
        Double x8down = 0.0;
        Double x9up = 200.0;
        Double x9down = 0.0;
        Double x10up = 0.0;
        Double x10down = 0.0;
        Double x11up = -200.0;
        Double x11down = 0.0;
        Double x12up = 0.0;
        Double x12down = 0.0;

        Double y8up = 0.0;
        Double y8down = -200.0;
        Double y9up = -200.0;
        Double y9down = -200.0;
        Double y10up = -200.0;
        Double y10down = -400.0;
        Double y11up = -400.0;
        Double y11down = -400.0;
        Double y12up = -400.0;
        Double y12down = -600.0;

        String upstreamNodeName8 = "N1";
        String upstreamNodeName9 = "N3";
        String upstreamNodeName10 = "N4";
        String upstreamNodeName11 = "N2";
        String upstreamNodeName12 = "N5";

        String downstreamNodeName8 = "N4";
        String downstreamNodeName9 = "N4";
        String downstreamNodeName10 = "N5";
        String downstreamNodeName11 = "N5";
        String downstreamNodeName12 = "N6";

        Double linkLength = 200.0;
        Double linkRoughness = 0.01;
        Double upstreamOffset = 0.0;
        Double downstreamOffset = 0.0;
        Double initialFlowRate = 0.0;
        Double maximumFlowRate = 0.0;
        Double diameter = 1.0;

        CrossSectionType crossSectionType = new Circular(diameter);
        //ProjectUnits linkUnits = new CubicMetersperSecond();

        OutsideSetup upstreamOutside8 = new OutsideSetup(upstreamNodeName8, upstreamOffset,
                maximumFlowRate, x8up, y8up, terrain8up);
        OutsideSetup downstreamOutside8 = new OutsideSetup(downstreamNodeName8, downstreamOffset,
                maximumFlowRate, x8down, y8down, terrain8down);

        OutsideSetup upstreamOutside9 = new OutsideSetup(upstreamNodeName9, upstreamOffset,
                maximumFlowRate, x9up, y9up, terrain9up);
        OutsideSetup downstreamOutside9 = new OutsideSetup(downstreamNodeName9, downstreamOffset,
                maximumFlowRate, x9down, y9down, terrain9down);

        OutsideSetup upstreamOutside10 = new OutsideSetup(upstreamNodeName10, upstreamOffset,
                maximumFlowRate, x10up, y10up, terrain10up);
        OutsideSetup downstreamOutside10 = new OutsideSetup(downstreamNodeName10, downstreamOffset,
                maximumFlowRate, x10down, y10down, terrain10down);

        OutsideSetup upstreamOutside11 = new OutsideSetup(upstreamNodeName11, upstreamOffset,
                maximumFlowRate, x11up, y11up, terrain11up);
        OutsideSetup downstreamOutside11 = new OutsideSetup(downstreamNodeName11, downstreamOffset,
                maximumFlowRate, x11down, y11down, terrain11down);

        OutsideSetup upstreamOutside12 = new OutsideSetup(upstreamNodeName12, upstreamOffset,
                maximumFlowRate, x12up, y12up, terrain8up);
        OutsideSetup downstreamOutside12 = new OutsideSetup(downstreamNodeName12, downstreamOffset,
                maximumFlowRate, x12down, y12down, terrain12down);

        conduit.put(linkName8, new Conduit(routingSetup, crossSectionType, upstreamOutside8, downstreamOutside8,
                linkLength, linkRoughness));
        conduit.put(linkName9, new Conduit(routingSetup, crossSectionType, upstreamOutside9, downstreamOutside9,
                linkLength, linkRoughness));
        conduit.put(linkName10, new Conduit(routingSetup, crossSectionType, upstreamOutside10, downstreamOutside10,
                linkLength, linkRoughness));
        conduit.put(linkName11, new Conduit(routingSetup, crossSectionType, upstreamOutside11, downstreamOutside11,
                linkLength, linkRoughness));
        conduit.put(linkName12, new Conduit(routingSetup, crossSectionType, upstreamOutside12, downstreamOutside12,
                linkLength, linkRoughness));
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
        setSubareasInitialValue(id, "1");
        setSubareasInitialValue(id, "2");
        setSubareasInitialValue(id, "3");
        setSubareasInitialValue(id, "4");
        setSubareasInitialValue(id, "5");
        setSubareasInitialValue(id, "6");
        setInitialTime(id, "8");
        setInitialTime(id, "9");
        setInitialTime(id, "10");
        setInitialTime(id, "11");
        setInitialTime(id, "12");
    }

    private void setSubareasInitialValue(Integer id, String areaName) {
        for(Subarea subarea : areas.get(areaName).getSubareas()) {
            subarea.setFlowRate(id, timeSetup.getStartDate(), 0.0);
            subarea.setRunoffDepth(id, timeSetup.getStartDate(), 0.0);
            subarea.setTotalDepth(id, timeSetup.getStartDate(), 0.0);
        }
    }

    private void setInitialTime(Integer id, String linkName) {
        Instant time = timeSetup.getStartDate();
        while (time.isBefore(timeSetup.getEndDate())) {
            conduit.get(linkName).setInitialUpFlowRate(id, time, 0.0);
            conduit.get(linkName).setInitialUpWetArea(id, time, 0.0);
            time = time.plusSeconds(routingSetup.getRoutingStepSize());
        }
        conduit.get(linkName).setInitialUpFlowRate(id, time, 0.0);
        conduit.get(linkName).setInitialUpWetArea(id, time, 0.0);
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