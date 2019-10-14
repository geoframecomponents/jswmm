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

public class RoutingDynamicWaveOptions implements RoutingOptions {

    private final Long routingStepSize;

    public RoutingDynamicWaveOptions(Long routingStepSize) {
        this.routingStepSize = routingStepSize;
    }

    @Override
    public RoutedFlow routeFlowRate(Integer id, Instant currentTime, double upstreamFlow,
                                    OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                                    Double linkSlope, CrossSectionType crossSectionType) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public Long adaptTimeDelay(Long routingStepSize, Long timeDelay) {
        throw new NullPointerException("Nothing implemented yet");
    }

    //@Override
    //public Double evaluateStreamWetArea(Double runoffFlowRate, Double linkLength, Double linkRoughness) {
    //    throw new NullPointerException("Nothing implemented yet");
    //}

    @Override
    public Long getRoutingStepSize() {
        return this.routingStepSize;
    }
}
