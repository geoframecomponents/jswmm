/*
 * JSWMM: Reimplementation of EPA SWMM in Java
 * Copyright (C) 2019 Daniele Dalla Torre (ftt01)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects;

import com.github.geoframecomponents.jswmm.dataStructure.Coordinates;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.Circular;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutingSolver;
import it.blogspot.geoframe.utils.GEOconstants;
import it.blogspot.geoframe.utils.GEOgeometry;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutedFlow;
import org.altervista.growworkinghard.jswmm.inpparser.objects.ConduitINP;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Conduit extends AbstractLink {

    CrossSectionType crossSectionType;

    Double linkLength;
    Double linkRoughness;
    Double linkSlope;

    public Conduit(String linkName, Unitable units, Datetimeable dateTime, RoutingSolver routingSolver,
                   CrossSectionType crossSectionType, OutsideSetup upstreamOutside, OutsideSetup downstreamOutside,
                   Double linkLength, Double linkRoughness, boolean reportOptions) {

        this(linkName, 1, units, dateTime, routingSolver, crossSectionType, upstreamOutside,
                downstreamOutside,linkLength, linkRoughness, reportOptions);
    }

    public Conduit(String linkName, int curveId, Unitable units, Datetimeable dateTime, RoutingSolver routingSolver,
                   CrossSectionType crossSectionType, OutsideSetup upstreamOutside, OutsideSetup downstreamOutside,
                   Double linkLength, Double linkRoughness, boolean reportOptions) {
        super(linkName);

        this.setLinksUnits(units);
        this.setLinksTime(dateTime);

        this.routingSolver = routingSolver;

        this.crossSectionType = crossSectionType;
        this.upstreamOutside = upstreamOutside;
        this.downstreamOutside = downstreamOutside;
        this.linkLength = linkLength;
        this.linkRoughness = linkRoughness;

        Instant current;
        for (current = dateTime.getDateTime(AvailableDateTypes.startDate);
             current.isBefore( dateTime.getDateTime(AvailableDateTypes.endDate) );
             current = current.plusSeconds( dateTime.getDateTime(AvailableDateTypes.stepSize)) ) {

            upstreamOutside.setFlowRate(curveId, current, 0.01);
        }
    }

    public Conduit(String name, int numberOfCurves, Datetimeable dateTime, Unitable units,
                   RoutingSolver routingSolver, String INPfile) throws ConfigurationException {

        super(name);
        interfaceINP = new ConduitINP(INPfile);

        this.setLinksUnits(units);
        this.setLinksTime(dateTime);

        this.routingSolver = routingSolver;

        String type = ((ConduitINP) interfaceINP).linkType(INPfile, name);
        double dimension = Double.parseDouble( ((ConduitINP) interfaceINP).linkDimension(INPfile, name) );
        this.setXsecProperties(type, dimension);

        this.setOutside(INPfile, "up");
        this.setOutside(INPfile, "down");

        this.linkLength = Double.parseDouble( ((ConduitINP) interfaceINP).linkLength(INPfile, name) );
        this.linkRoughness = Double.parseDouble( ((ConduitINP) interfaceINP).linkRoughness(INPfile, name) );
        this.linkSlope = Double.parseDouble( ((ConduitINP) interfaceINP).linksMinSlope(INPfile) );

        for (int curveId=1; curveId<=numberOfCurves; curveId++) {
            Instant current;
            for (current = dateTime.getDateTime(AvailableDateTypes.startDate);
                 current.isBefore( dateTime.getDateTime(AvailableDateTypes.endDate) );
                 current = current.plusSeconds( dateTime.getDateTime(AvailableDateTypes.stepSize)) ) {

                upstreamOutside.setFlowRate(curveId, current, 0.0001);
            }
        }
    }

    private void setXsecProperties(String type, double dimension) {
        switch (type) {
            case "CIRCULAR":
                this.crossSectionType = new Circular(dimension);
                break;
            default:
                throw new InvalidParameterException("Not an implemented cross section type.");
        }
    }

    private void setOutside(String INPfile, String upORdown) {

        String nodeName;
        double offset;
        double fillCoeff = 0.9;
        double x;
        double y;
        double terrainElev;

        switch (upORdown) {
            case "up":
                nodeName = ((ConduitINP) interfaceINP).nodeLinked(INPfile, name, "up");
                offset = Double.parseDouble(((ConduitINP) interfaceINP).offset(INPfile, name, "up"));
                //fillCoeff = Double.parseDouble( ((ConduitINP) interfaceINP).fillCoeff(name, INPfile) );
                x = Double.parseDouble(((ConduitINP) interfaceINP).nodeCoord(INPfile, name, "x", "up"));
                y = Double.parseDouble(((ConduitINP) interfaceINP).nodeCoord(INPfile, name, "y", "up"));
                terrainElev = Double.parseDouble(((ConduitINP) interfaceINP).nodeCoord(INPfile, name, "z", "up"));
                this.upstreamOutside = new OutsideSetup(nodeName, offset, fillCoeff, x, y, terrainElev);
                break;
            case "down":
                nodeName = ((ConduitINP) interfaceINP).nodeLinked(INPfile, name, "down");
                offset = Double.parseDouble(((ConduitINP) interfaceINP).offset(INPfile, name, "down"));
                //fillCoeff = Double.parseDouble( ((ConduitINP) interfaceINP).fillCoeff(name, INPfile) );
                x = Double.parseDouble(((ConduitINP) interfaceINP).nodeCoord(INPfile, name, "x", "down"));
                y = Double.parseDouble(((ConduitINP) interfaceINP).nodeCoord(INPfile, name, "y", "down"));
                terrainElev = Double.parseDouble(((ConduitINP) interfaceINP).nodeCoord(INPfile, name, "z", "down"));
                this.downstreamOutside = new OutsideSetup(nodeName, offset, fillCoeff, x, y, terrainElev);
                break;
            default:
                throw new InvalidParameterException("Not defined conduit parameters!");
        }
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
    public void evaluateFlowRate() {

        Instant startTime = getLinksTime().getDateTime(AvailableDateTypes.startDate);
        Instant totalTime = getLinksTime().getDateTime(AvailableDateTypes.endDate);
        long routingStepSize = getLinksTime().getDateTime(AvailableDateTypes.stepSize);

        for (Instant currentTime = startTime; currentTime.isBefore(totalTime);
             currentTime = currentTime.plusSeconds(routingStepSize)) {

            HashMap<Integer, LinkedHashMap<Instant, Double>> upstreamFlow = getUpstreamOutside().getStreamFlowRate();
            for (Integer id : upstreamFlow.keySet()) {

                RoutedFlow routedFlow = routingSolver.routeFlowRate(id, currentTime, upstreamFlow,
                        downstreamOutside, linkLength, linkRoughness, linkSlope, crossSectionType, routingStepSize);

                downstreamOutside.setFlowRate(id, routedFlow.getTime(), routedFlow.getValue());

                // to avoid NullPointerExceptions -> currentTime must be have a value
                HashMap<Integer, LinkedHashMap<Instant, Double>> downFlow = downstreamOutside.getStreamFlowRate();
                if (!downFlow.get(id).containsKey(currentTime)) {
                    downstreamOutside.setFlowRate(id, currentTime, 0.0);
                }
            }
        }
    }

    private RoutedFlow interpolate(Instant current, double valueUp, double valueDown, Instant timeUp, Instant timeDown) {
        double slope = (valueUp - valueDown) / (timeUp.getEpochSecond() - timeDown.getEpochSecond());
        RoutedFlow tmp = new RoutedFlow(current, valueDown + valueDown * slope);
        return tmp;
    }

    @Override
    public double evaluateMaxDischarge() {

        Instant currentTime = getLinksTime().getDateTime(AvailableDateTypes.startDate);
        Instant totalTime = getLinksTime().getDateTime(AvailableDateTypes.endDate);

        double maxDischarge = 0.0;
        while (currentTime.isBefore(totalTime)) {

            HashMap<Integer, LinkedHashMap<Instant, Double>> flowUpstreamNode = this.getUpstreamOutside().getStreamFlowRate();

            for (Integer id : flowUpstreamNode.keySet()) {
                double currentFlow = flowUpstreamNode.get(id).get(currentTime);
                if ( currentFlow >= maxDischarge) {
                    maxDischarge = currentFlow;
                }
            }

            currentTime = currentTime.plusSeconds(getLinksTime().getDateTime(AvailableDateTypes.stepSize));
        }
        return maxDischarge;
    }

    /**
     * Method to evaluate commercial diameter from maximum discharge, it takes into account the slope
     * @param discharge discharge at conduit upside [m^3/s]
     * @param pipeCompany commercial pipe company defined by user
     */
    @Override
    public void evaluateDimension(Double discharge, CommercialPipeSize pipeCompany) {

        linkSlope = computeNaturalSlope();

        double diameter = getDimension(discharge, linkSlope);

        double[] diameters = pipeCompany.getCommercialDiameter(diameter); //diameters in meters
        double thicknessPipe = diameters[1] - diameters[0];

        double deltaSlope = 0.0;
        Double minSlope = computeMinSlope(diameters[0]);
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
        Coordinates upstream = getUpstreamOutside().getNodeCoordinates();
        Coordinates downstream = getDownstreamOutside().getNodeCoordinates();
        if ( Math.abs ( upstreamOutside.getTerrainElevation()
                - downstreamOutside.getTerrainElevation() ) < 0.0001 ) {
            return 0.01;//TODO is there a better method?
        }
        else {
            Double slopeFromData = GEOgeometry.computeSlope(upstream.x, upstream.y, upstreamOutside.getTerrainElevation(),
                    downstream.x, downstream.y, downstreamOutside.getTerrainElevation());
            if ( slopeFromData < 0.001 ) {
                return 0.01;
            }
            else {
                return slopeFromData;
            }
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
     * @param discharge discharge at conduit upside
     * @param slope slope to design the pipe
     * @return commercial dimension [m]
     */
    private Double getDimension(Double discharge, Double slope) {
        
        Double fillCoeff = getUpstreamOutside().getFillCoeff();
        Double fillAngle = crossSectionType.computeFillAngle(fillCoeff);
        if( fillAngle == 0.0 ) {
            fillAngle = 0.001;
        }

        final double pow1 = 3.0 / 8;
        double coeff = Math.pow(2, 13.0/3);
        double numerator = (coeff * discharge);
        double denominator = (fillAngle - Math.sin(fillAngle)) * linkRoughness *
                Math.pow(slope, 0.5) * Math.pow( (1 - Math.sin(fillAngle) / fillAngle), 2.0/3 );

        return Math.pow( numerator / denominator, pow1 );
    }
}