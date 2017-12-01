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

        super(runoffSetup, routingSetup, infiltrationSetup, steadyStateSetup, projectUnits, timeSetup, reportSetup, offsetConvention,
                ignoreRainfall, ignoreSnowMelt, ignoreGroundwater, ignoreRDII, ignoreQuality, allowPonding,
                numberOfThreads, tempDirectory);
    }
}
