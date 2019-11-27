/*
 * JSWMM: Reimplementation of EPA SWMM in Java
 * Copyright (C) 2019 Daniele Dalla Torre (ftt01)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.geoframecomponents.jswmm.routing;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.Conduit;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;
import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.Lucchese_ca;

import oms3.annotations.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Routing {

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

    @In
    public CommercialPipeSize pipeCompany = new Lucchese_ca();

    /**
     * Data structure
     */
    @In
    @Out
    public SWMMobject dataStructure = null;

    @InNode
    public HashMap<Integer, List<Integer>> net3subtrees;

    @OutNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> routingFlowRate;

    @Execute
    public void run() {

        System.out.println("Routing on " + linkName);
        if (dataStructure != null && linkName != null) {
            this.conduit = dataStructure.getConduit(linkName);
        } else {
            throw new NullPointerException("Data structure is null");
        }

        /**
         * Evaluate the maximum discharge over all the response curves
         */
        double maxDischarge = conduit.evaluateMaxDischarge();
        System.out.println("Q_MAX " + maxDischarge);

        /**
         * Dimensioning main method
         */
        conduit.evaluateDimension(maxDischarge, pipeCompany);

        //route the maximum discharge to next bucket
        conduit.evaluateFlowRate();

        routingFlowRate = conduit.getDownstreamFlowRate();
    }
}