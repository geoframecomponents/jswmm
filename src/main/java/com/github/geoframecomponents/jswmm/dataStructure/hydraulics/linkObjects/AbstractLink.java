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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutingSolver;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class AbstractLink {

    protected Unitable linksUnits;
    protected Datetimeable linkTime;

    RoutingSolver routingSolver;

    OutsideSetup upstreamOutside;
    OutsideSetup downstreamOutside;

    public void setLinksUnits(Unitable linksUnits) {
        this.linksUnits = linksUnits;
    }

    public void setLinkTime(Datetimeable linkTime) {
        this.linkTime = linkTime;
    }

    public Unitable getLinksUnits() {
        return linksUnits;
    }

    public Datetimeable getLinkTime() {
        return linkTime;
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getDownstreamFlowRate() {
        return downstreamOutside.streamFlowRate;
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getUpstreamFlowRate() {
        return upstreamOutside.streamFlowRate;
    }

    public abstract OutsideSetup getUpstreamOutside();

    public abstract OutsideSetup getDownstreamOutside();

    public abstract void setInitialUpFlowRate(Integer id, Instant time, Double flowRate);

    public abstract void setInitialUpWetArea(Integer id, Instant startDate, double flowRate);

    public abstract void evaluateFlowRate();

    public abstract double evaluateMaxDischarge();

    public abstract void evaluateDimension(Double discharge, CommercialPipeSize pipeCompany);
}
