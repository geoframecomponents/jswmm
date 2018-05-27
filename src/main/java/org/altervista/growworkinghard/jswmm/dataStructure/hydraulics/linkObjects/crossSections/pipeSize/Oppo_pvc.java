package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import java.util.HashMap;
import java.util.Map;

public class Oppo_pvc implements CommercialPipeSize {

    private final Map<Double, Double> pipe = new HashMap() {{
        put(15.36,16.0);
        put(19.22,20.0);
        put(24.02,25.0);
        put(30.26,31.5);
        put(38.42,40.0);
        put(48.04,50.0);
        put(60.54,63.0);
    }};

    // returns commercial size in cm
    // expects designed size in cm
    @Override
    public double[] getCommercialDiameter(double designedDiameter) {
        double[] commercialSize = null;
        for(Map.Entry<Double, Double> diameter : pipe.entrySet()) {
            double innerSize = diameter.getKey();
            if (innerSize > designedDiameter) {
                double outerSize = diameter.getValue();
                commercialSize = new double[]{innerSize, outerSize};
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
