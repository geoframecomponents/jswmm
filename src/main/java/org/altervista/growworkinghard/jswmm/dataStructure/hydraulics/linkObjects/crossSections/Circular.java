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

    Double diameter;
    Boolean alwaysIncrease = false;

    private final Double depthFull;
    private final Double areaFull;
    private final Double areaMax;
    private final Double hydraulicRadious;
    private final Double sectionFactorFull;

    public Circular(Double diameter) {
        this.diameter = diameter;
        this.depthFull = 0.938*diameter;
        this.areaFull = Math.PI * diameter * diameter / 4;
        this.areaMax = 0.7854*Math.pow(getDepthFull(), 2);
        this.hydraulicRadious = 0.25*getDepthFull();
        this.sectionFactorFull = getAreaFull()*Math.pow(getHydraulicRadiusFull(), 2.0/3.0);
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
    public Double getHydraulicRadiusFull() {
        return hydraulicRadious;
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
        return (5.0/3 - 2.0/3 * derivatedWetPerimeter(theta) * hydraulicRadious(area, wetPerimeter)) *
                Math.pow(hydraulicRadious(area, wetPerimeter), 2.0/3);
    }

    private Double derivatedWetPerimeter(Double theta) {
        return 4.0 / depthFull / (1 - Math.cos(theta));
    }

    private Double hydraulicRadious(Double area, Double wetPerimeter) {
        return area / wetPerimeter;
    }
}