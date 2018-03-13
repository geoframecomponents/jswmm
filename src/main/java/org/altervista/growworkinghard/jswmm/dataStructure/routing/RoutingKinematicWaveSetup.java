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

package org.altervista.growworkinghard.jswmm.dataStructure.routing;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

class ChowTable {
    Double adimensionalArea;
    Double adimensionalSectionFactor;

    ChowTable(Double adimensionalArea, Double adimensionalSectionFactor) {
        this.adimensionalArea = adimensionalArea;
        this.adimensionalSectionFactor = adimensionalSectionFactor;
    }
}

public class RoutingKinematicWaveSetup implements RoutingSetup {

    private Instant initialTime;
    private Instant totalTime;
    private Long routingStepSize;

    public Instant getInitialTime() {
        return initialTime;
    }

    public Instant getTotalTime() {
        return totalTime;
    }

    public Long getRoutingStepSize() {
        return routingStepSize;
    }

    @Override
    public LinkedHashMap<Instant, Double> getDownstreamFlowRate() {
        return null;
    }

    private Integer referenceTableLength = 180;
    private List<ChowTable> relationsTable = new LinkedList<>();

    private Double iota = 0.6;
    private Double phi = 0.6;

    private Double beta;
    private Double constantOne;
    private Double constantTwo;
    private Double tolerance; //TODO setup!!!

    public RoutingKinematicWaveSetup(Instant initialTime, Instant totalTime, Long routingStepSize, Integer referenceTableLength,
                              Double iota, Double phi, Double tolerance) {
        this.initialTime = initialTime;
        this.totalTime = totalTime;
        this.routingStepSize = routingStepSize;
        this.referenceTableLength = referenceTableLength;
        this.iota = iota;
        this.phi = phi;
        this.tolerance = tolerance;
        fillTables();
    }

    public RoutingKinematicWaveSetup(Instant initialTime, Instant totalTime, Long routingStepSize, Double tolerance) {
        this.initialTime = initialTime;
        this.totalTime = totalTime;
        this.routingStepSize = routingStepSize;
        this.tolerance = tolerance;
        fillTables();
    }

    private void fillTables() {
        for(int i = 0; i <= referenceTableLength; i++) {
            Double theta = i*Math.PI/180;
            relationsTable.add(new ChowTable((theta-Math.sin(theta))/(2*Math.PI),
                    Math.pow((theta-Math.sin(theta)), 5.0/3.0) / (2 * Math.PI * Math.pow(theta, 2.0/3.0))));
        }
    }

    @Override
    public void evaluateFlowRate(Instant currentTime, Long routingStepSize, OutsideSetup upstreamOutside,
                                OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                                CrossSectionType crossSectionType) {

        Instant nextTime = currentTime.plusSeconds(routingStepSize);

        LinkedHashMap<Instant, Double> upFlowRate = upstreamOutside.getStreamFlowRate();
        LinkedHashMap<Instant, Double> downFlowRate = downstreamOutside.getStreamFlowRate();

        LinkedHashMap<Instant, Double> upWetArea = upstreamOutside.getStreamWetArea();
        LinkedHashMap<Instant, Double> downWetArea = downstreamOutside.getStreamWetArea();


        beta = evaluateBeta(linkLength, linkRoughness);

        //A1(t) = readTable(Q1(t)/beta)
        Double tempAdimensionalSF = ( upFlowRate.get(currentTime)/beta ) / crossSectionType.getSectionFactorFull();
        Double tempAdimensionalArea = sectionFactorToArea(tempAdimensionalSF)*crossSectionType.getAreaFull();
        upstreamOutside.setWetArea(currentTime, tempAdimensionalArea);

        //upSF(t+dt)
        Double upSectionFactor = upstreamOutside.getStreamFlowRate().get(nextTime) / beta;

        //A1(t+dt) = readTable(upSF(t+dt))
        tempAdimensionalSF = upSectionFactor / crossSectionType.getSectionFactorFull();
        tempAdimensionalArea = sectionFactorToArea(tempAdimensionalSF)*crossSectionType.getAreaFull();
        upstreamOutside.setWetArea(nextTime, tempAdimensionalArea);

        constantOne = linkLength / phi * iota / routingStepSize;
        constantTwo = linkLength/(phi*routingStepSize) *
                ((1- iota) * (upWetArea.get(nextTime) - upWetArea.get(currentTime)) - iota *
                        downWetArea.get(currentTime)) + (1 - phi)/phi * (downFlowRate.get(currentTime) -
                upFlowRate.get(currentTime)) - upFlowRate.get(nextTime);

        Double tryWetArea = downWetArea.get(currentTime);
        Double newWetArea = evaluateNewWetArea(tryWetArea, crossSectionType);

        downstreamOutside.setWetArea(nextTime, newWetArea);
        downstreamOutside.setFlowRate(nextTime, evaluateStreamFlowRate(newWetArea));
    }

