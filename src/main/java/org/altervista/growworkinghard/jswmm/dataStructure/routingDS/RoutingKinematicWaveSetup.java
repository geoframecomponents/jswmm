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

public class RoutingKinematicWaveSetup implements RoutingSetup {

    private final Long routingStepSize;
    private SWMMroutingTools routingTools;

    private final Double iota;
    private final Double phi;

    private final Double tolerance;
    private Double lowerBound;
    private Double upperBound;
    private Double Afull;

    public RoutingKinematicWaveSetup(Long routingStepSize, Integer referenceTableLength, Double iota,
                                     Double phi, Double tolerance) {
        this.routingStepSize = routingStepSize;
        this.iota = iota;
        this.phi = phi;
        this.tolerance = tolerance;

        this.routingTools = new SWMMroutingTools(referenceTableLength);
    }

    public RoutingKinematicWaveSetup(Long routingStepSize, Double tolerance) {
        this(routingStepSize, 180, 0.6, 0.6, tolerance);
    }

    @Override
    public Long getRoutingStepSize() {
        return routingStepSize;
    }

    @Override
    public RoutedFlow routeFlowRate(Integer id, Instant currentTime, OutsideSetup upstreamOutside,
                              OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                              Double linkSlope, CrossSectionType crossSectionType) {

        Instant nextTime = currentTime.plusSeconds(routingStepSize);

        Double dischargeFull = crossSectionType.getDischargeFull(linkRoughness, linkSlope);
        Double Afull = crossSectionType.getAreaFull();
        //System.out.println("Afull " + Afull);


        LinkedHashMap<Instant, Double> upFlowRate = upstreamOutside.getStreamFlowRate().get(id); //m^3/s
        LinkedHashMap<Instant, Double> downFlowRate = new LinkedHashMap<>();
        for (Instant time : upFlowRate.keySet()) {
            downFlowRate.put(time, 0.0);
        }

        LinkedHashMap<Instant, Double> upWetArea = upstreamOutside.getStreamWetArea().get(id); //m^2
        LinkedHashMap<Instant, Double> downWetArea = new LinkedHashMap<>();
        for (Instant time : upWetArea.keySet()) {
            downWetArea.put(time, 0.0);
        }

        final Double beta = (Math.sqrt(linkSlope) * linkRoughness) / dischargeFull; //should be Math.sqrt(linkSlope) / linkRoughness but the Manning coefficient is 1 / Gs

        //System.out.println("time) " + currentTime);
        //System.out.println("upFlowRate.get(nextTime) " + upFlowRate.get(nextTime));
        //System.out.println("beta " + beta);
        //A1(t+dt)
        upstreamOutside.setStreamWetArea( id, nextTime, routingTools.sectionFactorToArea(upFlowRate.get(nextTime)/dischargeFull) / beta );
        //System.out.println(upstreamOutside.getStreamWetArea().get(id).get(nextTime));


        final Double constantOne = ( linkLength * iota * Afull ) / ( routingStepSize * phi * dischargeFull );

        //System.out.println("constantOne " + constantOne);

        double a1 = upWetArea.get(currentTime) / Afull;
        double a2 = downWetArea.get(currentTime) / Afull;
        double ain = upWetArea.get(nextTime) / Afull;
        double aout = a2;

        double q2 = downFlowRate.get(currentTime) / dischargeFull;
        double q1 = upFlowRate.get(currentTime) / dischargeFull;
        double qin = upFlowRate.get(nextTime) / dischargeFull;

        //System.out.println("a1 " + a1 + "a2 " + a2 + "ain " + ain);
        //System.out.println("q1 " + q1 + "q2 " + q2 + "qin " + qin);

        final Double constantTwo = ((1 - iota) * (ain - a1) - iota * a2) + ((1 - phi) / phi) * (q2 -q1) - qin;

        //System.out.print(currentTime + " ");
        //System.out.println("constantTwo " + constantTwo);

        if (qin >= 1.0) {
            ain = 1.0;
        }
        else {
            ain = qin/( beta * Afull );
        }

        if (qin <= 0.001) {
            aout = 0.0;
        }
        else {
            upperBound = 1.0;
            final Double functionFull = functionValue(upperBound, beta, constantOne, constantTwo);
            //System.out.println("functionFull" + functionFull);
            double upperFct = functionFull;

            Double Amax = crossSectionType.getAreaMax();
            //System.out.println("Amax " + Amax);

            lowerBound = Amax/Afull;
            final Double functionMax = functionValue(lowerBound, beta, constantOne, constantTwo);
            //System.out.println("functionMax" + functionMax);

            double lowerFct;
            if (lowerBound < upperBound) {
                lowerFct = functionMax;
            }
            else {
                lowerFct = functionFull;
            }

            if (upperFct * lowerFct > 0) {
                upperBound = lowerBound;
                upperFct = lowerFct;
                lowerBound = 0.0;
                lowerFct = constantTwo;
            }

            if (upperFct * lowerFct <= 0) {
                if (aout < lowerBound || aout > upperBound) {
                    aout = 0.5 * (upperBound + lowerBound);
                }

                if (lowerFct > upperFct) {
                    double temp = lowerBound;
                    lowerBound = upperBound;
                    upperBound = temp;
                }

                Double tmpFunctionValue = functionValue(aout, beta, constantOne, constantTwo);
                Double tmpDerivateFunctionValue = derivatedFunction(crossSectionType,
                        routingTools.evaluateTheta(aout, crossSectionType), beta, constantOne);

                //System.out.println("PREITERATION");

                aout = iterativeWetArea(aout, tmpFunctionValue, tmpDerivateFunctionValue, crossSectionType,
                        beta, constantOne, constantTwo);

                //System.out.println("POSTITERATION");

            }
            else if (lowerFct < 0) {
                if (qin > 1.0) {
                    aout = ain;
                }
                else {
                    aout = 1.0;
                }
            }
            else {
                aout = 0.0;
            }
        }

        //Q2(t+dt)
        return new RoutedFlow(nextTime, dischargeFull * evaluateStreamFlowRate(aout * Afull, beta));
    }

