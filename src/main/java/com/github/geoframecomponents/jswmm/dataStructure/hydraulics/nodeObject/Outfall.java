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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.nodeObject;

import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import org.altervista.growworkinghard.jswmm.inpparser.objects.OutfallINP;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Outfall extends AbstractNode {

    Double fixedStage;
    LinkedHashMap<Instant, Double> tidalCurve;
    LinkedHashMap<Instant, Double> stageTimeseries;
    boolean gated;
    String routeTo;

    //TODO solve the conflict with tidal/timeseries

    public Outfall(String name, Unitable units, Double nodeElevation, Double fixedStage,
                   LinkedHashMap<Instant, Double> tidalCurve, LinkedHashMap<Instant, Double> stageTimeseries,
                   boolean gated, String routeTo) {

        super(name);
        this.nodeUnits = units;
        this.nodeElevation = nodeElevation;

        this.fixedStage = fixedStage;
        this.tidalCurve = tidalCurve;
        this.stageTimeseries = stageTimeseries;
        this.gated = gated;
        this.routeTo = routeTo;
    }

    public Outfall(String name, Unitable units, String INPfile) throws ConfigurationException {

        super(name);
        interfaceINP = new OutfallINP(INPfile);

        this.nodeUnits = units;
        this.nodeElevation = Double.valueOf( ((OutfallINP) interfaceINP).nodeElev(INPfile, name) );

        this.fixedStage = Double.valueOf( ((OutfallINP) interfaceINP).fixedStage(INPfile, name) );
        this.tidalCurve = null;
        this.stageTimeseries = null;
        this.gated = Boolean.parseBoolean( ((OutfallINP) interfaceINP).fixedStage(INPfile, name) );
        this.routeTo = ((OutfallINP) interfaceINP).fixedStage(INPfile, name);
    }

    @Override
    public void sumFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public HashMap<Integer, LinkedHashMap<Instant, Double>> getFlowRate() {
        throw new NullPointerException("Nothing implemented yet");
    }
}