    public Double evaluateStreamFlowRate(Double wetArea) {
        return areaToSectionFactor(wetArea) * beta;
    }

    private Double evaluateNewWetArea(Double tryWetArea, CrossSectionType crossSectionType) {

        Double theta = evaluateTheta(tryWetArea, crossSectionType);
        Double depth = evaluateDepth(theta, crossSectionType);

        //dwSF2(t+dt) = readTable(A2(t))
        Double tempAdimensionalArea = tryWetArea / crossSectionType.getAreaFull();
        Double tempAdimensionalSF = areaToSectionFactor(tempAdimensionalArea);
        Double downSectionFactor = tempAdimensionalSF * crossSectionType.getSectionFactorFull();

        Integer i = (int)(depth/crossSectionType.getDepthFull()) * (referenceTableLength - 1);
        Double[] sectionFactorTable = {relationsTable.get(i+1).adimensionalSectionFactor,
                relationsTable.get(i+1).adimensionalSectionFactor};

        Double downSectionFactorDerivate = (sectionFactorTable[0] - sectionFactorTable[1]) * (referenceTableLength - 1) *
                (crossSectionType.getSectionFactorFull() / crossSectionType.getAreaFull());

        Double numerator = beta * downSectionFactor + tryWetArea * constantOne + constantTwo;
        Double denominator = beta * downSectionFactorDerivate + constantOne;

        if( Math.abs(numerator/denominator) < tolerance ) {
            return tryWetArea + numerator/denominator;
        }
        else {
            return evaluateNewWetArea(tryWetArea + numerator/denominator, crossSectionType);
        }
    }

    private Double sectionFactorToArea(Double sectionFactor) {

        int elementCounter = 0;
        Double lowerSFValue = relationsTable.get(elementCounter).adimensionalSectionFactor;
        Double upperSFValue = null;

        for (ChowTable element : relationsTable) {
            while (element.adimensionalSectionFactor < sectionFactor) {
                lowerSFValue = element.adimensionalSectionFactor;
                elementCounter++;
            }
            upperSFValue = element.adimensionalSectionFactor;
        }
        double[] x = {lowerSFValue, upperSFValue};
        double[] y = {elementCounter-1.0, (double)elementCounter};

        // return linear interpolation of (x,y) on sectionFactor
        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction psf = interpolator.interpolate(x, y);
        return psf.value(sectionFactor);
    }

    private Double areaToSectionFactor(Double area) {

        int elementCounter = 0;
        Double lowerSFValue = relationsTable.get(elementCounter).adimensionalArea;
        Double upperSFValue = null;

        for (ChowTable element : relationsTable) {
            while (element.adimensionalArea < area) {
                lowerSFValue = element.adimensionalArea;
                elementCounter++;
            }
            upperSFValue = element.adimensionalArea;
        }
        double[] x = {lowerSFValue, upperSFValue};
        double[] y = {elementCounter-1.0, (double)elementCounter};

        // return linear interpolation of (x,y) on sectionFactor
        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction psf = interpolator.interpolate(x, y);
        return psf.value(area);
    }

    private Double evaluateDepth(Double theta, CrossSectionType crossSectionType) {
        return crossSectionType.getDepthFull() * (1 - Math.cos(theta/2)) / 2;
    }

    private Double evaluateBeta(Double linkSlope, Double linkRoughness) {
        return Math.sqrt(linkSlope)/ linkRoughness;
    }

    private Double evaluateTheta(Double area, CrossSectionType crossSectionType) {

        Double adimensionalArea = area/crossSectionType.getAreaFull();
        Double tempTheta;
        Double deltaTheta;

        do {
            tempTheta = 0.031715 - 12.79384*adimensionalArea + 8.28479*Math.sqrt(adimensionalArea);
            deltaTheta = 2 * Math.PI * adimensionalArea - (tempTheta - Math.sin(tempTheta)) / (1 - Math.cos(tempTheta));
            tempTheta += deltaTheta;
        } while (deltaTheta > 0.0001);

        return tempTheta;
    }
}
