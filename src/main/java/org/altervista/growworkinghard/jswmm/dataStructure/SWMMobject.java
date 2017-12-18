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
        Instant startDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2018-01-01T01:00:00Z");
        Instant reportStartDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant reportEndDate = Instant.parse("2018-01-01T00:00:00Z");
        Instant sweepStart = Instant.parse("2018-01-01T00:00:00Z");
        Instant sweepEnd = Instant.parse("2018-01-02T00:00:00Z");
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

        this.routingSetup = new RoutingSteadySetup(initialTime, totalTime, routingStepSize, toleranceMethod);
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
        String areaName = "S1";
        Double subcatchmentArea = 1E4;

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

        Double characteristicWidth = 100.0;
        Double areaSlope = 0.01;
        Double curbLength = 0.0;

        String raingageName = "STA01";
        ReceiverRunoff receiverSubcatchment = null;

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

        String nodeName = "J1";
        Double nodeElevation = 100.0;
        Double maximumDepthNode = 0.0;
        Double initialDepthNode = 0.0;
        Double maximumDepthSurcharge = 0.0;
        Double nodePondingArea = 0.0;

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
        String nodeName = "O1";
        Double nodeElevation = 90.0;
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

        String linkName = "C1";
        String upstreamNodeName = "J1";
        String downstreamNodeName = "O1";
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
        for(Subarea subarea : areas.get("S1").getSubareas()) {
            subarea.setFlowRate(timeSetup.getStartDate(), 0.0);
            subarea.setRunoffDepth(timeSetup.getStartDate(), 0.0);
            subarea.setTotalDepth(timeSetup.getStartDate(), 0.0);
        }
    }
}


