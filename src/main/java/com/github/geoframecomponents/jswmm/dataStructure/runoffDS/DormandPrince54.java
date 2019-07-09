/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.geoframecomponents.jswmm.dataStructure.runoffDS;

import oms3.annotations.*;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;

@Description("ODE solver for Manning equation over subcatchments")
@Author(name = "ftt01", contact = "dallatorre.daniele@gmail.com")
@Status(Status.DRAFT)
@License("GPL3.0")

public class DormandPrince54 implements RunoffSolverMethod {

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

    public double[] integrate(Double initialTime, double[] inputValues,
                       Double finalTime, double[] outputValues){

        dp54.integrate(ode, initialTime, inputValues, finalTime, outputValues);
        return outputValues;
    }
}