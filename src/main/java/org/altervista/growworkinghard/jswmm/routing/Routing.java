package org.altervista.growworkinghard.jswmm.routing;

import oms3.annotations.Execute;
import oms3.annotations.Finalize;
import oms3.annotations.In;
import oms3.annotations.Initialize;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.Conduit;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingSetup;

import java.time.Instant;

public class Routing {

    @In
    public String linkName;

    @In
    public Instant initialTime;

    @In
    public Instant totalTime;

    @In
    public Long routingStepSize;

    @In
    public OutsideSetup upstreamOutside;

    @In
    public OutsideSetup downstreamOutside;

    @In
    public Double linkLength;

    @In
    public Double linkRoughness;

    @In
    public CrossSectionType crossSectionType;

    @In
    public RoutingSetup routingSetup;

    @In
    public SWMMobject dataStructure = null;

    @Initialize
    void init() {
        if(dataStructure != null) {
            this.linkName = dataStructure.getRoutingSetup().getLinkName();
            this.initialTime = dataStructure.getTimeSetup().getStartDate();
            this.totalTime = dataStructure.getTimeSetup().getEndDate();
            this.routingStepSize = dataStructure.getRoutingSetup().getRoutingStepSize();

            Conduit conduit = dataStructure.getConduit().get(linkName);
            this.upstreamOutside = conduit.getUpstreamOutside();
            this.downstreamOutside = conduit.getDownstreamOutside();

            this.linkLength = conduit.getLinkLength();
            this.linkRoughness = conduit.getLinkRoughness();
            this.crossSectionType = conduit.getCrossSectionType();
        }
    }

    @Execute
    public void routingRun() {

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            routingSetup.evaluateWetArea(currentTime, routingStepSize, upstreamOutside, downstreamOutside, linkLength, linkRoughness,
                                crossSectionType);

            currentTime = currentTime.plusSeconds(routingStepSize);
        }

    }

    @Finalize
    void upgradeSWMMobject() {

    }

}
