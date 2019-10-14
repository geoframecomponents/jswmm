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

public class KinematicWaveOptions implements RoutingSolver {

    @Override
    public RoutedFlow routeFlowRate(Integer id, Instant currentTime, double upstreaFlow, OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness, Double linkSlope, CrossSectionType crossSectionType, double routingStep) {
        throw new NullPointerException("Nothing implemented yet.");
    }

    @Override
    public double adaptTimeDelay(double routingStepSize, double timeDelay) {
        throw new NullPointerException("Nothing implemented yet.");
    }

//
//    private final Long routingStepSize;
//
//    private final Integer referenceTableLength;
//    private final List<ChowTable> relationsTable = new LinkedList<>();
//
//    private final Double iota;
//    private final Double phi;
//
//    private final Double tolerance;
//    private Double lowerBound;
//    private Double upperBound;
//
//    public KinematicWaveOptions(Long routingStepSize, Integer referenceTableLength, Double iota,
//                                     Double phi, Double tolerance) {
//        this.routingStepSize = routingStepSize;
//        this.referenceTableLength = referenceTableLength;
//        this.iota = iota;
//        this.phi = phi;
//        this.tolerance = tolerance;
//        fillTables();
//    }
//
//    public KinematicWaveOptions(Long routingStepSize, Double tolerance) {
//        this.routingStepSize = routingStepSize;
//        this.referenceTableLength = 180;
//        this.tolerance = tolerance;
//        this.iota = 0.6;
//        this.phi = 0.6;
//        fillTables();
//    }
//
//    private void fillTables() {
//        for(int i = 0; i <= referenceTableLength; i++) {
//            Double theta = 2*i*Math.PI/180;
//            if (theta == 0.0) {
//                relationsTable.add(new ChowTable(0.0,0.0));
//            }
//            else {
//                relationsTable.add(new ChowTable((theta-Math.sin(theta))/(2*Math.PI),
//                        Math.pow((theta-Math.sin(theta)), 5.0/3.0) / (2 * Math.PI * Math.pow(theta, 2.0/3.0))));
//            }
//        }
//    }
//
//    @Override
//    public Long getRoutingStepSize() {
//        return routingStepSize;
//    }
//
//    @Override
//    public void evaluateFlowRate(Integer id, Instant currentTime, OutsideSetup upstreamOutside,
//                                 OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
//                                 Double linkSlope, CrossSectionType crossSectionType) {
//
//        Instant nextTime = currentTime.plusSeconds(routingStepSize);
//
//        LinkedHashMap<Instant, Double> upFlowRate = upstreamOutside.getStreamFlowRate().get(id);
//        LinkedHashMap<Instant, Double> downFlowRate = new LinkedHashMap<>();
//        for (Instant time : upFlowRate.keySet()) {
//            downFlowRate.put(time, 0.0);
//        }
//
//        LinkedHashMap<Instant, Double> upWetArea = upstreamOutside.getStreamWetArea().get(id);
//        LinkedHashMap<Instant, Double> downWetArea = new LinkedHashMap<>();
//        for (Instant time : upWetArea.keySet()) {
//            downWetArea.put(time, 0.0);
//        }
//
//        final Double beta = Math.sqrt(linkSlope) / linkRoughness;
//
//        //A1(t+dt)
//        upWetArea.put(nextTime, sectionFactorToArea( upFlowRate.get(nextTime)/beta ));
//
//        final Double constantOne = linkLength / phi * iota / routingStepSize;
//        final Double constantTwo = linkLength / (phi * routingStepSize) * ((1 - iota) * (upWetArea.get(nextTime) - upWetArea.get(currentTime)) -
//                iota * downWetArea.get(currentTime)) + (1 - phi) / phi * (downFlowRate.get(currentTime) - upFlowRate.get(currentTime))-
//                upFlowRate.get(nextTime);
//
//        Double Amax = crossSectionType.getAreaMax();
//        Double Afull = crossSectionType.getAreaFull();
//
//        final Double functionMax = functionValue(Amax, beta, constantOne, constantTwo);
//        final Double functionFull = functionValue(Afull, beta, constantOne, constantTwo);
//
//        Boolean validBounds = setBounds(Afull, Amax, crossSectionType.getAlwaysIncrease(), functionMax, functionFull);
//
//        //A2(t+dt)
//        if (validBounds) {
//            //Newton-Raphson
//            downstreamOutside.setWetArea(id, nextTime,
//                    streamWetArea(downstreamOutside.getStreamWetArea().get(id).get(currentTime), crossSectionType, beta, constantOne, constantTwo));
//        }
//        else {
//            if (functionMax > 0) {
//                downstreamOutside.setWetArea(id, nextTime, 0.0);
//            }
//            else {
//                downstreamOutside.setWetArea(id, nextTime, Afull);
//            }
//        }
//
//        //Q2(t+dt)
//        downstreamOutside.setAreaFlowRate(id, nextTime,
//                evaluateStreamFlowRate(downstreamOutside.getStreamWetArea().get(id).get(nextTime), beta));
//    }
//
//    private Double streamWetArea(Double downstreamWetArea, CrossSectionType crossSectionType, Double beta, Double constantOne, Double constantTwo) {
//
//        if (lowerBound > downstreamWetArea || upperBound < downstreamWetArea) {
//            downstreamWetArea = (lowerBound + upperBound) / 2;
//        }
//
//        Double tmpFunctionValue = functionValue(downstreamWetArea, beta, constantOne, constantTwo);
//        Double tmpDerivateFunctionValue = derivatedFunction(crossSectionType,
//                evaluateTheta(downstreamWetArea, crossSectionType), beta, constantOne);
//
//        return iterativeWetArea(downstreamWetArea, tmpFunctionValue, tmpDerivateFunctionValue, crossSectionType, beta, constantOne, constantTwo);
//    }
//
//    private Double iterativeWetArea(Double area, Double function, Double derivate, CrossSectionType crossSectionType, Double beta, Double constantOne, Double constantTwo) {
//
//        Double deltaArea = Math.abs(upperBound - lowerBound);
//
//        if ( ((area - upperBound) * derivate - function) * ((area - lowerBound) * derivate - function) >= 0 ||
//                Math.abs(2 * function) > Math.abs(deltaArea * derivate) ){
//
//            deltaArea = 0.5 * (upperBound - lowerBound);
//            area = lowerBound + deltaArea;
//        }
//        else {
//            deltaArea = function / derivate;
//            area -= deltaArea;
//        }
//
//        if (Math.abs(deltaArea) < tolerance) {
//            return area;
//        }
//        else {
//            Double newFunctionValue = functionValue(area, beta, constantOne, constantTwo);
//            Double newDerivatedFctValue = derivatedFunction(crossSectionType,
//                    evaluateTheta(area, crossSectionType), beta, constantOne);
//            if (newFunctionValue < 0) {
//                lowerBound = area;
//            }
//            else {
//                upperBound = area;
//            }
//            return iterativeWetArea(area, newFunctionValue, newDerivatedFctValue, crossSectionType, beta, constantOne, constantTwo);
//        }
//    }
//
//    private Double derivatedFunction(CrossSectionType crossSectionType, Double theta, Double beta, Double constantOne) {
//        return beta * crossSectionType.derivatedSectionFactor(theta) + constantOne;
//    }
//
//    private Boolean setBounds(Double Afull, Double Amax, Boolean alwaysIncrease, Double functionMax, Double functionFull) {
//
//        if (functionMax*functionFull < 0) {
//            if (functionMax > functionFull) {
//                lowerBound = Afull;
//                upperBound = Amax;
//            }
//            else {
//                lowerBound = Afull;
//                upperBound = Amax;
//            }
//            return true;
//        }
//        else {
//            if (alwaysIncrease) {
//                lowerBound = 0.0;
//                upperBound = Afull;
//            }
//            else  {
//                lowerBound = 0.0;
//                upperBound = Amax;
//            }
//            return false;
//        }
//    }
//
//    private Double functionValue(Double area, Double beta, Double constantOne, Double constantTwo) {
//        return beta * areaToSectionFactor(area) + constantOne * area + constantTwo;
//    }
//
//    private Double evaluateStreamFlowRate(Double wetArea, Double beta) {
//        return areaToSectionFactor(wetArea) * beta;
//    }
//
//    private Double sectionFactorToArea(Double sectionFactor) {
//
//        if (sectionFactor == 0.0) {
//            return 0.0;
//        }
//        else {
//            int elementCounter = 0;
//            Double lowerSFValue = relationsTable.get(elementCounter).adimensionalSectionFactor;
//            Double upperSFValue = null;
//
//            for (ChowTable element : relationsTable) {
//                if (element.getAdimensionalSectionFactor() < sectionFactor) {
//                    lowerSFValue = element.adimensionalSectionFactor;
//                    elementCounter++;
//                }
//                else {
//                    upperSFValue = element.adimensionalSectionFactor;
//                    break;
//                }
//            }
//            double[] x = {lowerSFValue, upperSFValue};
//            double[] y = {relationsTable.get(elementCounter-1).getAdimensionalArea(),
//                    relationsTable.get(elementCounter).getAdimensionalArea()};
//
//            // return linear interpolation of (x,y) on sectionFactor
//            LinearInterpolator interpolator = new LinearInterpolator();
//            PolynomialSplineFunction psf = interpolator.interpolate(x, y);
//            return psf.value(sectionFactor);
//        }
//    }
//
//    private Double areaToSectionFactor(Double area) {
//
//        if (area == 0.0) {
//            return 0.0;
//        }
//        else {
//            int elementCounter = 0;
//            Double lowerArea = relationsTable.get(elementCounter).adimensionalArea;
//            Double upperArea = null;
//
//            for (ChowTable element : relationsTable) {
//                if (element.getAdimensionalArea() < area) {
//                    lowerArea = element.adimensionalArea;
//                    elementCounter++;
//                }
//                else {
//                    upperArea = element.adimensionalArea;
//                    break;
//                }
//            }
//            double[] x = {lowerArea, upperArea};
//            double[] y = {relationsTable.get(elementCounter-1).getAdimensionalSectionFactor(),
//                    relationsTable.get(elementCounter).getAdimensionalSectionFactor()};
//
//            // return linear interpolation of (x,y) on sectionFactor
//            LinearInterpolator interpolator = new LinearInterpolator();
//            PolynomialSplineFunction psf = interpolator.interpolate(x, y);
//            return psf.value(area);
//        }
//    }
//
//    private Double evaluateTheta(Double area, CrossSectionType crossSectionType) {
//
//        Double adimensionalArea = area / crossSectionType.getAreaFull();
//        Double tempTheta;
//        Double deltaTheta;
//
//        tempTheta = 0.031715 - 12.79384*adimensionalArea + 8.28479*Math.sqrt(adimensionalArea);
//        do {
//            deltaTheta = 2 * Math.PI * adimensionalArea - (tempTheta - Math.sin(tempTheta)) / (1 - Math.cos(tempTheta));
//            tempTheta += deltaTheta;
//        } while (Math.abs(deltaTheta) > 0.0001);
//
//        return tempTheta;
//    }
}
