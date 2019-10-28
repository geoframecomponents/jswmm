/*
JSWMM: Reimplementation of EPA SWMM in Java
Copyright (C) 2019 Daniele Dalla Torre (ftt01)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

import java.util.LinkedHashMap;

public class Oppo_pvc extends CommercialPipeSize {

    public Oppo_pvc() {
        pipe = new LinkedHashMap() {{
            put(153.6,160.0);
            put(192.2,200.0);
            put(240.2,250.0);
            put(302.6,315.0);
            put(384.2,400.0);
            put(480.4,500.0);
            put(605.4,630.0);
            put(1005.4,1000.0);
        }};
    }
}
