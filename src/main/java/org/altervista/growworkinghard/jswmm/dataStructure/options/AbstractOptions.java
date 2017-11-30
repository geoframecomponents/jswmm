package org.altervista.growworkinghard.jswmm.dataStructure.options;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.infiltration.InfiltrationSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;

public class AbstractOptions {

     RunoffSetup runoffSetup;
     RoutingSetup routingSetup;
     InfiltrationSetup infiltrationSetup;
     SteadyStateSetup steadyStateSetup;
     ProjectUnits projectUnits;

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
}
