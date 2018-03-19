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

import java.time.Instant;
import java.util.LinkedHashMap;

public class OutsideSetup {

    String nodeName;

    Double nodeOffset;
    Double maximumStreamFlowRate;

    LinkedHashMap<Instant, Double> streamWetArea;
    LinkedHashMap<Instant, Double> streamFlowRate;

    public OutsideSetup(String nodeName, Double nodeOffset, Double maximumFlowRate) {
        this.nodeName = nodeName;
        this.nodeOffset = nodeOffset;
        this.maximumStreamFlowRate = maximumFlowRate;
    }

    public LinkedHashMap<Instant, Double> getStreamWetArea() {
        return streamWetArea;
    }

    public LinkedHashMap<Instant, Double> getStreamFlowRate() {
        return streamFlowRate;
    }

    public void setWetArea(Instant time, Double value) {
        if (streamWetArea == null) {
            streamWetArea = new LinkedHashMap<>();
        }
        this.streamWetArea.put(time, value);
    }

    public void setFlowRate(Instant time, Double flowRate) {
        if (streamFlowRate == null) {
            streamFlowRate = new LinkedHashMap<>();
        }
        this.streamFlowRate.put(time, flowRate);
    }
}
