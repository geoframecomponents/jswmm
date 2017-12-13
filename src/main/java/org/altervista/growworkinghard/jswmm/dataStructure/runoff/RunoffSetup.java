package org.altervista.growworkinghard.jswmm.dataStructure.runoff;

import org.altervista.growworkinghard.jswmm.runoff.RunoffODE;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.time.Instant;

public interface RunoffSetup {

    public Instant getInitialTime();

    public Instant getTotalTime();

    public Long getRunoffStepSize();

    public FirstOrderIntegrator getFirstOrderIntegrator();

    public FirstOrderDifferentialEquations getOde();

    public void setOde(Double rainfall, Double depthFactor);

    public String getAreaName();
}
