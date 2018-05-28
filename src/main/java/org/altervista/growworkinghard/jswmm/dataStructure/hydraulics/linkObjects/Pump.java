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

package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

class Pump extends AbstractLink {

    @Override
    public OutsideSetup getUpstreamOutside() {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public OutsideSetup getDownstreamOutside() {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public void sumUpstreamFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate) {
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
    public Double evaluateMaxDischarge(Instant currentTime) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public void evaluateDimension(Double discharge, CommercialPipeSize pipeCompany) {
        throw new NullPointerException("Nothing implemented yet");
    }
}