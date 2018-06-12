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

package org.altervista.growworkinghard.jswmm.dataStructure.routingDS;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

import java.time.Instant;
import java.util.LinkedHashMap;

public class RoutingSteadySetup implements RoutingSetup {

    private final Long routingStepSize;

    private SWMMroutingTools routingTools;

    public RoutingSteadySetup(Long routingStepSize, Integer referenceTableLength) {
        this.routingStepSize = routingStepSize;
        this.routingTools = new SWMMroutingTools(referenceTableLength);
    }

    public RoutingSteadySetup(Long routingStepSize) {
        this(routingStepSize, 180);
    }

    @Override
    public RoutedFlow routeFlowRate(Integer id, Instant currentTime, OutsideSetup upstreamOutside,
                              OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                              Double linkSlope, CrossSectionType crossSectionType) {

        Double dischargeFull = crossSectionType.getDischargeFull(linkRoughness, linkSlope);
        Double Afull = crossSectionType.getAreaFull();

        final Double beta = (Math.sqrt(linkSlope) * linkRoughness) / dischargeFull; //should be Math.sqrt(linkS$

        LinkedHashMap<Instant, Double> upstreamFlow = upstreamOutside.getStreamFlowRate().get(id);

        double currentFlow = upstreamFlow.get(currentTime) / dischargeFull;
        double area = routingTools.sectionFactorToArea(currentFlow / beta) * Afull;
        double celerity = currentFlow * dischargeFull / area;
        Long timeDelay = (long) (linkLength / celerity);
        Long timeDelayLong = adaptTimeDelay(routingStepSize, timeDelay);

        double qout;
        if (currentFlow == 0.0) {
            qout = 0.0;
        }
        else if (currentFlow > 1.0) {
            qout = 1.0;
        }
        else {
            qout = currentFlow;
        }

        //System.out.println("qout " + qout);
        //System.out.println("dischargeFull " + dischargeFull);

        return new RoutedFlow(currentTime.plusSeconds(timeDelayLong), (qout * dischargeFull));
     }

    @Override
    public Long adaptTimeDelay(Long routingStepSize, Long timeDelay) {
        long temp = timeDelay / routingStepSize;
        return temp * routingStepSize;
    }

    @Override
     public Long getRoutingStepSize() {
        return this.routingStepSize;
    }
}
