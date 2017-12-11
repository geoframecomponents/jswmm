package org.altervista.growworkinghard.jswmm.dataStructure.routing;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

class ChowTable {
    Double adimensionalArea;
    Double adimensionalSectionFactor;

    public ChowTable(Double adimensionalArea, Double adimensionalSectionFactor) {
        this.adimensionalArea = adimensionalArea;
        this.adimensionalSectionFactor = adimensionalSectionFactor;
    }
}

public class RoutingSteadySetup implements RoutingSetup {

    Double upSectionFactor;
    Double downSectionFactor;
    Double downSectionFactorDerivate;

    static final Integer referenceTableLength = 180;
    static final List<ChowTable> relationsTable = null;

    Double iota = 0.6;
    Double phi = 0.6;
    Double beta;
    Double constantOne;
    Double constantTwo;
    Double tolerance; //TODO setup!!!

    @Override
    public void evaluateWetArea(Instant currentTime, long routingStepSize, OutsideSetup upstreamOutside,
                                OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                                CrossSectionType crossSectionType) {

        Instant nextTime = currentTime.plus(routingStepSize, (TemporalUnit) SECONDS);

        LinkedHashMap<Instant, Double> upFlowRate = upstreamOutside.getFlowRate();
        LinkedHashMap<Instant, Double> downFlowRate = upstreamOutside.getFlowRate();

        LinkedHashMap<Instant, Double> upWetArea = upstreamOutside.getWetArea();
        LinkedHashMap<Instant, Double> downWetArea = upstreamOutside.getWetArea();


        beta = evaluateBeta(linkLength, linkRoughness);

        //A1(t) = readTable(Q1(t)/beta)
        upstreamOutside.setWetArea(currentTime, sectionFactorToArea(upFlowRate.get(currentTime)/beta));

        //upSF(t+dt)
        upSectionFactor =  upstreamOutside.getFlowRate().get(nextTime)/beta;

        //A1(t+dt) = readTable(upSF(t+dt))
        upstreamOutside.setWetArea(nextTime, sectionFactorToArea(upSectionFactor));

        constantOne = linkLength / phi * iota / routingStepSize;
        constantTwo = linkLength/(phi*routingStepSize) *
                ((1- iota) * (upWetArea.get(nextTime) - upWetArea.get(currentTime)) - iota *
                        downWetArea.get(currentTime)) + (1 - phi)/phi * (downFlowRate.get(currentTime) -
                            upFlowRate.get(currentTime)) - upFlowRate.get(nextTime);

        Double tryWetArea = downWetArea.get(currentTime);
        downstreamOutside.setWetArea(nextTime, evaluateNewWetArea(tryWetArea, crossSectionType));
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

    @Override
    public void fillTables() {
        for(int i = 0; i<=referenceTableLength; i++) {
            Double theta = i*Math.PI/180;
            relationsTable.add(new ChowTable((theta-Math.sin(theta))/(2*Math.PI),
                    Math.pow((theta-Math.sin(theta)), 5.0/3.0) / (2 * Math.PI * Math.pow(theta, 2.0/3.0))));
        }
    }

    private Double evaluateNewWetArea(Double tryWetArea, CrossSectionType crossSectionType) {

        Double theta = evaluateTheta(tryWetArea, crossSectionType);
        Double depth = evaluateDepth(theta, crossSectionType);

        //dwSF2(t+dt) = readTable(A2(t))
        downSectionFactor = areaToSectionFactor(tryWetArea);

        Integer i = (int)(depth/crossSectionType.getDepthFull()) * (referenceTableLength - 1);
        Double[] sectionFactorTable = {relationsTable.get(i+1).adimensionalSectionFactor,
                relationsTable.get(i+1).adimensionalSectionFactor};

        downSectionFactorDerivate = (sectionFactorTable[0] - sectionFactorTable[1]) * (referenceTableLength - 1) *
                (crossSectionType.getSectionFactorFull()/crossSectionType.getAreaFull());

        Double numerator = beta * downSectionFactor + tryWetArea * constantOne + constantTwo;
        Double denominator = beta * downSectionFactorDerivate + constantOne;

        if( Math.abs(numerator/denominator) < tolerance ) {
            return tryWetArea + numerator/denominator;
        }
        else {
            return evaluateNewWetArea(tryWetArea + numerator/denominator, crossSectionType);
        }
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

    Double evaluateBeta(Double linkSlope, Double linkRoughness) {
        return Math.sqrt(linkSlope)/ linkRoughness;
    }

    Double evaluateTheta(Double area, CrossSectionType crossSectionType) {
        Double adimensionalArea = area/crossSectionType.getAreaFull();
        return 0.031715 - 12.79384*adimensionalArea + 8.28479*Math.sqrt(adimensionalArea);
    }
}