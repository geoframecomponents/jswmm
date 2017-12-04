package org.altervista.growworkinghard.jswmm.runoff;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.util.FastMath;

/**
 * @brief Implementation of the runoff Ordinary Differential Equation
 *
 * @description This class implements the runoff ODE that is used to evaluate the runoff over a defined watershed.
 *              The <strong>precipitation</strong> variable includes the rainfall data reduced by the evaporation and
 *              increased by the snow melting.
 *              The <strong>alpha</strong> variable is a factor evaluated as follow:
 *              \f{eqnarray*}{
 *                      \alpha_P &=& \frac{1.49\,W\,S^{^1/_2}}{A_1\,n_P}\quad pervious\ area\ A_1\\
 *                      \alpha_I &=& \frac{1.49\,W\,S^{^1/_2}}{(A_2+A_3)\,n_I}\quad impervious\ area\ A_2\ and\ A_3
 *              \f}
 *              where \f$W\f$ is the width, \f$S\f$ is the slope, \f$n_P\f$ and \f$n_I\f$ are the roughness coefficients
 *              for pervious and impervious areas.
 *
 * @author ftt01 dallatorre.daniele@gmail.com
 * @version 0.1
 * @date October 16, 2017
 * @copyright GNU Public License v3
 */

class RunoffODE implements FirstOrderDifferentialEquations {

    private double precipitation;
    private double alpha;

    public RunoffODE(double precipitation, double alpha) {
        this.precipitation = precipitation;
        this.alpha = alpha;
    }

    public int getDimension() {
        return 1;
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) {
        yDot[0] = precipitation - alpha*FastMath.pow(y[0],5.0/3.0);
    }

}