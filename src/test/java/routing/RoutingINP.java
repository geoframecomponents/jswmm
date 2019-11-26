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

package routing;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.routing.FlowRateDispatcher;
import com.github.geoframecomponents.jswmm.routing.Routing;
import com.github.geoframecomponents.jswmm.runoff.PreRunoff;
import com.github.geoframecomponents.jswmm.runoff.Runoff;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class RoutingINP {

    SWMMobject data;
    PreRunoff preRunoff_1;
    PreRunoff preRunoff_2;
    Runoff runoff_1;
    Runoff runoff_2;
    Routing routing1;
    Routing routing2;
    Routing routing3;
    FlowRateDispatcher flowRateDispatcher1;
    FlowRateDispatcher flowRateDispatcher2;
    FlowRateDispatcher flowRateDispatcher3;

    @Before
    public void initialize() throws ConfigurationException {
        int numberOfCurves = 3;
        data = new SWMMobject("network12.inp", numberOfCurves);

        preRunoff_1 = new PreRunoff();
        preRunoff_2 = new PreRunoff();

        preRunoff_1.setDataStructure(data);
        preRunoff_2.setDataStructure(data);

        preRunoff_1.areaName = "S1";
        preRunoff_2.areaName = "S2";

        preRunoff_1.numberOfCurves = numberOfCurves;
        preRunoff_2.numberOfCurves = numberOfCurves;

        preRunoff_1.initialize();
        preRunoff_2.initialize();

        preRunoff_1.run();
        preRunoff_2.run();

        runoff_1 = new Runoff();
        runoff_2 = new Runoff();

        runoff_1.dataStructure = data;
        runoff_1.areaName = "S1";
        runoff_1.nodeName = "J1";
        runoff_1.adaptedRainfallData = preRunoff_1.getAdaptedRainfallData();

        runoff_2.dataStructure = data;
        runoff_2.areaName = "S2";
        runoff_2.nodeName = "J3";
        runoff_2.adaptedRainfallData = preRunoff_2.getAdaptedRainfallData();

        runoff_1.initialize();
        runoff_2.initialize();

        runoff_1.run();
        runoff_2.run();

        //System.out.println(runoff_1.runoffFlowRate);
        //System.out.println(runoff_2.runoffFlowRate);
    }

    @Test
    public void execute() {

        routing1 = new Routing();
        flowRateDispatcher1 = new FlowRateDispatcher();
        flowRateDispatcher1.dataStructure = data;
        flowRateDispatcher1.setFlowRate1(runoff_1.runoffFlowRate);
        flowRateDispatcher1.upstreamNodeName = "J1";
        flowRateDispatcher1.linkName = "8";
        flowRateDispatcher1.run();

        //System.out.println(flowRateDispatcher1.flowRate1);

        List<Integer> subtree = new ArrayList<>();
        subtree.add(0, 8);
        //subtree.add(0, 10);
        //subtree.add(1, 8);
        //subtree.add(2, 9);
        //routingTest("10", subtree);

        routing1.dataStructure = data;
        routing1.linkName = "8";

        routing1.net3subtrees = new HashMap<>();
        routing1.net3subtrees.put(subtree.get(0), subtree);

        routing1.run();

        flowRateDispatcher2 = new FlowRateDispatcher();
        flowRateDispatcher2.dataStructure = data;
        flowRateDispatcher2.setFlowRate1(runoff_2.runoffFlowRate);
        flowRateDispatcher2.upstreamNodeName = "J3";
        flowRateDispatcher2.linkName = "9";
        flowRateDispatcher2.run();

        routing2 = new Routing();

        subtree.set(0, 9);
        //subtree.add(0, 10);
        //subtree.add(1, 8);
        //subtree.add(2, 9);
        //routingTest("10", subtree);

        routing2.dataStructure = data;
        routing2.linkName = "9";

        routing2.net3subtrees = new HashMap<>();
        routing2.net3subtrees.put(subtree.get(0), subtree);

        routing2.run();

        //System.out.println(routing1.routingFlowRate);

        flowRateDispatcher3 = new FlowRateDispatcher();
        flowRateDispatcher3.dataStructure = data;
        flowRateDispatcher3.setFlowRate1(routing1.routingFlowRate);
        flowRateDispatcher3.setFlowRate2(routing2.routingFlowRate);
        flowRateDispatcher3.upstreamNodeName = "J4";
        flowRateDispatcher3.linkName = "10";
        flowRateDispatcher3.run();

        routing3 = new Routing();

        subtree.set(0, 10);
        subtree.add(1, 8);
        subtree.add(2, 9);

        routing3.dataStructure = data;
        routing3.linkName = "10";

        routing3.net3subtrees = new HashMap<>();
        routing3.net3subtrees.put(subtree.get(0), subtree);

        routing3.run();
    }
}