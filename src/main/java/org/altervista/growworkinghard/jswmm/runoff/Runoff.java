package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Area;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.ReceiverRunoff;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Subarea;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

public class Runoff {

    @In
    public LinkedHashMap<Instant, Double> adaptedRainfallData;

    @In
    public LinkedHashMap<Instant, Double> evaporationData = null;

    /**
     * Time setup of the simulation
     */
    @In
    public Instant initialTime;

    @In
    public Instant totalTime;

    /**
     * Area setup
     */
    @In
    public String areaName = "S1";

    @In
    private List<Subarea> subareas;

    @In
    public Double slopeArea;

    @In
    public Double characteristicWidth;

    /**
     * Integration method setup
     */
    @In
    public FirstOrderIntegrator firstOrderIntegrator;

    @In
    public  Long runoffStepSize;

    /**
     * Data structure
     */
    @In
    @Out
    public SWMMobject dataStructure;

    private RunoffSetup runoffSetup;

    private List<ReceiverRunoff> receivers;

    public Runoff() throws IOException {
    }

    @Initialize
    public void initialize(LinkedHashMap<Instant, Double> tempRainfall, SWMMobject dataStructure) {
        if (dataStructure != null && areaName != null) {

            this.dataStructure = dataStructure;
            //TODO evaporation!!
            this.runoffSetup = dataStructure.getRunoffSetup();
            TimeSetup timeSetup = dataStructure.getTimeSetup();
            Area areas = dataStructure.getAreas().get(areaName);

            //this.areaName = runoffSetup.getAreaName();
            this.initialTime = timeSetup.getStartDate();
            this.totalTime = timeSetup.getEndDate();
            this.runoffStepSize = runoffSetup.getRunoffStepSize();

            this.subareas = areas.getSubareas();
            this.slopeArea = areas.getAreaSlope();
            this.characteristicWidth = areas.getCharacteristicWidth();

            this.adaptedRainfallData = tempRainfall;
            this.receivers = areas.getReceivers();
        }
    }

    @Execute
    public void run() {

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            //check snownelt - snowaccumulation TODO build a new component
            upgradeStepValues(currentTime);

            currentTime = currentTime.plusSeconds(runoffStepSize);
        }
        LinkedHashMap<Instant, Double> currentRunoff = dataStructure.getAreas().get(areaName).evaluateTotalFlowRate(); //TODO to be verified
        transferRunoffToReceiver(currentRunoff);
    }

    private void upgradeStepValues(Instant currentTime) {
        for (Subarea subarea : subareas) {
            subarea.setDepthFactor(slopeArea, characteristicWidth);
            subarea.evaluateFlowRate(adaptedRainfallData.get(currentTime), 0.0, currentTime, //TODO evaporation!!
                    runoffSetup, slopeArea, characteristicWidth);
        }
    }

    private void transferRunoffToReceiver(LinkedHashMap<Instant, Double> currentRunoff) {
        if(receivers != null) {
            for(ReceiverRunoff receiverRunoff : receivers) {
                currentRunoff.forEach((k, v) ->
                        receiverRunoff.getReceiverObject().getRunoffInflow().merge(k,
                                v*receiverRunoff.getPercentage(), Double::sum));
            }
        }
    }
}