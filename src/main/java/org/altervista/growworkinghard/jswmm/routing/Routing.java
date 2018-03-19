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

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.Conduit;
import org.altervista.growworkinghard.jswmm.dataStructure.routingDS.RoutingSetup;

import java.time.Instant;

public class Routing {

    /**
     * Time setup
     */
    private Instant initialTime;

    private Instant totalTime;


    /**
     * Simulation node fields
     */
    @In
    public String linkName = null;

    @In
    public String downstreamNodeName = null;

    /**
     * Link characteristics
     */
    @In
    public Conduit conduit;

    /**
     * Integration method setup
     */
    private Long routingStepSize;

    private RoutingSetup routingSetup;

    /**
     * Data structure
     */
    @In
    @Out
    public SWMMobject dataStructure = null;

    @Initialize
    public void initialize() {

        if(dataStructure != null && linkName != null) {
            this.initialTime = dataStructure.getTimeSetup().getStartDate();
            this.totalTime = dataStructure.getTimeSetup().getEndDate();

            this.routingSetup = dataStructure.getRoutingSetup();
            this.routingStepSize = routingSetup.getRoutingStepSize();

            this.conduit = dataStructure.getConduit().get(linkName);
        }
        else {
            throw new NullPointerException("Nothing implemented yet");
        }

    }

    @Execute
    public void run() {

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            conduit.evaluateFlowRate(currentTime);

            currentTime = currentTime.plusSeconds(routingStepSize);
        }
    }

    @Finalize
    void upgradeSWMMobject() {
        dataStructure.addNodeFlowRate(downstreamNodeName, conduit.getDownstreamFlowRate());
    }
}