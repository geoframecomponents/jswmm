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

package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections;

public class Circular implements CrossSectionType {

    private double[] diameters;
    Boolean alwaysIncrease = false;

    private Double depthFull;
    private Double areaFull;
    private Double areaMax;
    private Double hydraulicRadiousFull;
    private Double sectionFactorFull;

    public Circular(double innerDiameter, double outerDiameter) {
        this.diameters = new double[]{innerDiameter, outerDiameter};
        this.depthFull = 0.938 * innerDiameter;
        this.areaFull = Math.PI * innerDiameter * innerDiameter / 4;
        this.areaMax = 0.7854 * Math.pow(getDepthFull(), 2);
        this.hydraulicRadiousFull = 0.25 * getDepthFull();
        this.sectionFactorFull = getAreaFull() * Math.pow(getHydraulicRadiusFull(), 2.0 / 3.0);
    }

    public Circular(double innerDiameter) {
        this(innerDiameter, innerDiameter);
    }

    @Override
    public void setDimensions(double innerDiameter, double outerDiameter) {
        if (this.diameters == null) {
            this.diameters = new double[]{innerDiameter, outerDiameter};
        }
        else{
            this.diameters[0] = innerDiameter;
            this.diameters[1] = outerDiameter;
        }
    }

    @Override
    public Double getDepthFull() {
        return depthFull;
    }

    @Override
    public Double getAreaFull() {
        return areaFull;
    }

    @Override
    public Double computeHydraulicRadious(Double diameter, Double fillAngle) {
        return diameter / ( 1 - Math.sin(fillAngle)/fillAngle );
    }

    @Override
    public Double getHydraulicRadiusFull() {
        return hydraulicRadiousFull;
    }

    @Override
    public Double getSectionFactorFull() {
        return sectionFactorFull;
    }

    @Override
    public Double getAreaMax() {
        return areaMax;
    }

    @Override
    public Boolean getAlwaysIncrease() {
        return alwaysIncrease;
    }

    public Double derivatedSectionFactor(Double theta) {
        Double area = areaFull * (theta - Math.sin(theta)) / (2 * Math.PI);
        Double wetPerimeter = theta * depthFull / 2;
        return (5.0 / 3 - 2.0 / 3 * derivatedWetPerimeter(theta) * getHydraulicRadious(area, wetPerimeter)) *
                Math.pow(getHydraulicRadious(area, wetPerimeter), 2.0 / 3);
    }

    private Double getHydraulicRadious(Double area, Double wetPerimeter) {
        return area / wetPerimeter;
    }

    private Double derivatedWetPerimeter(Double theta) {
        return 4.0 / depthFull / (1 - Math.cos(theta));
    }

    public Double computeFillAngle(Double fillCoefficient) {
        return 2 * Math.acos( 1 - 2 * fillCoefficient );
    }
}