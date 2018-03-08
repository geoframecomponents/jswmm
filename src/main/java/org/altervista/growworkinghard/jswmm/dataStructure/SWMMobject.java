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
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.CubicMetersperSecond;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.GlobalTimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
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

    public SWMMobject(String inpFileName) throws IOException {
        setTime();
        setRunoff();
        setRouting();
        setRaingages();
        setSubcatchments();
        setNodes();
        setLinks();
    }

    public SWMMobject() throws IOException {
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
        Instant startDate = Instant.parse("2000-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2000-01-01T05:00:00Z");
        Instant reportStartDate = Instant.parse("2000-01-01T00:00:00Z");
        Instant reportEndDate = Instant.parse("2000-01-02T00:00:00Z");
        Instant sweepStart = Instant.parse("2000-01-01T00:00:00Z");
        Instant sweepEnd = Instant.parse("2000-01-02T00:00:00Z");
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
        Long runoffStepSize = 60L; //must be in seconds!!

        Double minimumStepSize = 1.0e-8;
        Double maximumStepSize = 1.0e+3;
        Double absoluteRunoffTolerance = 1.0e-10;
        Double relativeRunoffTolerance = 1.0e-10;

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

        routingSetup = new RoutingSteadySetup(initialTime, totalTime, routingStepSize, toleranceMethod);
    }

    private void setRaingages() throws IOException {

        //for (each raingage)
        ReadDataFromFile readDataFromFile = new ReadSWMM5RainfallFile("./data/rainfall.txt");
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
        Double subcatchmentArea = 1E7;

        Double imperviousPercentage = 1.0;
        Double imperviousWOstoragePercentage = 0.0;

        Double depressionStorageImpervious = 0.0;
        Double depressionStoragePervious = 0.0;

        String perviousTo = "OUTLET";
        Double percentageFromPervious = 0.0;

        String imperviousTo = "OUTLET";
        Double percentageFromImpervious = 0.0;

        Double roughnessCoefficientPervious = 0.1;
        Double roughnessCoefficientImpervious = 0.01;

        Double characteristicWidth = 100.0;
        Double areaSlope = 0.005;
        Double curbLength = 0.0;

        String raingageName = "STA01";
        SubcatchmentReceiverRunoff receiverSubcatchment = null;

        List<Subarea> subareas = divideAreas(imperviousPercentage, subcatchmentArea,
                imperviousWOstoragePercentage, depressionStoragePervious, depressionStorageImpervious,
                roughnessCoefficientPervious, roughnessCoefficientImpervious,
                perviousTo, imperviousTo, percentageFromPervious, percentageFromImpervious);

        areas.put(areaName, new Area(subcatchmentArea, raingageSetup.get(areaName), receiverSubcatchment,
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

        String nodeName = "N1";
        Double nodeElevation = 2.0;
        Double maximumDepthNode = 3.0;
        Double initialDepthNode = 0.0;
        Double maximumDepthSurcharge = 1.0;
        Double nodePondingArea = 200.0;

        junctions.put(nodeName, new Junction(nodeElevation, maximumDepthNode, initialDepthNode,
                maximumDepthSurcharge, nodePondingArea));
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
        //setConduit();
    }

    private void setConduit() {

        String linkName = "L1";
        Double linkSlope = 0.1;
        String upstreamNodeName = "";
        String downstreamNodeName = "";
        Double linkLength = 0.0;
        Double linkRoughness = null;
        Double upstreamOffset = 0.0;
        Double downstreamOffset = 0.0;
        Double initialFlowRate = 0.0;
        Double maximumFlowRate = 0.0;

        CrossSectionType crossSectionType = new Circular(2.0);
        //ProjectUnits linkUnits = new CubicMetersperSecond();

        OutsideSetup upstreamOutside = new OutsideSetup(upstreamNodeName, upstreamOffset, initialFlowRate, maximumFlowRate);
        OutsideSetup downstreamOutside = new OutsideSetup(downstreamNodeName, downstreamOffset, initialFlowRate, maximumFlowRate);

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
    }
}