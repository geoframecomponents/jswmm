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

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import java.util.LinkedHashMap;

public class Lucchese_ca extends CommercialPipeSize {

    public Lucchese_ca() {
        pipe = new LinkedHashMap() {{
            put(400.0, 510.0);
            put(500.0, 620.0);
            put(600.0, 740.0);
            put(700.0, 850.0);
            put(800.0, 970.0);
            put(1000.0, 1200.0);
            put(1200.0, 1440.0);
        }};
    }
}