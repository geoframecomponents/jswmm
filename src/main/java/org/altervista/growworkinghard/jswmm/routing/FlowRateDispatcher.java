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
import java.util.LinkedHashMap;

public class FlowRateDispatcher {

    @In
    @Out
    SWMMobject dataStructure;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate1;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate2;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate3;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate4;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate5;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate6;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate7;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate8;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate9;

    @InNode
    public LinkedHashMap<Instant, Double> flowRate10;

    @In
    public String upstreamNodeName;

    @In
    public String linkName = null;

    private LinkedHashMap<Instant, Double> nodeFlowRate;

    public FlowRateDispatcher(String upstreamNodeName, String linkName) {
        this.upstreamNodeName = upstreamNodeName;
        this.linkName = linkName; 
    }

    @Execute
    public void run() {
        if (flowRate1 != null) {
            sumFlowRate(flowRate1);
        }
        if (flowRate2 != null) {
            sumFlowRate(flowRate2);
        }
        if (flowRate3 != null) {
            sumFlowRate(flowRate3);
        }
        if (flowRate4 != null) {
            sumFlowRate(flowRate4);
        }
        if (flowRate5 != null) {
            sumFlowRate(flowRate5);
        }
        if (flowRate6 != null) {
            sumFlowRate(flowRate6);
        }
        if (flowRate7 != null) {
            sumFlowRate(flowRate7);
        }
        if (flowRate8 != null) {
            sumFlowRate(flowRate8);
        }
        if (flowRate9 != null) {
            sumFlowRate(flowRate9);
        }
        if (flowRate10 != null) {
            sumFlowRate(flowRate10);
        }
        dataStructure.setNodeFlowRate(upstreamNodeName, nodeFlowRate);
        dataStructure.setLinkFlowRate(linkName, nodeFlowRate);
    }

    private void sumFlowRate(LinkedHashMap<Instant, Double> flowRate) {
        if (nodeFlowRate == null) {
            nodeFlowRate = new LinkedHashMap<>(flowRate);
        }
        else {
            flowRate.forEach((k, v) -> nodeFlowRate.merge(k, v, Double::sum));
        }
    }
}