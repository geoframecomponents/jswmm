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
import org.geotools.graph.util.geom.Coordinate2D;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Conduit extends AbstractLink {

    CrossSectionType crossSectionType;

    Double linkLength;
    Double linkRoughness;
    Double linkSlope;

    public Conduit(RoutingSetup routingSetup, CrossSectionType crossSectionType, OutsideSetup upstreamOutside,
                   OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness, Double linkSlope) {
        this.routingSetup = routingSetup;
        this.crossSectionType = crossSectionType;
        this.upstreamOutside = upstreamOutside;
        this.downstreamOutside = downstreamOutside;
        this.linkLength = linkLength;
        this.linkRoughness = linkRoughness;
        this.linkSlope = linkSlope;
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

        HashMap<Integer, LinkedHashMap<Instant, Double>> flowUpstream = getUpstreamOutside().getStreamFlowRate();

        if ( flowUpstream == null) {
            flowUpstream = new HashMap<>();
        }
        for (Integer id : newFlowRate.keySet()) {
            for (Instant time : newFlowRate.get(id).keySet()) {
                Double tempValue = 0.0;
                if (flowUpstream.containsKey(id)) {
                    tempValue = flowUpstream.get(id).get(time);
                }
                Double tempNewValue = newFlowRate.get(id).get(time);
                LinkedHashMap<Instant, Double> sumValues = new LinkedHashMap<>();
                sumValues.put(time, tempValue + tempNewValue);
                flowUpstream.put(id, sumValues);
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
    public void buildLink(Double dimension, HashMap<Integer, List<Integer>> subtrees) {
        //allineamento peli liberi
        //setup offset e up
        
    }

    @Override
    public void evaluateFlowRate(Instant currentTime) {
        for (Integer id : this.getUpstreamOutside().getStreamFlowRate().keySet()) {
            routingSetup.evaluateFlowRate(id, currentTime, upstreamOutside, downstreamOutside,
                    linkLength, linkRoughness, linkSlope, crossSectionType);
        }
    }

    @Override
    public Double evaluateMaxDischarge(Instant currentTime) {

        HashMap<Integer, LinkedHashMap<Instant, Double>> flowUpstreamNode = this.getUpstreamOutside().getStreamFlowRate();

        Double maxDischarge = 0.0;
        for (Integer id : flowUpstreamNode.keySet()) {
            if ( flowUpstreamNode.get(id).get(currentTime) >= maxDischarge) {
                maxDischarge = flowUpstreamNode.get(id).get(currentTime);
            }
        }
        return maxDischarge;
    }

    @Override
    public Double evaluateDimension(Double discharge) {

        Double naturalSlope = computeNaturalSlope();
        Double diameter = getDimension(discharge, naturalSlope);

        //diameter = diameterToCommercial(diameter);

        Double minSlope = computeMinSlope(diameter);
        //if (naturalSlope < minSlope && naturalSlope > maxSlope) {
        if (naturalSlope < minSlope) {
            diameter = getDimension(discharge, minSlope);
            //diameter = diameterToCommercial(diameter);
            return diameter;
        }
        else {
            return diameter;
        }
    }

    private Double computeNaturalSlope() {
        Coordinate2D upstream = getUpstreamOutside().getNodeCoordinates();
        return GEOgeometry.computeSlope(upstream.x, upstream.y, getUpstreamOutside().getTerrainElevation(),
                upstream.x, upstream.y, getDownstreamOutside().getTerrainElevation());
    }

    private double computeMinSlope(Double diameter) {

        Double fillCoeff = getUpstreamOutside().getFillCoeff();
        Double fillAngle = crossSectionType.computeFillAngle(fillCoeff);
        Double hydraulicRadius = crossSectionType.computeHydraulicRadious(diameter, fillAngle);

        return GEOconstants.SHEARSTRESS
                / (GEOconstants.WSPECIFICWEIGHT * hydraulicRadius);
    }

    private Double getDimension(Double discharge, Double slope) {
        
        Double fillCoeff = getUpstreamOutside().getFillCoeff();
        Double fillAngle = crossSectionType.computeFillAngle(fillCoeff);

        final double pow1 = 3.0 / 8;
        double numerator = Math.pow((discharge * fillAngle)
                / (linkRoughness * Math.pow(slope, 0.5)), pow1);
        final double pow2 = 5.0 / 8;
        double denominator = Math
                .pow(1 - Math.sin(fillAngle) / fillAngle, pow2);
        final double pow3 = -9.0 / 8;

        return numerator / denominator * Math.pow(10, pow3);
    }
}