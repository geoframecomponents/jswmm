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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize.CommercialPipeSize;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import com.github.geoframecomponents.jswmm.dataStructure.routingDS.RoutingSolver;
import org.altervista.growworkinghard.jswmm.inpparser.DataFromFile;
import org.altervista.growworkinghard.jswmm.inpparser.INPparser;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Generic link object
 */
public abstract class AbstractLink extends INPparser {

    protected String name;

    protected Unitable linksUnits;
    protected Datetimeable linksTime;

    RoutingSolver routingSolver;

    OutsideSetup upstreamOutside;
    OutsideSetup downstreamOutside;

    DataFromFile interfaceINP;

    public AbstractLink(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setLinksUnits(Unitable linksUnits) {
        this.linksUnits = linksUnits;
    }

    public void setLinksTime(Datetimeable linksTime) {
        this.linksTime = linksTime;
    }

    public Unitable getLinksUnits() {
        if (linksUnits!=null) {
            return linksUnits;
        }
        else {
            throw new NullPointerException("linksUnits null");
        }
    }

    public Datetimeable getLinksTime() {
        if (linksTime !=null) {
            return linksTime;
        }
        else {
            throw new NullPointerException("linksTime null");
        }
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getDownstreamFlowRate() {
        if (downstreamOutside.streamFlowRate !=null) {
            return downstreamOutside.streamFlowRate;
        }
        else {
            throw new NullPointerException("downstreamOutside.streamFlowRate null");
        }
    }

    public HashMap<Integer, LinkedHashMap<Instant, Double>> getUpstreamFlowRate() {
        if (upstreamOutside.streamFlowRate !=null) {
            return upstreamOutside.streamFlowRate;
        }
        else {
            throw new NullPointerException("upstreamOutside.streamFlowRate null");
        }
    }

    public abstract OutsideSetup getUpstreamOutside();

    public abstract OutsideSetup getDownstreamOutside();

    public abstract void evaluateFlowRate();

    public abstract double evaluateMaxDischarge();

    public abstract void evaluateDimension(Double discharge, CommercialPipeSize pipeCompany);
}
