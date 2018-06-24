package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import it.blogspot.geoframe.utils.GEOunitsTransform;

import java.util.LinkedHashMap;
import java.util.Map;

public class Lucchese_ca implements CommercialPipeSize {

    private final Map<Double, Double> pipe = new LinkedHashMap() {{
        put(400.0, 510.0);
        put(500.0, 620.0);
        put(600.0, 740.0);
        put(700.0, 850.0);
        put(800.0, 970.0);
        put(1000.0, 1200.0);
        put(1200.0, 1440.0);
    }};

    // returns commercial size in meters
    // expects designed size in meters
    @Override
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
