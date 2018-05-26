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

import it.blogspot.geoframe.utils.GEOconstants;
import it.blogspot.geoframe.utils.GEOgeometry;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import org.altervista.growworkinghard.jswmm.dataStructure.routingDS.RoutingSetup;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Conduit extends AbstractLink {

    CrossSectionType crossSectionType;

    Double linkLength;
    Double linkRoughness;
    Double linkSlope;

    private Double maxDischarge;

    public Conduit(RoutingSetup routingSetup, CrossSectionType crossSectionType, OutsideSetup upstreamOutside,
                   OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness, Double linkSlope) {
        this.routingSetup = routingSetup;
        this.crossSectionType = crossSectionType;
        this.upstreamOutside = upstreamOutside;
        this.downstreamOutside = downstreamOutside;
        this.linkLength = linkLength;
        this.linkRoughness = linkRoughness;
        this.linkSlope = linkSlope;
        this.maxDischarge = 0.0;
    }

    public Double getMaxDischarge() {
        return maxDischarge;
    }

    @Override
    public OutsideSetup getUpstreamOutside() {
        return upstreamOutside;
    }

    @Override
    public OutsideSetup getDownstreamOutside() {
        return downstreamOutside;
    }

    @Override
    public void sumUpstreamFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate) {
        if (upstreamOutside.streamFlowRate == null) {
            upstreamOutside.streamFlowRate = new HashMap<>();
        }
        for (Integer id : newFlowRate.keySet()) {
            for (Instant time : newFlowRate.get(id).keySet()) {
                Double tempValue = 0.0;
                if (upstreamOutside.streamFlowRate.containsKey(id)) {
                    tempValue = upstreamOutside.streamFlowRate.get(id).get(time);
                }
                Double tempNewValue = newFlowRate.get(id).get(time);
                LinkedHashMap<Instant, Double> sumValues = new LinkedHashMap<>();
                sumValues.put(time, tempValue + tempNewValue);
                upstreamOutside.streamFlowRate.put(id, sumValues);
            }
        }
    }

    @Override
    public void setInitialUpFlowRate(Integer id, Instant time, Double flowRate) {
        upstreamOutside.setFlowRate(id, time, flowRate);
    }

    @Override
    public void setInitialUpWetArea(Integer id, Instant time, double flowRate) {
        upstreamOutside.setWetArea(id, time, flowRate);
    }

    @Override
    public void evaluateFlowRate(Instant currentTime) {
        for (Integer id : this.getUpstreamOutside().getStreamFlowRate().keySet()) {
            routingSetup.evaluateFlowRate(id, currentTime, upstreamOutside, downstreamOutside,
                    linkLength, linkRoughness, linkSlope, crossSectionType);
        }
    }

    @Override
    public void evaluateMaxCurve(Instant currentTime) {
        for (Integer id : this.getUpstreamOutside().getStreamFlowRate().keySet()) {
            if ( this.getUpstreamOutside().getStreamFlowRate().get(id).get(currentTime) >= maxDischarge) {
                maxDischarge = this.getUpstreamOutside().getStreamFlowRate().get(id).get(currentTime);
            }

            LinkedHashMap<Instant, Double> tempHM = new LinkedHashMap<>();
            tempHM.put(currentTime, maxDischarge);
            this.getUpstreamOutside().getStreamFlowRate().put(id, tempHM);
        }
    }

    @Override
    public void evaluateDimension() {
        Double naturalSlope = computeNaturalSlope();

        computeDiameter(slope);

        Double diameter = diameterToCommercial();

        Double minSlope = computeMinSlope(diameter);
        if (slope < minSlope) {
            return evaluateDiameter(minSlope);
        }
        else {
            return diameter;
        }
    }

    private Double computeNaturalSlope() {
        return GEOgeometry.computeSlope(getUpstreamOutside().getNodeCoordinates().x,
                getUpstreamOutside().getNodeCoordinates().y, getUpstreamOutside().getTerrainElevation(),
                getDownstreamOutside().getNodeCoordinates().x,
                getDownstreamOutside().getNodeCoordinates().y, getDownstreamOutside().getTerrainElevation());
    }

    private double computeMinSlope(Double diameter) {
        double hydraulicRadius = crossSectionType.computeHydraulicRadious(diameter, crossSectionType.);

        return GEOconstants.SHEARSTRESS
                / (GEOconstants.WSPECIFICWEIGHT * hydraulicRadius);
    }
}