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

package org.altervista.growworkinghard.jswmm.runoff;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.util.FastMath;

public class RunoffODE implements FirstOrderDifferentialEquations {

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