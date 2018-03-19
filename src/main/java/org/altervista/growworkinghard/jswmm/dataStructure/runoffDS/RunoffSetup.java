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

package org.altervista.growworkinghard.jswmm.dataStructure.runoffDS;

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

    public Double getMinimumStepSize();

    public Double getMaximumStepSize();

    public Double getAbsoluteRunoffTolerance();

    public Double getRelativeRunoffTolerance();
}
