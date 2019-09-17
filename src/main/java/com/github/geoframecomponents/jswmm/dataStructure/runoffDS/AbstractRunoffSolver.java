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

import com.github.geoframecomponents.jswmm.dataStructure.options.units.ProjectUnits;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

public abstract class AbstractRunoffSolver {

    public ProjectUnits units;

    public Long runoffStepSize;

    public Double minimumStepSize;
    public Double maximumStepSize;
    public Double absoluteRunoffTolerance;
    public Double relativeRunoffTolerance;

    public Long getRunoffStepSize() {
        return runoffStepSize;
    }

    public Double getMinimumStepSize() {
        return minimumStepSize;
    }

    public Double getMaximumStepSize() {
        return maximumStepSize;
    }

    public Double getAbsoluteRunoffTolerance() {
        return absoluteRunoffTolerance;
    }

    public Double getRelativeRunoffTolerance() {
        return relativeRunoffTolerance;
    }

    public abstract FirstOrderIntegrator getFirstOrderIntegrator();

    public abstract FirstOrderDifferentialEquations getOde();

    public abstract void setOde(Double rainfall, Double depthFactor);
}
