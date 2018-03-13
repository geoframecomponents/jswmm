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
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingKinematicWaveSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingSteadySetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.SWMM5RunoffSetup;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


import static org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.SubcatchmentReceiverRunoff.ReceiverType.SUBCATCHMENT;
public class SWMMobject {

    private TimeSetup timeSetup;
    private RunoffSetup runoffSetup;
    private RoutingSetup routingSetup;
    private HashMap<String, RaingageSetup> raingageSetup = new HashMap<>();
    private HashMap<String, Area> areas = new HashMap<>();
    private HashMap<String, Junction> junctions = new HashMap<>();
    private HashMap<String, Outfall> outfalls = new HashMap<>();
    private HashMap<String, Conduit> conduit = new HashMap<>();

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
        setInitialValues();
    }

    public TimeSetup getTimeSetup() {
        return timeSetup;
    }

    public RunoffSetup getRunoffSetup() {
        return runoffSetup;
    }

    public RoutingSetup getRoutingSetup() { return routingSetup; }

    public HashMap<String, RaingageSetup> getRaingages() {
        return raingageSetup;
    }

    public HashMap<String, Area> getAreas() {
        return areas;
    }

    public HashMap<String, Junction> getJunctions() {
        return junctions;
    }

    public HashMap<String, Outfall> getOutfalls() {
        return outfalls;
    }

    public HashMap<String, Conduit> getConduit() {
        return conduit;
    }

    private void setTime() {
        Instant startDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2018-01-01T01:00:00Z");
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

        String ODEintegrator = "DP54";

        FirstOrderIntegrator firstOrderIntegrator = null;
        if(ODEintegrator == "DP54") {
            firstOrderIntegrator = new DormandPrince54Integrator(minimumStepSize, maximumStepSize,
                    absoluteRunoffTolerance, relativeRunoffTolerance);
        }
        this.runoffSetup = new SWMM5RunoffSetup(initialTime, totalTime, runoffStepSize, firstOrderIntegrator);
    }

    private void setRouting() {

        Instant initialTime = timeSetup.getStartDate();
        Instant totalTime = timeSetup.getEndDate();

        Long routingStepSize = 30L;
        Double toleranceMethod = 0.0015;

        routingSetup = new RoutingKinematicWaveSetup(initialTime, totalTime, routingStepSize, toleranceMethod);
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
        SubcatchmentReceiverRunoff receiverSubcatchment2 = null;

        List<Subarea> subareas2 = divideAreas(imperviousPercentage2, subcatchmentArea2,
                imperviousWOstoragePercentage2, depressionStoragePervious2, depressionStorageImpervious2,
                roughnessCoefficientPervious2, roughnessCoefficientImpervious2,
                perviousTo2, imperviousTo2, percentageFromPervious2, percentageFromImpervious2);

        areas.put(areaName2, new Area(subcatchmentArea2, raingageSetup.get(areaName2), receiverSubcatchment2,
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

        OutsideSetup upstreamOutside = new OutsideSetup(upstreamNodeName, upstreamOffset, maximumFlowRate);
        OutsideSetup downstreamOutside = new OutsideSetup(downstreamNodeName, downstreamOffset, maximumFlowRate);

        conduit.put(linkName, new Conduit(crossSectionType, upstreamOutside, downstreamOutside, linkLength,
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
    private void setInitialValues() {
        for(Subarea subarea : areas.get("Sub1").getSubareas()) {
            subarea.setFlowRate(timeSetup.getStartDate(), 0.0);
            subarea.setRunoffDepth(timeSetup.getStartDate(), 0.0);
            subarea.setTotalDepth(timeSetup.getStartDate(), 0.0);
        }

        for(Subarea subarea : areas.get("Sub2").getSubareas()) {
            subarea.setFlowRate(timeSetup.getStartDate(), 0.0);
            subarea.setRunoffDepth(timeSetup.getStartDate(), 0.0);
            subarea.setTotalDepth(timeSetup.getStartDate(), 0.0);
        }
    }
}
    }
}
