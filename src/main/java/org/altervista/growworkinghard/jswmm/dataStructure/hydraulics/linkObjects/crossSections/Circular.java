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

    //Double depthFull;
    //Double areaFull;
    //Double hydraulicRadiousFull;
    //Double sectionFactorFull;

    public Circular(Double diameter) {
        this.diameter = diameter;
    }

    @Override
    public Double getDepthFull() {
        return 0.938*diameter;
    }

    @Override
    public Double getAreaFull() {
        return 0.7854*Math.pow(getDepthFull(), 2);
    }

    @Override
    public Double getHydraulicRadiusFull() {
        return 0.25*getDepthFull();
    }

    @Override
    public Double getSectionFactorFull() {
        return getAreaFull()*Math.pow(getHydraulicRadiusFull(), 2.0/3.0);
    }
}
