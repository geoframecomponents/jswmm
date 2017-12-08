package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Area;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Subarea;
import org.altervista.growworkinghard.jswmm.dataStructure.options.AbstractOptions;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.altervista.growworkinghard.jswmm.runoff.Runoff.OdeMethod.DP54;

public class Runoff {

    @In
    LinkedHashMap<Instant, Double> adaptedRainfallData;

    @In
    LinkedHashMap<Instant, Double> evaporationData;

    @In
    LinkedHashMap<Instant, Double> adaptedInfiltrationData;

    @In
    @Out
    SWMMobject dataStructure;

    /**
     * Time setup of the simulation
     */
    private Instant initialTime;
    private Instant totalTime;
    private long runoffStepSize;

    /**
     * Area setup
     */
    private List<Subarea> subareas;

    Double slopeArea;
    Double characteristicWidth;

    /**
     * Integration method setup
     */
    public enum OdeMethod {
        DP54
    }
    OdeMethod odeMethod;    //TODO create in dataStructure!!
    FirstOrderIntegrator firstOrderIntegrator;

    @Description("Minimum step for evaluation of the ODE")
    @In
    private Double minimumStepSize = 1.0e-8;

    @Description("Maximum step for evaluation of the ODE")
    @In
    private Double maximumStepSize = 1.0e+3;

    @Description("Absolute tolerance for evaluation of the ODE")
    @In
    private Double absoluteTolerance = 1.0e-10;

    @Description("Relative tolerance for evaluation of the ODE")
    @In
    private Double relativeTolerance = 1.0e-10;

    AbstractOptions options = dataStructure.options;
    RunoffSetup runoffSetup = options.getRunoffSetup();
    Area areas = dataStructure.areas.get("A1");

    @Initialize
    void initialize() {
        if (dataStructure != null) {

            this.initialTime = options.getTimeSetup().getStartDate();
            this.totalTime = options.getTimeSetup().getEndDate();

            this.runoffStepSize = runoffSetup.getRunoffStepSize();

            this.subareas = areas.getSubareas();//TODO rewrite when SWMMobject is OK
            this.slopeArea = areas.getAreaSlope();
            this.characteristicWidth = areas.getCharacteristicWidth();

            if (odeMethod == DP54) {
                firstOrderIntegrator = new DormandPrince54Integrator(minimumStepSize, maximumStepSize,
                        absoluteTolerance, relativeTolerance);
            }

            this.minimumStepSize = runoffSetup.getMinimumStepSize();
            this.maximumStepSize = runoffSetup.getMaximumStepSize();
            this.absoluteTolerance = runoffSetup.getAbsoluteTolerance();
            this.relativeTolerance = runoffSetup.getRelativeTolerance();
        }
    }

    @Execute
    public void run() {

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            //check snownelt - snowaccumulation TODO build a new component
            upgradeStepValues(currentTime);

            currentTime.plus(runoffStepSize, (TemporalUnit) SECONDS);
        }
    }

    void upgradeStepValues(Instant currentTime) {
        for (Subarea subarea : subareas) {
            subarea.setDepthFactor(slopeArea, characteristicWidth);
            subarea.evaluateFlowRate(adaptedRainfallData.get(currentTime), evaporationData.get(currentTime), currentTime,
                    runoffStepSize, slopeArea, characteristicWidth);
        }
        areas.evaluateTotalFlowRate();      //TODO to be verified
    }
}