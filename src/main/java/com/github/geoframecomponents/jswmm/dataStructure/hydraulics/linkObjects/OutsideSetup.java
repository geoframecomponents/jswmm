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

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

import it.blogspot.geoframe.utils.GEOconstants;
import org.geotools.graph.util.geom.Coordinate2D;

public class OutsideSetup {

    String nodeName;
    private Coordinate2D nodeCoordinates;
    private double terrainElevation;
    private double baseElevation;
    private double offset;
    private double height;
    private double excavation;
    private double fillCoeff;
    private double waterDepth;

    HashMap<Integer, LinkedHashMap<Instant, Double>> streamWetArea = new HashMap<>();
    HashMap<Integer, LinkedHashMap<Instant, Double>> streamFlowRate = new HashMap<>();

    public OutsideSetup(String nodeName, Double downOffset, Double fillCoeff, Double x, Double y, double terrainElevation) {
        this.nodeName = nodeName;
        this.offset = downOffset;
        this.fillCoeff = fillCoeff;
        this.nodeCoordinates = new Coordinate2D(x, y);
        this.terrainElevation = terrainElevation;
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getStreamWetArea() {
        return streamWetArea;
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getStreamFlowRate() {
        return streamFlowRate;
    }

    public void setWetArea(Integer id, Instant time, Double wetArea) {
        if (!streamWetArea.containsKey(id)) {
            streamWetArea.put(id, new LinkedHashMap<>());
        }
        Double oldWetArea = streamWetArea.get(id).get(time);
        if (oldWetArea == null) {
            LinkedHashMap<Instant, Double> newLHM = new LinkedHashMap<>();
            newLHM.put(time, wetArea);
            streamWetArea.put(id, newLHM);
        }
        else {
            LinkedHashMap<Instant, Double> oldLHM = streamWetArea.get(id);
            oldLHM.put(time, wetArea);
            streamWetArea.put(id, oldLHM);
        }
    }

    public void setFlowRate(Integer id, Instant time, Double flowRate) {
        if (!streamFlowRate.containsKey(id)) {
            streamFlowRate.put(id, new LinkedHashMap<>());
        }
        LinkedHashMap<Instant, Double> oldLHM = streamFlowRate.get(id);
        oldLHM.put(time, flowRate);
        streamFlowRate.put(id, oldLHM);
    }

    public void sumStreamFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate) {
        for (Integer id : newFlowRate.keySet()) {
            if (!streamFlowRate.containsKey(id)) {
                streamFlowRate.put(id, new LinkedHashMap<>());
            }
            for (Instant time : newFlowRate.get(id).keySet()) {
                Double oldFLowRate = streamFlowRate.get(id).get(time);
                double value;
                if (oldFLowRate == null) {
                    value = newFlowRate.get(id).get(time);
                } else {
                    value = newFlowRate.get(id).get(time) + oldFLowRate;
                }
                LinkedHashMap<Instant, Double> oldLHM = streamFlowRate.get(id);
                oldLHM.put(time, value);
                streamFlowRate.put(id, oldLHM);
            }
        }
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

    public double getWaterDepth() {
        return waterDepth;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setWaterDepth(double waterDepth) {
        this.waterDepth = waterDepth;
    }

    public void upgradeOffset(double delta) {
        if (this.offset < Math.abs(delta)) {
            this.offset -= delta;
            this.height -= delta;
            this.baseElevation += delta;
            checkMaxExcavation(height);
        }
        else{
            this.offset += delta;
        }
    }

    private void checkMaxExcavation(double escavation) {
        if (escavation > GEOconstants.MAXIMUMEXCAVATION) {
            //TODO warning and save the escavation difference
            System.out.println("over MAXIMUMEXCAVATION");
        }
    }

    /**
     * Set the heights for the node inlet/outlet changing the offset
     * @param excavation
     * @param offset
     */
    public void setHeights(double excavation, double offset) {
        this.offset = offset;
        this.height = excavation + this.offset;
        this.baseElevation = terrainElevation - height;
        this.excavation = excavation;
        checkMaxExcavation(excavation);
    }

    /**
     * Set the heights for the node inlet/outlet without changing the offset
     * @param excavation
     */
    public void setHeights(double excavation) {
        this.height = excavation + this.offset;
        this.baseElevation = terrainElevation - height;
        this.excavation = excavation;
        checkMaxExcavation(excavation);
    }
}