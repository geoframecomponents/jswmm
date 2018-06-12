package org.altervista.growworkinghard.jswmm.dataStructure.routingDS;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.LinkedList;
import java.util.List;

class ChowTable {
    Double adimensionalArea;
    Double adimensionalSectionFactor;

    ChowTable(Double adimensionalArea, Double adimensionalSectionFactor) {
        this.adimensionalArea = adimensionalArea;
        this.adimensionalSectionFactor = adimensionalSectionFactor;
    }

    public Double getAdimensionalArea() {
       return adimensionalArea;
    }

    public Double getAdimensionalSectionFactor() {
        return adimensionalSectionFactor;
    }
}

public class SWMMroutingTools {

    private final Integer referenceTableLength;
    private final List<ChowTable> relationsTable = new LinkedList<>();

    public SWMMroutingTools(Integer referenceTableLength) {
        this.referenceTableLength = referenceTableLength;
        fillTables();
    }

    private void fillTables() {
        for(int i = 0; i <= referenceTableLength; i++) {
            Double theta = 2*i*Math.PI/180;
            if (theta == 0.0) {
                relationsTable.add(new ChowTable(0.0,0.0));
            }
            else {
                relationsTable.add(new ChowTable((theta-Math.sin(theta))/(2*Math.PI),
                        Math.pow((theta-Math.sin(theta)), 5.0/3.0) / (2 * Math.PI * Math.pow(theta, 2.0/3.0))));
            }
        }
    }

    public Double sectionFactorToArea(Double sectionFactor) {

        if (sectionFactor == 0.0) {
            return 0.0;
        }
        else {
            int elementCounter = 0;
            Double lowerSFValue = relationsTable.get(elementCounter).adimensionalSectionFactor;
            Double upperSFValue = null;

            for (ChowTable element : relationsTable) {
                if (element.getAdimensionalSectionFactor() < sectionFactor) {
                    lowerSFValue = element.adimensionalSectionFactor;
                    elementCounter++;
                } else {
                    upperSFValue = element.adimensionalSectionFactor;
                    break;
                }
            }
            double[] x = {lowerSFValue, upperSFValue};
            double[] y = {relationsTable.get(elementCounter-1).getAdimensionalArea(),
                    relationsTable.get(elementCounter).getAdimensionalArea()};

            // return linear interpolation of (x,y) on sectionFactor
            LinearInterpolator interpolator = new LinearInterpolator();
            PolynomialSplineFunction psf = interpolator.interpolate(x, y);
            return psf.value(sectionFactor);
        }
    }

    public Double areaToSectionFactor(Double area) {

        if (area == 0.0) {
            return 0.0;
        }
        else {
            int elementCounter = 0;
            Double lowerArea = relationsTable.get(elementCounter).adimensionalArea;
            Double upperArea = null;

            for (ChowTable element : relationsTable) {
                if (element.getAdimensionalArea() < area) {
                    lowerArea = element.adimensionalArea;
                    elementCounter++;
                }
                else {
                    upperArea = element.adimensionalArea;
                    break;
                }
            }
            double[] x = {lowerArea, upperArea};
            double[] y = {relationsTable.get(elementCounter-1).getAdimensionalSectionFactor(),
                    relationsTable.get(elementCounter).getAdimensionalSectionFactor()};

            // return linear interpolation of (x,y) on sectionFactor
            LinearInterpolator interpolator = new LinearInterpolator();
            PolynomialSplineFunction psf = interpolator.interpolate(x, y);
            return psf.value(area);
        }
    }

    public Double evaluateTheta(Double area, CrossSectionType crossSectionType) {

        Double tempTheta;
        Double deltaTheta;

        tempTheta = 0.031715 - 12.79384*area + 8.28479*Math.sqrt(area);
        do {
            deltaTheta = 2 * Math.PI * area - (tempTheta - Math.sin(tempTheta)) / (1 - Math.cos(tempTheta));
            tempTheta += deltaTheta;
        } while (Math.abs(deltaTheta) > 0.0001);

        return tempTheta;
    }
}





