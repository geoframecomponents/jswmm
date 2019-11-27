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

package com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment;

import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.Datetimeable;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.Unitable;
import org.altervista.growworkinghard.jswmm.inpparser.DataFromFile;
import org.altervista.growworkinghard.jswmm.inpparser.INPparser;

public abstract class AbstractSubcatchment extends INPparser {

    protected String name;

    Unitable subcatchmentUnits;
    Datetimeable subcatchmentTime;

    DataFromFile interfaceINP;

    public AbstractSubcatchment(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSubcatchmentUnits(Unitable subcatchmentUnits) {
        this.subcatchmentUnits = subcatchmentUnits;
    }

    public void setSubcatchmentTime(Datetimeable subcatchmentTime) {
        this.subcatchmentTime = subcatchmentTime;
    }

    public Unitable getSubcatchmentUnits() {
        return subcatchmentUnits;
    }

    public Datetimeable getSubcatchmentTime() {
        return subcatchmentTime;
    }
}