       /*for (Map.Entry<Integer, LinkedHashMap<Instant, Double>> entry : downstreamOutside.getStreamFlowRate().entrySet()) {
            //System.out.println("ID rain" + entry.getKey());
            for (Instant time : entry.getValue().keySet()) {
                //System.out.println("Instant rain" + time);
                System.out.print(entry.getValue().get(time));
            }
        }*/

    private Double iterativeWetArea(Double area, Double function, Double derivate, CrossSectionType crossSectionType,
                                    Double beta, Double constantOne, Double constantTwo) {

        double deltaArea;
        if ( ((area - upperBound) * derivate - function) * ((area - lowerBound) * derivate - function) >= 0
                || Math.abs(2 * function) > Math.abs( (upperBound - lowerBound) * derivate) ){

            deltaArea = 0.5 * (upperBound - lowerBound);
            area = lowerBound + deltaArea;
            //TODO if (lowerBound == area) break;
        }
        else {
            deltaArea = function / derivate;
            area -= deltaArea;
        }

        System.out.println("upperBound " + upperBound);
        System.out.println("lowerBound " + lowerBound);
        System.out.println("deltaArea " + deltaArea);

        if (Math.abs(deltaArea) < tolerance) {
            return area;
        }
        else {

            //System.out.println("iteration");

            Double newFunctionValue = functionValue(area, beta, constantOne, constantTwo);

            //System.out.println("newFCTvalue" + newFunctionValue);

            Double newDerivatedFctValue = derivatedFunction(crossSectionType,
                    routingTools.evaluateTheta(area, crossSectionType), beta, constantOne);

            //System.out.println("newDerivatedFctValue" + newDerivatedFctValue);

            if (newFunctionValue < 0) {
                lowerBound = area;
            }
            else {
                upperBound = area;
            }
            return iterativeWetArea(area, newFunctionValue, newDerivatedFctValue, crossSectionType, beta, constantOne, constantTwo);
        }
    }

    private Double derivatedFunction(CrossSectionType crossSectionType, Double theta, Double beta, Double constantOne) {
        Afull = crossSectionType.getAreaFull();
        return beta * crossSectionType.derivatedSectionFactor(theta) + constantOne;
    }

    private Double functionValue(Double area, Double beta, Double constantOne, Double constantTwo) {
        return beta * routingTools.areaToSectionFactor(area) + constantOne * area + constantTwo;
    }

    private Double evaluateStreamFlowRate(Double wetArea, Double beta) {
        return routingTools.areaToSectionFactor(wetArea) * beta;
    }
}