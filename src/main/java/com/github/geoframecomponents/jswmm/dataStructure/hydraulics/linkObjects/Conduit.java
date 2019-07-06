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

import it.blogspot.geoframe.utils.GEOconstants;
import it.blogspot.geoframe.utils.GEOgeometry;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutedFlow;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutingSetup;
import org.geotools.graph.util.geom.Coordinate2D;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Conduit extends AbstractLink {

    CrossSectionType crossSectionType;

    Double linkLength;
    Double linkRoughness;
    Double linkSlope;

    public Conduit(RoutingSetup routingSetup, CrossSectionType crossSectionType, OutsideSetup upstreamOutside,
                   OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness) {
        this.routingSetup = routingSetup;
        this.crossSectionType = crossSectionType;
        this.upstreamOutside = upstreamOutside;
        this.downstreamOutside = downstreamOutside;
        this.linkLength = linkLength;
        this.linkRoughness = linkRoughness;
    }

    public CrossSectionType getCrossSectionType() {
        return crossSectionType;
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
    public void setInitialUpFlowRate(Integer id, Instant time, Double flowRate) {
        upstreamOutside.setFlowRate(id, time, flowRate);
    }

    @Override
    public void setInitialUpWetArea(Integer id, Instant time, double flowRate) {
        upstreamOutside.setWetArea(id, time, flowRate);
    }

    @Override
    public void evaluateFlowRate(Instant currentTime) {

        HashMap<Integer, LinkedHashMap<Instant, Double>> currentFlow = getUpstreamOutside().getStreamFlowRate();
        for (Integer id : currentFlow.keySet()) {

            HashMap<Integer, LinkedHashMap<Instant, Double>> flow = upstreamOutside.getStreamFlowRate();
            LinkedHashMap<Instant, Double> upstreamFlow = flow.get(id);

            RoutedFlow routedFlow = routingSetup.routeFlowRate(id, currentTime, upstreamFlow.get(currentTime),
                    downstreamOutside, linkLength, linkRoughness, linkSlope, crossSectionType);

            downstreamOutside.setFlowRate(id, routedFlow.getTime(), routedFlow.getValue());
        }

        //System.out.println("END evaluateFlowRate");
    }

    @Override
    public Double evaluateMaxDischarge(Instant currentTime, Double maxDischarge) {

        HashMap<Integer, LinkedHashMap<Instant, Double>> flowUpstreamNode = this.getUpstreamOutside().getStreamFlowRate();

        for (Integer id : flowUpstreamNode.keySet()) {

            double currentFlow = flowUpstreamNode.get(id).get(currentTime);
            if ( currentFlow >= maxDischarge) {
                maxDischarge = currentFlow;
            }
        }

        return maxDischarge;
    }

    /**
     * Method to evaluate commercial diameter from maximum discharge, it takes into account the slope
     * TODO generalize to different crossSections
     * @param discharge
     * @param pipeCompany
     */
    @Override
    public void evaluateDimension(Double discharge, CommercialPipeSize pipeCompany) {

        linkSlope = computeNaturalSlope();

        // TODO: the first diameter has to be bigger or equal to the biggest upstream pipe

        double diameter = getDimension(discharge, linkSlope);

        double[] diameters = pipeCompany.getCommercialDiameter(diameter); //diameters in meters
        double thicknessPipe = diameters[1] - diameters[0];

        double deltaSlope = 0.0;
        Double minSlope = computeMinSlope(diameters[0]);
        //if (naturalSlope < minSlope && naturalSlope > maxSlope) {
        if (linkSlope < minSlope) {
            diameter = getDimension(discharge, minSlope);
            diameters = pipeCompany.getCommercialDiameter(diameter); //diameters in meters
            deltaSlope = minSlope - linkSlope;
            linkSlope = minSlope;
        }
        crossSectionType.setDimensions(diameters[0], diameters[1]);
        double fillAngleMax = evaluateFillAngle(diameters[0], linkSlope, discharge);
        double maxQDepth = diameters[0] / 2 * ( 1 + Math.cos(Math.PI - fillAngleMax / 2) );

        double upstreamExcavation = GEOconstants.MINIMUMEXCAVATION + diameters[1];
        double downstreamExcavation = upstreamExcavation;
        if (deltaSlope > 0.0) {
            downstreamExcavation += linkLength*deltaSlope;
        }
        upstreamOutside.setHeights(upstreamExcavation, 0.0);
        downstreamOutside.setHeights(downstreamExcavation);

        double waterDepth = GEOconstants.MINIMUMEXCAVATION + ( thicknessPipe + diameters[0] - maxQDepth );
        getUpstreamOutside().setWaterDepth(waterDepth + linkLength*deltaSlope);
        getDownstreamOutside().setWaterDepth(waterDepth);

        System.out.println("D " + diameters[0]);
    }

    private double evaluateFillAngle(double innerSize, double slope, double discharge) {
        final double TWO_THIRTEENOVERTHREE = Math.pow(2, 13/3);
        final double EIGHTOVERTHREE = 8/3;
        double initFillAngle = 2 * Math.acos((1 - 2 * getUpstreamOutside().getFillCoeff()));

        double b = discharge / (linkRoughness * Math.sqrt(slope)); // conversione di discharge m3 to l e slope m to cm
        double known = (b * TWO_THIRTEENOVERTHREE) / Math.pow(innerSize, EIGHTOVERTHREE); // innersize m2cm

        double exponent = 2/3;
        double fillAngle = fillAngleBisection(initFillAngle, known, exponent);

        if (fillAngle > initFillAngle)
            throw new IllegalArgumentException("New angle must be smaller than old angle");

        return  fillAngle;
    }

    private double fillAngleBisection(double fillAngle, double known, double exponent) {
        double delta = fillAngle / 10;
        double upperBound = gsm(known, fillAngle, exponent);

        double fillAngle_i = 0;
        double lowerBound;
        for (int i = 1; i <= 10; i++) {
            fillAngle_i = fillAngle - (i * delta);
            lowerBound = gsm(known, fillAngle_i, exponent);

            if (upperBound * lowerBound < 0) break; // root of function is between upperBound, lowerBound

        }
        // add check on bracketing not succeeding
        if (fillAngle_i == 0) {
            String msg = "fillAngle_i cannot be 0";
            throw new NullPointerException(msg);
        }

        double accuracy = 0.005;
        return bisection(fillAngle_i, fillAngle_i + delta, known, accuracy, exponent);
    }

    private double bisection(double fillAngle, double fillAnglePlusDelta, double known, double accuracy, double exponent) {

        double function = gsm(known, fillAngle, exponent);
        double function_mid = gsm(known, fillAnglePlusDelta, exponent);

        if (function * function_mid >= 0) {
            String msg = "Both functions are positive. Non bisection possible";
            throw new IllegalArgumentException(msg);
        }

        double deltaAngle;
        double rtb;
        if (function < 0) {
            deltaAngle = fillAnglePlusDelta - fillAngle;
            rtb = fillAngle;
        } else {
            deltaAngle = fillAngle - fillAnglePlusDelta;
            rtb = fillAnglePlusDelta;
        }

        int MAXLOOP = 40;
        for (int i = 0; i < MAXLOOP; i++) {
            double fillAngleMid = rtb + (deltaAngle *= 0.5);
            function_mid = gsm(known, fillAngleMid, exponent);

            if (function_mid <= 0) {
                rtb = fillAngleMid;
            }

            if (Math.abs(deltaAngle) < accuracy || function_mid == 0) {
                return rtb;
            }
        }
        throw new UnsupportedOperationException("Too many bisections");
    }

    private double gsm(double known, double fillAngle, double exponent) {
        if (fillAngle <= 0) {
            fillAngle = 0.01; //minimum filling for channels
            System.out.println("Negative fill angle: " + fillAngle + ". Minimum assigned.");
        }
        return (known - (fillAngle - Math.sin(fillAngle)) * Math.pow((1 - Math.sin(fillAngle)/fillAngle), exponent));
    }

    private Double computeNaturalSlope() {
        Coordinate2D upstream = getUpstreamOutside().getNodeCoordinates();
        if ( upstreamOutside.getTerrainElevation().equals(downstreamOutside.getTerrainElevation()) ) {
            return 0.001;//TODO is there a better method?
        }
        else {
            return GEOgeometry.computeSlope(upstream.x, upstream.y, upstreamOutside.getTerrainElevation(),
                    upstream.x, upstream.y, downstreamOutside.getTerrainElevation());
        }
    }

    private double computeMinSlope(Double diameter) {

        Double fillCoeff = getUpstreamOutside().getFillCoeff();
        Double fillAngle = crossSectionType.computeFillAngle(fillCoeff);
        Double hydraulicRadius = crossSectionType.computeHydraulicRadious(diameter, fillAngle);

        return GEOconstants.SHEARSTRESS
                / (GEOconstants.WSPECIFICWEIGHT * hydraulicRadius);
    }

    /**
     * Evaluate the dimension for a given discharge and slope, just for the Circular
     * TODO extend to all crossSectionTypes
     * @param discharge
     * @param slope
     * @return
     */
    private Double getDimension(Double discharge, Double slope) {
        
        Double fillCoeff = getUpstreamOutside().getFillCoeff();
        Double fillAngle = crossSectionType.computeFillAngle(fillCoeff);
        if( fillAngle == 0.0 ) {
            fillAngle = 0.001;
        }

        System.out.println("fillCoeff " + fillCoeff);
        System.out.println("fillAngle " + fillAngle);
        System.out.println("slope " + slope);

        final double pow1 = 3.0 / 8;
        double coeff = Math.pow(2, 13.0/3);
        double numerator = (coeff * discharge);
        double denominator = (fillAngle - Math.sin(fillAngle)) * linkRoughness *
                Math.pow(slope, 0.5) * Math.pow( (1 - Math.sin(fillAngle) / fillAngle), 2.0/3 );

        return Math.pow( numerator / denominator, pow1 );
    }
}
