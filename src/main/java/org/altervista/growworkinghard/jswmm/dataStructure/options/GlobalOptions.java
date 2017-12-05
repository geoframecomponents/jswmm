package org.altervista.growworkinghard.jswmm.dataStructure.options;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.infiltration.InfiltrationSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;

public class GlobalOptions extends AbstractOptions {

    public GlobalOptions(RunoffSetup runoffSetup, RoutingSetup routingSetup, InfiltrationSetup infiltrationSetup,
                         SteadyStateSetup steadyStateSetup, ProjectUnits projectUnits, TimeSetup timeSetup, ReportSetup reportSetup,
                         OffsetConvention offsetConvention, boolean ignoreRainfall, boolean ignoreSnowMelt,
                         boolean ignoreGroundwater, boolean ignoreRDII, boolean ignoreQuality, boolean allowPonding,
                         Integer numberOfThreads, String tempDirectory) {

        this.runoffSetup = runoffSetup;
        this.routingSetup = routingSetup;
        this.infiltrationSetup = infiltrationSetup;
        this.steadyStateSetup = steadyStateSetup;
        this.projectUnits = projectUnits;
        this.timeSetup = timeSetup;
        this.reportSetup = reportSetup;
        this.offsetConvention = offsetConvention;
        this.ignoreRainfall = ignoreRainfall;
        this.ignoreSnowMelt = ignoreSnowMelt;
        this.ignoreGroundwater = ignoreGroundwater;
        this.ignoreRDII = ignoreRDII;
        this.ignoreQuality = ignoreQuality;
        this.allowPonding = allowPonding;
        this.numberOfThreads = numberOfThreads;
        this.tempDirectory = tempDirectory;
    }
}
