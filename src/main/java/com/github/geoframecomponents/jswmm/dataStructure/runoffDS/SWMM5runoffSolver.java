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

import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;

public class SWMM5runoffSolver extends AbstractRunoffSolver {

    private FirstOrderDifferentialEquations ode = new RunoffODE();

    public SWMM5runoffSolver(Long runoffStepSize, Double minimumStepSize, Double maximumStepSize,
                             Double absoluteRunoffTolerance, Double relativeRunoffTolerance, Unitable units) {
        this.runoffStepSize = runoffStepSize;
        this.minimumStepSize = minimumStepSize;
        this.maximumStepSize = maximumStepSize;
        this.absoluteRunoffTolerance = absoluteRunoffTolerance;
        this.relativeRunoffTolerance = relativeRunoffTolerance;
        this.units = units;
    }

    @Override
    public FirstOrderIntegrator getFirstOrderIntegrator() {
        String ODEintegrator = "DP54";

        if(ODEintegrator == "DP54") {
            return new DormandPrince54Integrator(minimumStepSize, maximumStepSize,
                    absoluteRunoffTolerance, relativeRunoffTolerance);
        }
        return null;
    }

    @Override
    public FirstOrderDifferentialEquations getOde() {
        return ode;
    }

    @Override
    public void setOde(Double rainfall, Double depthFactor) {
        this.ode = new RunoffODE(rainfall, depthFactor);
    }
}