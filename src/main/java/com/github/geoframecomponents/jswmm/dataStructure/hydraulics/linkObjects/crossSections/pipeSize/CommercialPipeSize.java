package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import it.blogspot.geoframe.utils.GEOunitsTransform;

import java.util.Map;

public abstract class CommercialPipeSize {

    Map<Double, Double> pipe;

    // returns commercial size in meters
    // expects designed size in meters
    public double[] getCommercialDiameter(Double designedDiameter) {
        designedDiameter = GEOunitsTransform.meters2millimiters(designedDiameter);
        double[] commercialSize = null;
        for(Map.Entry<Double, Double> diameter : pipe.entrySet()) {
            double innerSize = diameter.getKey();
            if (innerSize > designedDiameter) {
                double outerSize = GEOunitsTransform.millimiters2meters(diameter.getValue());
                innerSize = GEOunitsTransform.millimiters2meters(innerSize);
                commercialSize = new double[]{innerSize, outerSize};
                break;
            }
        }
        if (commercialSize == null) {
            String msg = "Designed diameter " + designedDiameter;
            msg += " is bigger than the biggest diameter available.";
            throw new NullPointerException(msg);
        }
        return commercialSize;
    }

}
