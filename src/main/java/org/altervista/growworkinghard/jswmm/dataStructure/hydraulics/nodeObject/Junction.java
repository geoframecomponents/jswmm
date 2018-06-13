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

package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Junction extends AbstractNode {

    Double maximumDepthNode;
    Double initialDepthnode;
    Double maximumDepthSurcharge;
    Double pondingArea;

    public Junction(Double nodeElevation, Double maximumDepthNode, Double initialDepthnode,
                    Double maximumDepthSurcharge, Double pondingArea) {
        this.nodeElevation = nodeElevation;
        this.maximumDepthNode = maximumDepthNode;
        this.initialDepthnode = initialDepthnode;
        this.maximumDepthSurcharge = maximumDepthSurcharge;
        this.pondingArea = pondingArea;
    }

    @Override
    public void sumFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate) {
        for (Integer id : newFlowRate.keySet()) {
            if (!nodeFlowRate.containsKey(id)) {
                nodeFlowRate.put(id, new LinkedHashMap<>());
            }
            for (Instant time : newFlowRate.get(id).keySet()) {

                Double oldFLowRate = nodeFlowRate.get(id).get(time);
                if (oldFLowRate == null) {
                    LinkedHashMap<Instant, Double> newLHM = newFlowRate.get(id);
                    nodeFlowRate.put(id, newLHM);
                } else {
                    //System.out.print("time " + time);
                    //System.out.println(newFlowRate.get(id).get(time));

                    LinkedHashMap<Instant, Double> oldLHM = nodeFlowRate.get(id);
                    oldLHM.put(time, newFlowRate.get(id).get(time) + oldFLowRate);
                    nodeFlowRate.put(id, oldLHM);
                }
            }
        }
    }

    @Override
    public HashMap<Integer, LinkedHashMap<Instant, Double>> getFlowRate() {
        return this.nodeFlowRate;
    }
}
