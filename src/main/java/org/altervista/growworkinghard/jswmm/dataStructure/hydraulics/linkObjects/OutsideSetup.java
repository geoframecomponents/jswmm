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

import it.blogspot.geoframe.utils.GEOconstants;
import org.geotools.graph.util.geom.Coordinate2D;

public class OutsideSetup {

    String nodeName;
    private Coordinate2D nodeCoordinates;
    private double terrainElevation;
    private double baseElevation;
    private double downOffset;
    private double upOffset;
    private double height;
    private double excavation;
    private double fillCoeff;
    private double waterDepth;

    HashMap<Integer, LinkedHashMap<Instant, Double>> streamWetArea = new HashMap<>();
    HashMap<Integer, LinkedHashMap<Instant, Double>> streamFlowRate = new HashMap<>();

    public OutsideSetup(String nodeName, Double downOffset, Double fillCoeff, Double x, Double y, double terrainElevation) {
        this.nodeName = nodeName;
        this.downOffset = downOffset;
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
        Double oldFLowRate = streamFlowRate.get(id).get(time);
        if (oldFLowRate == null) {
            LinkedHashMap<Instant, Double> newLHM = new LinkedHashMap<>();
            newLHM.put(time, flowRate);
            streamFlowRate.put(id, newLHM);
        }
        else {
            LinkedHashMap<Instant, Double> oldLHM = streamFlowRate.get(id);
            oldLHM.put(time, flowRate);
            streamFlowRate.put(id, oldLHM);
        }
    }

    public void sumStreamFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate) {
        for (Integer id : newFlowRate.keySet()) {
            if (!streamFlowRate.containsKey(id)) {
                streamFlowRate.put(id, new LinkedHashMap<>());
            }
            for (Instant time : newFlowRate.get(id).keySet()) {
                Double oldFLowRate = streamFlowRate.get(id).get(time);
                if (oldFLowRate == null) {
                    LinkedHashMap<Instant, Double> newLHM = newFlowRate.get(id);
                    streamFlowRate.put(id, newLHM);
                } else {
                    LinkedHashMap<Instant, Double> oldLHM = streamFlowRate.get(id);
                    oldLHM.put(time, newFlowRate.get(id).get(time) + oldFLowRate);
                    streamFlowRate.put(id, oldLHM);
                }
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

    private void setHeight(double height) {
        this.height = height;
    }

    private void setBaseElevation(double height) {
        this.baseElevation = terrainElevation - height ;
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
        this.upOffset += delta;
        this.height += delta;
        this.baseElevation += delta;
        checkMaxExcavation(height);//TODO
    }

    private void checkMaxExcavation(double escavation) {
        if (escavation > GEOconstants.MAXIMUMEXCAVATION) {
            //TODO warning
            System.out.println("over MAXIMUMEXCAVATION");
        }
    }

    public void setHeights(double excavation, double offset) {
        this.downOffset = offset;
        setHeight(excavation + downOffset);
        setBaseElevation( height );
        this.excavation = excavation;
        checkMaxExcavation(excavation);
    }

    public void setHeights(double excavation) {
        setHeight(excavation + downOffset);
        setBaseElevation( height );
        this.excavation = excavation;
        checkMaxExcavation(excavation);
    }
}
