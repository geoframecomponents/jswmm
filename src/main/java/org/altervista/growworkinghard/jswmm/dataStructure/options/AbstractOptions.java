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

package org.altervista.growworkinghard.jswmm.dataStructure.options;

import org.altervista.growworkinghard.jswmm.dataStructure.infiltration.InfiltrationSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.routingDS.RoutingSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.RunoffSetup;

public class AbstractOptions {

     RunoffSetup runoffSetup;
    RoutingSetup routingSetup;
    InfiltrationSetup infiltrationSetup;
    SteadyStateSetup steadyStateSetup;
    ProjectUnits projectUnits;
    TimeSetup timeSetup;
    ReportSetup reportSetup;

    public enum OffsetConvention {
        DEPTH,
        ELEVATION
    }
    OffsetConvention offsetConvention;

    boolean ignoreRainfall;
    boolean ignoreSnowMelt;
    boolean ignoreGroundwater;
    boolean ignoreRDII;
    boolean ignoreQuality;
    boolean allowPonding;

    Integer numberOfThreads;
    String tempDirectory;

    public RunoffSetup getRunoffSetup() {
        return runoffSetup;
    }

    public RoutingSetup getRoutingSetup() {
        return routingSetup;
    }

    public InfiltrationSetup getInfiltrationSetup() {
        return infiltrationSetup;
    }

    public SteadyStateSetup getSteadyStateSetup() {
        return steadyStateSetup;
    }

    public ProjectUnits getProjectUnits() {
        return projectUnits;
    }

    public TimeSetup getTimeSetup() {
        return timeSetup;
    }

    public ReportSetup getReportSetup() {
        return reportSetup;
    }

    public OffsetConvention getOffsetConvention() {
        return offsetConvention;
    }

    public boolean isIgnoreRainfall() {
        return ignoreRainfall;
    }

    public boolean isIgnoreSnowMelt() {
        return ignoreSnowMelt;
    }

    public boolean isIgnoreGroundwater() {
        return ignoreGroundwater;
    }

    public boolean isIgnoreRDII() {
        return ignoreRDII;
    }

    public boolean isIgnoreQuality() {
        return ignoreQuality;
    }

    public boolean isAllowPonding() {
        return allowPonding;
    }

    public Integer getNumberOfThreads() {
        return numberOfThreads;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }
}
