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

package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;

import java.time.Instant;

public class Conduit extends AbstractLink {

    CrossSectionType crossSectionType;

    Double linkLength;
    Double linkRoughness;
    Double linkSlope;

    public Conduit(CrossSectionType crossSectionType, OutsideSetup upstreamOutside, OutsideSetup downstreamOutside,
                   Double linkLength, Double linkRoughness, Double linkSlope) {
        this.crossSectionType = crossSectionType;
        this.upstreamOutside = upstreamOutside;
        this.downstreamOutside = downstreamOutside;
        this.linkLength = linkLength;
        this.linkRoughness = linkRoughness;
        this.linkSlope = linkSlope;
    }

    @Override
    public void evaluateFlowRate(Instant currentTime) {
        routingSetup.evaluateFlowRate(currentTime, routingStepSize, upstreamOutside, downstreamOutside,
                linkLength, linkRoughness, linkSlope, crossSectionType);
    }
}
