package org.altervista.growworkinghard.jswmm.dataStructure.runoff;

import org.altervista.growworkinghard.jswmm.runoff.RunoffODE;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.time.Instant;

public class SWMM5RunoffSetup implements RunoffSetup {

    private String areaName;

    private Instant initialTime;

    private Instant totalTime;

    private Long runoffStepSize;

    private FirstOrderIntegrator firstOrderIntegrator;

    private FirstOrderDifferentialEquations ode = new RunoffODE(0.0, 0.0);

    public SWMM5RunoffSetup(Instant initialTime, Instant totalTime, Long runoffStepSize, FirstOrderIntegrator firstOrderIntegrator) {
        this.initialTime = initialTime;
        this.totalTime = totalTime;
        this.runoffStepSize = runoffStepSize;
        this.firstOrderIntegrator = firstOrderIntegrator;
    }

    @Override
    public Instant getInitialTime() {
        return initialTime;
    }

    @Override
    public Instant getTotalTime() {
        return totalTime;
    }

    @Override
    public Long getRunoffStepSize() {
        return runoffStepSize;
    }

    @Override
    public FirstOrderIntegrator getFirstOrderIntegrator() {
        return firstOrderIntegrator;
    }

    @Override
    public FirstOrderDifferentialEquations getOde() {
        return ode;
    }

    @Override
    public void setOde(Double rainfall, Double depthFactor) {
        this.ode = new RunoffODE(rainfall, depthFactor);
    }

    @Override
    public String getAreaName() {
        return areaName;
    }
}
