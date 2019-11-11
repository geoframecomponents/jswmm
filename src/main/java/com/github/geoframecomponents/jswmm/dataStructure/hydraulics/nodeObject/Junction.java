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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.nodeObject;

import com.github.geoframecomponents.jswmm.dataStructure.options.units.SWMMunits;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import org.altervista.growworkinghard.jswmm.inpparser.objects.JunctionINP;
import org.altervista.growworkinghard.jswmm.inpparser.objects.OutfallINP;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Junction extends AbstractNode {

    Double maximumDepthNode;
    Double initialDepthnode;
    Double maximumDepthSurcharge;
    Double pondingArea;

    public Junction(String name, Unitable units, Double nodeElevation, Double maximumDepthNode,
                    Double initialDepthnode, Double maximumDepthSurcharge, Double pondingArea) {

        super(name);
        this.nodeUnits = units;
        this.nodeElevation = nodeElevation;

        this.maximumDepthNode = maximumDepthNode;
        this.initialDepthnode = initialDepthnode;
        this.maximumDepthSurcharge = maximumDepthSurcharge;
        this.pondingArea = pondingArea;
    }

    public Junction(String name, Unitable units, String INPfile) throws ConfigurationException {

        super(name);
        interfaceINP = new JunctionINP(INPfile);
        this.nodeUnits = units;

        this.nodeElevation = Double.valueOf( ((JunctionINP) interfaceINP).nodeElev(INPfile, name) );
        this.maximumDepthNode = Double.valueOf( ((JunctionINP) interfaceINP).maxDepth(INPfile, name) );
        this.initialDepthnode = Double.valueOf( ((JunctionINP) interfaceINP).initDepth(INPfile, name) );
        this.maximumDepthSurcharge = Double.valueOf( ((JunctionINP) interfaceINP).maxDepthSur(INPfile, name) );
        this.pondingArea = Double.valueOf( ((JunctionINP) interfaceINP).pondArea(INPfile, name) );
    }

    public Junction(String name, Unitable units, String INPfile, boolean outfall) throws ConfigurationException {
        super(name);

        interfaceINP = new OutfallINP(INPfile);
        this.nodeUnits = units;

        this.nodeElevation = Double.valueOf( ((OutfallINP) interfaceINP).nodeElev(INPfile, name) );
        this.maximumDepthNode = Double.valueOf( ((OutfallINP) interfaceINP).maxDepth(INPfile, name) );
        this.initialDepthnode = Double.valueOf( ((OutfallINP) interfaceINP).initDepth(INPfile, name) );
        this.maximumDepthSurcharge = Double.valueOf( ((OutfallINP) interfaceINP).maxDepthSur(INPfile, name) );
        this.pondingArea = Double.valueOf( ((OutfallINP) interfaceINP).pondArea(INPfile, name) );
    }

    @Override
    public void sumFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate) {

        for (Integer id : newFlowRate.keySet()) {
            if (!nodeFlowRate.containsKey(id)) {
                nodeFlowRate.put(id, new LinkedHashMap<>());
            }
            for (Instant time : newFlowRate.get(id).keySet()) {
                Double oldFLowRate = nodeFlowRate.get(id).get(time);
                double value;
                if (oldFLowRate == null) {
                    value = newFlowRate.get(id).get(time);
                } else {
                    value = newFlowRate.get(id).get(time) + oldFLowRate;
                }
                LinkedHashMap<Instant, Double> oldLHM = nodeFlowRate.get(id);
                oldLHM.put(time, value);
                nodeFlowRate.put(id, oldLHM);
            }
        }
    }

    @Override
    public HashMap<Integer, LinkedHashMap<Instant, Double>> getFlowRate() {
        return this.nodeFlowRate;
    }
}