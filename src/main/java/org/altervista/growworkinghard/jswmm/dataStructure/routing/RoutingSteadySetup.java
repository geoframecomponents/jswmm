package org.altervista.growworkinghard.jswmm.dataStructure.routing;

import java.time.Instant;

public class RoutingSteadySetup implements RoutingSetup {

    Instant routingStepSize;
    //TODO instantiate the variables!!!!

    public RoutingSteadySetup(Instant routingStepSize) {
        this.routingStepSize = routingStepSize;
    }
}
