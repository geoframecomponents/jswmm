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
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.geotools.graph.util.geom.Coordinate2D;

public class OutsideSetup {

    String nodeName;
    private Coordinate2D nodeCoordinates;
    private Double terrainElevation;
    private Double baseElevation;

    Double nodeOffset;
    private Double fillCoeff;

    HashMap<Integer, LinkedHashMap<Instant, Double>> streamWetArea = new HashMap<>();
    HashMap<Integer, LinkedHashMap<Instant, Double>> streamFlowRate = new HashMap<>();

    public OutsideSetup(String nodeName, Double nodeOffset, Double fillCoeff, Double x, Double y) {
        this.nodeName = nodeName;
        this.nodeOffset = nodeOffset;
        this.fillCoeff = fillCoeff;
        this.nodeCoordinates = new Coordinate2D(x, y);
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getStreamWetArea() {
        return streamWetArea;
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getStreamFlowRate() {
        return streamFlowRate;
    }

    public void setWetArea(Integer id, Instant time, Double value) {
        LinkedHashMap<Instant, Double> temp = new LinkedHashMap<>();
        temp.put(time, value);
        this.streamWetArea.put(id, temp);
    }

    public void setFlowRate(Integer id, Instant time, Double flowRate) {
        LinkedHashMap<Instant, Double> temp = new LinkedHashMap<>();
        temp.put(time, flowRate);
        this.streamFlowRate.put(id, temp);
    }

    public Double getFillCoeff() {
        return fillCoeff;
    }

    public Coordinate2D getNodeCoordinates() {
        return nodeCoordinates;
    }

    public Double getTerrainElevation() {
        return terrainElevation;
    }

    public Double getBaseElevation() {
        return baseElevation;
    }
}
