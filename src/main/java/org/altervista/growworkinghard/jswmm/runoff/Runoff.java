package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Subarea;
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
    SWMMobject dataStructure = new SWMMobject();

    Instant initialTime;
    Instant totalTime;
    long runoffStepSize;

    Subarea subareaPervious;
    Subarea subareaImperviousWStorage;
    Subarea subareaImperviousWOStorage;

    List<Subarea> subareas;
    LinkedHashMap<Instant, Double> totalAreaFlowRate;

    Double slopeArea;
    Double characteristicWidth;

    FirstOrderIntegrator firstOrderIntegrator;

    public enum OdeMethod {
        DP54
    }
    OdeMethod odeMethod;

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


    @Initialize
    void initialize() {
        if (dataStructure != null) {
            this.minimumStepSize = dataStructure.options.getRunoffSetup().getMinimumStepSize();
            this.maximumStepSize = dataStructure.options.getRunoffSetup().getMaximumStepSize();
            this.absoluteTolerance = dataStructure.options.getRunoffSetup().getAbsoluteTolerance();
            this.relativeTolerance = dataStructure.options.getRunoffSetup().getRelativeTolerance();

            subareas.add(subareaPervious);
            subareas.add(subareaImperviousWStorage);
            subareas.add(subareaImperviousWOStorage);
        }
    }


    public Runoff() throws IOException {

        if (odeMethod == DP54) {
            firstOrderIntegrator = new DormandPrince54Integrator(minimumStepSize, maximumStepSize,
                    absoluteTolerance, relativeTolerance);
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
        //area.evaluateTotalAreaFlowRate();
    }

}