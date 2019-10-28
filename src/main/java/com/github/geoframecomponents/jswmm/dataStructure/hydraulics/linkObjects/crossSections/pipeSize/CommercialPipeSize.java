/*
JSWMM: Reimplementation of EPA SWMM in Java
Copyright (C) 2019 Daniele Dalla Torre (ftt01)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import it.blogspot.geoframe.utils.GEOunitsTransform;

import java.util.Map;

/**
 * Class that defines the commercial dimensions of pipe.
 *
 * Extending this abstract class is possible to define personalized dimensions of pipe.
 */

public abstract class CommercialPipeSize {

    Map<Double, Double> pipe;

    /**
     * Method that return closer right diameter in <b>meters</b>.
     * @param designedDiameter The diameter obtained from the model.
     * @return Best diameter that satisfy the <em>designedDiameter</em>. In <b>meters</b>.
     */
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
