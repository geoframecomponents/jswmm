package org.altervista.growworkinghard.jswmm.routing;

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.Conduit;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingSetup;

import java.time.Instant;

public class Routing {

    @In
    public String linkName = null;

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
    private String downstreamNodeName;

    @In
    public Double linkLength;

    @In
    public Double linkRoughness;

    @In
    public CrossSectionType crossSectionType;

    @In
    public RoutingSetup routingSetup;

    @In
    @Out
    public SWMMobject dataStructure = null;


    @Initialize
    public void initialize() {
    //    if(dataStructure != null && linkName != null) {

        this.dataStructure = dataStructure;

            this.initialTime = dataStructure.getTimeSetup().getStartDate();
            this.totalTime = dataStructure.getTimeSetup().getEndDate();

	    this.routingSetup = dataStructure.getRoutingSetup();
            this.routingStepSize = routingSetup.getRoutingStepSize();

            Conduit conduit = dataStructure.getConduit().get(linkName);
            this.upstreamOutside = conduit.getUpstreamOutside();
            this.downstreamOutside = conduit.getDownstreamOutside();
            this.downstreamNodeName = downstreamOutside.getNodeName();

            this.linkLength = conduit.getLinkLength();
            this.linkRoughness = conduit.getLinkRoughness();
            this.crossSectionType = conduit.getCrossSectionType();
    }

    @Execute
    public void run() {

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            routingSetup.evaluateFlowRate(currentTime, routingStepSize, upstreamOutside, downstreamOutside, linkLength,
                    linkRoughness, crossSectionType);

            //routingSetup.evaluateStreamFlowRate(downstreamOutside.getStreamWetArea().get(currentTime));

            currentTime = currentTime.plusSeconds(routingStepSize);
        }

        dataStructure.getJunctions().get(downstreamNodeName).addRoutingFlowRate(downstreamOutside.getStreamFlowRate());
    }

    @Finalize
    void upgradeSWMMobject() {
    }

}