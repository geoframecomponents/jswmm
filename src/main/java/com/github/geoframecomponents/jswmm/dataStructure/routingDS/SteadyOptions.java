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

package com.github.geoframecomponents.jswmm.dataStructure.routingDS;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

import java.time.Instant;

public class RoutingSteadyOptions implements RoutingOptions {

    private SWMMroutingTools routingTools;

    public RoutingSteadyOptions(Long routingStepSize, Integer referenceTableLength) {
        this.routingTools = new SWMMroutingTools(referenceTableLength);
    }

    public RoutingSteadyOptions(Long routingStepSize) {
        this(routingStepSize, 180);
    }

    @Override
    public RoutedFlow routeFlowRate(Integer id, Instant currentTime, double upstreamFlow,
                                    OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                                    Double linkSlope, CrossSectionType crossSectionType, double routingStepSize) {

        Double dischargeFull = crossSectionType.getDischargeFull(linkRoughness, linkSlope);
        Double Afull = crossSectionType.getAreaFull();

        final Double beta = (Math.sqrt(linkSlope) * linkRoughness) / dischargeFull; //with Gs as linkRoughness

        double currentFlow = upstreamFlow / dischargeFull;
        double area = routingTools.sectionFactorToArea(currentFlow / beta) * Afull;
        double celerity = currentFlow * dischargeFull / area;
        double timeDelay = (linkLength / celerity);
        double timeDelayLong = adaptTimeDelay(routingStepSize, timeDelay);

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

        //System.out.println(" discharge " + qout * dischargeFull);
        //System.out.println("dischargeFull " + dischargeFull);

        return new RoutedFlow(currentTime.plusSeconds((long) timeDelayLong), (qout * dischargeFull));
     }

    @Override
    public double adaptTimeDelay(double routingStepSize, double timeDelay) {
        double temp = timeDelay / routingStepSize;
        return temp * routingStepSize;
    }
}