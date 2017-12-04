package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;

@Description("ODE solver for Manning equation over subcatchments")
@Author(name = "ftt01", contact = "dallatorre.daniele@gmail.com")
@Status(Status.DRAFT)
@License("GPL3.0")

public class DormandPrince54 extends AbstractRunoffMethod {

    @Description("Precipitation data")
    @In
    private Double precipitation;

    @Description("Constant depth factor")
    @In
    private Double depthFactor;

    @Description("Initial time")
    @In
    private Double initialTime;

    @Description("Final time")
    @In
    private Double finalTime;

    @Description("Initial value")
    @In
    private double[] initialValue = { 0.0 };

    @Description("Output step size")
    @In
    private Double outputStepSize = 0.0;

    @Description("Output values")
    @Out
    private double[] outputValues;


    private FirstOrderIntegrator dp54;

    private FirstOrderDifferentialEquations ode;

    protected DormandPrince54(Double precipitation, Double depthFactor,
                              Double minimumStepSize, Double maximumStepSize,
                              Double absoluteTolerance, Double relativeTolerance) {

        this.precipitation = precipitation;
        this.ode = new RunoffODE(precipitation,depthFactor);
        this.dp54 = new DormandPrince54Integrator(minimumStepSize, maximumStepSize,
                absoluteTolerance, relativeTolerance);
    }

    double[] integrate(Double initialTime, double[] inputValues,
                       Double finalTime, double[] outputValues){

        dp54.integrate(ode, initialTime, inputValues, finalTime, outputValues);
        return outputValues;
    }
}