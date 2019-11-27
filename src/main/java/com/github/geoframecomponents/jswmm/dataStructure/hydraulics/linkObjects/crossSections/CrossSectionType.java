/*
 * JSWMM: Reimplementation of EPA SWMM in Java
 * Copyright (C) 2019 Daniele Dalla Torre (ftt01)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections;

/**
 * Class that defines the properties of the pipe.
 *
 * Extending this abstract class is possible to define personalized cross section type of pipe.
 * @author ftt01 (dallatorre.daniele@gmail.com)
 */
public interface CrossSectionType {

    /**
     * Defines if the cross section defined always increase its capacity increasing the discharge.
     * @return <b>true</b> id the capacity always increase.
     */
    public Boolean getAlwaysIncrease();

    /**
     * @param theta fill angle
     * @return derivative section factor
     */
    public Double derivatedSectionFactor(Double theta);

    Double getDepthFull();

    Double getAreaFull();

    Double getHydraulicRadiusFull();

    Double getDischargeFull(double roughnessCoefficient, double slope);

    Double getAreaMax();

    Double computeHydraulicRadious(Double diameter, Double fillAngle);

    Double computeFillAngle(Double fillCoeff);

    void setDimensions(double innerDiameter, double outerDiameter);

    double getMainDimension();
}
