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

package org.altervista.growworkinghard.jswmm.routing;

import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.InNode;
import oms3.annotations.Out;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class FlowRateDispatcher {

    @InNode
    @Out
    SWMMobject dataStructure;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate1;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate2;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate3;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate4;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate5;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate6;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate7;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate8;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate9;

    @InNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate10;

    @In
    public String upstreamNodeName = null;

    @In
    public String linkName = null;

    public FlowRateDispatcher(String upstreamNodeName, String linkName) {
        this.upstreamNodeName = upstreamNodeName;
        this.linkName = linkName; 
    }

    @Execute
    public void run() {
        if (flowRate1 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate1);
            dataStructure.setLinkFlowRate(linkName, flowRate1);
        }
        if (flowRate2 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate2);
            dataStructure.setLinkFlowRate(linkName, flowRate2);
        }
        if (flowRate3 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate3);
            dataStructure.setLinkFlowRate(linkName, flowRate3);
        }
        if (flowRate4 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate4);
            dataStructure.setLinkFlowRate(linkName, flowRate4);
        }
        if (flowRate5 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate5);
            dataStructure.setLinkFlowRate(linkName, flowRate5);
        }
        if (flowRate6 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate6);
            dataStructure.setLinkFlowRate(linkName, flowRate6);
        }
        if (flowRate7 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate7);
            dataStructure.setLinkFlowRate(linkName, flowRate7);
        }
        if (flowRate8 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate8);
            dataStructure.setLinkFlowRate(linkName, flowRate8);
        }
        if (flowRate9 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate9);
            dataStructure.setLinkFlowRate(linkName, flowRate9);
        }
        if (flowRate10 != null) {
            dataStructure.setNodeFlowRate(upstreamNodeName, flowRate10);
            dataStructure.setLinkFlowRate(linkName, flowRate10);
        }
    }
}