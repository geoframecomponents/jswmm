package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject.Junction;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Area;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Subarea;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Runoff {

    @In
    public LinkedHashMap<Instant, Double> adaptedRainfallData;

    private LinkedHashMap<Instant, Double> evaporationData = null;

    /**
     * Time setup of the simulation
     */
    private Instant initialTime;

    private Instant totalTime;

    /**
     * Area setup
     */

    private Area area;

    @In
    public String areaName = null;

    private Junction node;

    @In
    public String nodeName = null;

    private List<Subarea> subareas;

    private Double slopeArea;

    private Double characteristicWidth;

    /**
     * Integration method setup
     */
    private FirstOrderIntegrator firstOrderIntegrator;

    private  Long runoffStepSize;

    /**
     * Data structure
     */
    @In
    @Out
    public SWMMobject dataStructure;

    private RunoffSetup runoffSetup;

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        if (dataStructure != null && areaName != null) {

            //this.dataStructure = dataStructure;
            //TODO evaporation!!
            this.runoffSetup = dataStructure.getRunoffSetup();
            TimeSetup timeSetup = dataStructure.getTimeSetup();
            this.area = dataStructure.getAreas().get(areaName);
            //TODO IF node is not Juntions????
            this.node = dataStructure.getJunctions().get(nodeName);

            //this.areaName = runoffSetup.getAreaName();
            this.initialTime = timeSetup.getStartDate();
            this.totalTime = timeSetup.getEndDate();
            this.runoffStepSize = runoffSetup.getRunoffStepSize();

            this.subareas = area.getSubareas();
            this.slopeArea = area.getAreaSlope();
            this.characteristicWidth = area.getCharacteristicWidth();

            //this.adaptedRainfallData = tempRainfall;
        } else {
            throw new NullPointerException("");//TODO
        }

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            //check snownelt - snowaccumulation TODO build a new component
            upgradeStepValues(currentTime);

            currentTime = currentTime.plusSeconds(runoffStepSize);
        }
        area.evaluateTotalFlowRate(); //TODO to be verified
        node.addRunoffFlowRate(area.getTotalAreaFlowRate());
        //System.out.println(area.getTotalAreaFlowRate().get(Instant.parse("2018-01-01T00:02:00Z")));
    }

    private void upgradeStepValues(Instant currentTime) {
        for (Subarea subarea : subareas) {
            subarea.setDepthFactor(slopeArea, characteristicWidth);
            subarea.evaluateFlowRate(adaptedRainfallData.get(currentTime), 0.0, currentTime, //TODO evaporation!!
                    runoffSetup, slopeArea, characteristicWidth);
        }
    }

    @Finalize
    public void upgradeNodeFlowRate() {
    }

    private List<Double> testingValues(String fileName) {
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

    public void test(String fileChecks) {
        LinkedHashMap<Instant, Double> evaluated = area.getTotalAreaFlowRate();
        List<Double> defined = testingValues(fileChecks);

        int i = 0;
        for(Map.Entry<Instant, Double> data : evaluated.entrySet()) {
            //TODO check a method to do it better - not always is ordered
            assertEquals(data.getValue(), defined.get(i), 0.85);
            //System.out.println(data.getValue());
            //System.out.println(defined.get(i));
            i = i + 1;
        }
    }
}