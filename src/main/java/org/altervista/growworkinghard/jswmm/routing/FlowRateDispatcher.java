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
    public String upstreamNodeName = null;

    @In
    public String linkName = null;

    public void run() {
        LinkedHashMap<Instant, Double> flowRate = dataStructure.getJunctions().get(upstreamNodeName).getFlowRate();
        dataStructure.getConduit().get(linkName).setFlowRate(flowRate);
    }
}