/*

    long routingStepSize;

    RoutingSetup routingSetup = new RoutingSteadySetup(0.1);

    InfiltrationSetup infiltrationSetup;
    SteadyStateSetup steadyStateSetup;



    List<String> reportSubcatchment;
    List<String> reportNodes; //TODO define!!
    List<String> reportLinks; //TODO define!!
    List<LIDcontrol> reportLID; //TODO define!!

    public ReportSetup reportSetup = new ReportSetup(true, true, true, true,
                                    reportSubcatchment, reportNodes, reportLinks, reportLID);

    AbstractOptions.OffsetConvention offsetConvention = AbstractOptions.OffsetConvention.DEPTH;
    boolean ignoreRainfall = false;
    boolean ignoreSnowMelt = true;
    boolean ignoreGroundwater = true;
    boolean ignoreRDII = true;
    boolean ignoreQuality = true;
    boolean allowPonding = false;

    Integer numberOfThreads = 1;
    String temporaryDirectory = "";

    public AbstractOptions options = new GlobalOptions(runoffSetup, routingSetup, infiltrationSetup, steadyStateSetup,
            projectUnits, timeSetup, reportSetup, offsetConvention, ignoreRainfall, ignoreSnowMelt, ignoreGroundwater,
            ignoreRDII, ignoreQuality, allowPonding, numberOfThreads, temporaryDirectory);

    public List<RaingageSetup> raingages;

    public HashMap<String, Area> areas;

    public AbstractNode[] nodes;

    public AbstractLink[] links;

    FirstOrderIntegrator firstOrderIntegrator;

    AbstractFilesData filesData = new AbstractFilesData();

    //INPparser to select data
    // [TITLE]
    // [OPTIONS]
    // [REPORT]
    // [FILES]
    HashMap<String, FilesData.RainfallFile> rainfallFileHashMap = new HashMap<>();
    rainfallFileHashMap.put("", FilesData.RainfallFile.SAVE);
    filesData.filesDataIO.setRainfallFile(rainfallFileHashMap);

    HashMap<String, FilesData.RunoffFile> runoffFileHashMap = new HashMap<>();
    runoffFileHashMap.put("", FilesData.RunoffFile.SAVE);
    filesData.filesDataIO.setRunoffFile(runoffFileHashMap);

    HashMap<String, FilesData.HotstartFile> hotstartFileSaveHashMap = new HashMap<>();
    hotstartFileSaveHashMap.put("", FilesData.HotstartFile.SAVE);
    filesData.filesDataIO.setHotstartFileSave(hotstartFileSaveHashMap);

    HashMap<String, FilesData.HotstartFile> hotstartFileUseHashMap = new HashMap<>();
    hotstartFileUseHashMap.put("", FilesData.HotstartFile.USE);
    filesData.filesDataIO.setHotstartFileUse(hotstartFileUseHashMap);

    HashMap<String, FilesData.InflowFile> inflowFileHashMap = new HashMap<>();
    inflowFileHashMap.put("", FilesData.InflowFile.USE);
    filesData.filesDataIO.setInflowFile(inflowFileHashMap);

    HashMap<String, FilesData.OutflowFile> outflowFileHashMap = new HashMap<>();
    outflowFileHashMap.put("", FilesData.OutflowFile.SAVE);
    filesData.filesDataIO.setOutflowFile(outflowFileHashMap);

    // [RAINGAGES]----
    // [EVAPORATION]
    // [TEMPERATURE]
    // [ADJUSTMENTS]
    // [SUBCATCHMENTS]----
    // [SUBAREAS]----
    // [INFILTRATION]
    // [LID_CONTROLS]
    // [LID_USAGE]
    // [AQUIFERS]
    // [GROUNDWATER]
    // [GWF]
    // [SNOWPACKS]
    // [JUNCTIONS]----
    // [OUTFALLS]----
    // [DIVIDERS]
    // [STORAGE]
    // [CONDUITS]----
    // [PUMPS]
    // [ORIFICES]
    // [WEIRS]
    // [OUTLETS]
    // [XSECTIONS]----
    // [TRANSECTS]
    // [LOSSES]
    // [CONTROLS]
    // [POLLUTANTS]
    // [LANDUSES]
    // [COVERAGES]
    // [LOADINGS]
    // [BUILDUP]
    // [WASHOFF]
    // [TREATMENT]
    // [INFLOWS]
    // [DWF]
    // [RDII]
    // [HYDROGRAPHS]
    // [CURVES]
    // [TIMESERIES]----
    // [PATTERNS]


    //for (each raingage)

        ReadDataFromFile readDataFromFile = new ReadSWMM5RainfallFile("ciao");
        ProjectUnits raingageUnits = new CubicMetersperSecond();
        String raingageName = "RG1";
        String dataSourceName = "test1";
        String stationName = "STA01";
        Instant rainfallStartDate = Instant.parse("2000-04-04T00:00Z");
        Instant rainfallEndDate = Instant.parse("2000-04-04T00:00Z");
        Double snowpack = 0.0;
        raingages.add(0, new GlobalRaingage(readDataFromFile, raingageUnits, raingageName, dataSourceName, stationName,
                rainfallStartDate, rainfallEndDate, snowpack));

        //////for (each subcatchment)//////

        //ReadDataFromFile subcatchmentReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        //AcquiferSetup acquiferSetup = new Acquifer();
        //SnowPackSetup subcatchmentSnowpack = new SnowPack();
        //ProjectUnits subcatchmentUnits = new CubicMetersperSecond();
        //String subcatchmentName = "SUB1";
        Double subcatchmentArea = 1000.0;

        Double roughnessCoefficientPervious = 1.0;
        Double roughnessCoefficientImpervious = 1.0;

        LinkedHashMap<Instant, Double> depressionStoragePervious = null;
        LinkedHashMap<Instant, Double> depressionStorageImpervious = null;

        Double imperviousWstoragePercentage = 1.0;

        String areaName = "A1";
        //String relativeRaingageName = "STA01";
        //ReceiverRunoff subcatchmentReceiverRunoff = new ReceiverRunoff(SUBCATCHMENT, "");

        Double imperviousPercentage = 0.0;
        Double characteristicWidth = 100.0;
        Double subareaSlope = 0.2;
        Double curbLength = 0.0;

        SubareaReceiver.SubareaReceiverRunoff perviousTo = OUTLET;
        Double percentageFromPervious = 10.0;

        SubareaReceiver.SubareaReceiverRunoff imperviousTo = OUTLET;
        Double percentageFromImpervious = 10.0;


        Double imperviousWStorageArea = subcatchmentArea*imperviousPercentage*imperviousWstoragePercentage;
        Double imperviousWOStorageArea = subcatchmentArea*imperviousPercentage - imperviousWStorageArea;

        //readDataFromFile, acquiferSetup, subcatchmentSnowpack, subcatchmentUnits,
        //subcatchmentName, subcatchmentArea, relativeRaingageName, subcatchmentReceiverRunoff, perviousSubareaReceiverRunoff,
        //percentagePerviousReceiver, imperviousPercentage, characteristicWidth, subareaSlope, curbLength);

        List<Subarea> tmpSubareas = null;
        if(imperviousPercentage == 0.0) {
            tmpSubareas.add(new Pervious(subcatchmentArea, roughnessCoefficientPervious,
                    depressionStoragePervious, firstOrderIntegrator));
        }
        else if(imperviousPercentage == 1.0) {
            if (1 - imperviousWstoragePercentage != 0) {
                tmpSubareas.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, firstOrderIntegrator));
            }
            tmpSubareas.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                    roughnessCoefficientImpervious, firstOrderIntegrator));
        }
        else {
            if (perviousTo == IMPERVIOUS) {
                tmpSubareas.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, firstOrderIntegrator));

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new Pervious(subcatchmentArea, roughnessCoefficientPervious,
                        depressionStoragePervious, firstOrderIntegrator));

                tmpSubareas.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious,
                        percentageFromPervious, tmpConnections, firstOrderIntegrator));
            }
            else if(perviousTo == OUTLET) {
                tmpSubareas.add(new Pervious(subcatchmentArea, roughnessCoefficientPervious,
                        depressionStoragePervious, firstOrderIntegrator));
            }

            if (imperviousTo == PERVIOUS) {

                List<Subarea> tmpConnections = null;
                tmpConnections.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, firstOrderIntegrator));
                tmpConnections.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, firstOrderIntegrator));

                tmpSubareas.add(new Pervious(subcatchmentArea, depressionStoragePervious, roughnessCoefficientPervious,
                        percentageFromImpervious, tmpConnections, firstOrderIntegrator));
            }
            else if (imperviousTo == OUTLET) {
                tmpSubareas.add(new ImperviousWithStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        depressionStorageImpervious, roughnessCoefficientImpervious, firstOrderIntegrator));
                tmpSubareas.add(new ImperviousWithoutStorage(imperviousWStorageArea, imperviousWOStorageArea,
                        roughnessCoefficientImpervious, firstOrderIntegrator));
            }
        }
        areas.put(areaName, new Area(tmpSubareas));

        //for (each junction)
        ReadDataFromFile junctionReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        WriteDataToFile writeDataToFile = new WriteSWMM5RainfallToFile();
        ExternalInflow dryWeatherInflow = new DryWeatherInflow();
        ExternalInflow RDII = new RainfallDependentInfiltrationInflow();
        ProjectUnits nodeUnits = new CubicMetersperSecond();
        String nodeName = "N1";
        Double nodeElevation = 2.0;
        Double maximumDepthNode = 3.0;
        Double initialDepthNode = 0.0;
        Double maximumDepthSurcharge = 1.0;
        Double nodePondingArea = 200.0;

        //nodes[0] = new Junction(junctionReadDataFromFile, writeDataToFile, dryWeatherInflow, RDII, nodeUnits, nodeName,
        //       nodeElevation, maximumDepthNode, initialDepthNode, maximumDepthSurcharge, nodePondingArea);

        //for (each outfall)
        ReadDataFromFile outfallReadDataFromFile = new ReadSWMM5RainfallFile("ciao");
        WriteDataToFile outfallWriteDataToFile = new WriteSWMM5RainfallToFile();
        ExternalInflow outfallDryWeatherInflow = new DryWeatherInflow();
        ExternalInflow outfallRDII = new RainfallDependentInfiltrationInflow();
        ProjectUnits outfallNodeUnits = new CubicMetersperSecond();
        String outfallName = "N1";
        Double outfallElevation = 2.0;
        Double fixedStage = 3.0;
        LinkedHashMap<Instant, Double> tidalCurve = null;
        LinkedHashMap<Instant, Double> stageTimeseries = null;
        boolean gated = false;
        String routeTo = "";

        nodes[1] = new Outfall(outfallReadDataFromFile, outfallWriteDataToFile, outfallDryWeatherInflow, outfallRDII,
                outfallNodeUnits, outfallName, outfallElevation, fixedStage, tidalCurve, stageTimeseries,
                gated, routeTo);

        //for (each link)
        String linkName = "";
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
        ProjectUnits linkUnits = new CubicMetersperSecond();

        OutsideSetup upstreamOutside = new OutsideSetup();
        OutsideSetup downstreamOutside = new OutsideSetup();

        links[0] = new Conduit(crossSectionType, upstreamOutside, downstreamOutside, linkLength, linkRoughness, linkSlope);

    }
}

*/