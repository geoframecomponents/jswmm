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
package com.github.geoframecomponents.jswmm.dataStructure.routingDS;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.OutsideSetup;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.CrossSectionType;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.PeriodStepTolerance;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface RoutingSolver {

    RoutedFlow routeFlowRate(Integer id, Instant currentTime, HashMap<Integer, LinkedHashMap<Instant, Double>> upstreamFlow,
                             OutsideSetup downstreamOutside, Double linkLength, Double linkRoughness,
                             Double linkSlope, CrossSectionType crossSectionType, double routingStep);

    public double adaptTimeDelay(double routingStepSize, double timeDelay);
}
