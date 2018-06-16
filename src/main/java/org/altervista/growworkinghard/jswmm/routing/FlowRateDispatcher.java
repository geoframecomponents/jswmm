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
import java.util.Map;

public class FlowRateDispatcher {

    @InNode
    @Out
    public SWMMobject dataStructure;

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

    @Execute
    public void run() {

        Long routingStepSize = dataStructure.getRoutingSetup().getRoutingStepSize();
        Long flowRateStepSize = dataStructure.getRunoffSetup().getRunoffStepSize();//TODO must be generalized
        Instant initialTime = dataStructure.getTimeSetup().getStartDate();
        Instant totalTime = dataStructure.getTimeSetup().getEndDate();

        if (flowRate1 != null) {
            System.out.println("Processing flowrate1");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate1);
        }

        if (flowRate2 != null) {
            System.out.println("Processing flowrate2");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate2);
        }

        if (flowRate3 != null) {
            System.out.println("Processing flowrate3");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate3);
        }

        if (flowRate4 != null) {
            System.out.println("Processing flowrate4");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate4);
        }

        if (flowRate5 != null) {
            System.out.println("Processing flowRate5");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate5);
        }

        if (flowRate6 != null) {
            System.out.println("Processing flowRate6");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate6);
        }

        if (flowRate7 != null) {
            System.out.println("Processing flowRate7");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate7);
        }

        if (flowRate8 != null) {
            System.out.println("Processing flowrate8");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate8);
        }

        if (flowRate9 != null) {
            System.out.println("Processing flowrate9");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate9);
        }

        if (flowRate10 != null) {
            System.out.println("Processing flowrate10");
            dispatchFlow(routingStepSize, flowRateStepSize, totalTime.getEpochSecond(),
                    initialTime.getEpochSecond(), flowRate10);
        }

    }

    private void dispatchFlow(Long routingStepSize, Long flowRateStepSize, long totalTime, long initialTime,
                              HashMap<Integer, LinkedHashMap<Instant, Double>> flowRate) {

        HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate = new HashMap<>();

        if ( !routingStepSize.equals(flowRateStepSize) ) {
            for (Integer id : flowRate.keySet()) {
                LinkedHashMap<Instant, Double> currentFlowRate = dataStructure.adaptDataSeries(routingStepSize,
                        flowRateStepSize, totalTime, initialTime, flowRate.get(id));
                newFlowRate.put(id, currentFlowRate);
            }
        }

        /*for (Map.Entry<Integer, LinkedHashMap<Instant, Double>> entry : newFlowRate.entrySet()) {
            for (Instant time : entry.getValue().keySet()) {
                System.out.println("ID " + entry.getKey());
                System.out.println("Instant " + time);
                System.out.println("Value " + entry.getValue().get(time));
            }
        }*/

        dataStructure.setNodeFlowRate(upstreamNodeName, newFlowRate);
        dataStructure.setLinkFlowRate(linkName, newFlowRate);
    }
}

            /*for (Map.Entry<Integer, LinkedHashMap<Instant, Double>> entry : flowRate1.entrySet()) {
                for (Instant time : entry.getValue().keySet()) {
                    System.out.println("ID " + entry.getKey());
                    System.out.println("Instant " + time);
                    System.out.println("Value " + entry.getValue().get(time));
                }
            }


            for (Integer id : flowRate.keySet()) {
            LinkedHashMap<Instant, Double> currentFlow = dataStructure.getConduit(linkName).getUpstreamOutside().getStreamFlowRate().get(id);
            for (Instant time : currentFlow.keySet()) {
                System.out.println("time " +  time);
                System.out.println("Value " + currentFlow.get(time));
            }
        }

        */