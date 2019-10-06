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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.ProjectUnits;

import java.time.Instant;

class Pump extends AbstractLink {

    public Pump(ProjectUnits units) {
        super(units);
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
    public void setInitialUpFlowRate(Integer id, Instant time, Double flowRate) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public void setInitialUpWetArea(Integer id, Instant startDate, double flowRate) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public void evaluateFlowRate(Instant currentTime) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public Double evaluateMaxDischarge(Instant currentTime, Double maxDischarge) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public void evaluateDimension(Double discharge, CommercialPipeSize pipeCompany) {
        throw new NullPointerException("Nothing implemented yet");
    }
}
