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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;

import java.time.Instant;

class Pump extends AbstractLink {


    public Pump(String name) {
        super(name);
    }

    @Override
    public OutsideSetup getUpstreamOutside() {
        throw new NullPointerException("Nothing implemented yet");

    }

    @Override
    public OutsideSetup getDownstreamOutside() {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public void evaluateFlowRate() {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public double evaluateMaxDischarge() {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public void evaluateDimension(Double discharge, CommercialPipeSize pipeCompany) {
        throw new NullPointerException("Nothing implemented yet");

    }
}
