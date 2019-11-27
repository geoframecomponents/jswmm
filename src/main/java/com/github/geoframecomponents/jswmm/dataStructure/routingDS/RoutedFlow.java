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
package com.github.geoframecomponents.jswmm.dataStructure.routingDS;

import java.time.Instant;

public class RoutedFlow {
    Instant time;
    double value;

    public RoutedFlow(Instant time, double value) {
        this.time = time;
        this.value = value;
    }

    public Instant getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
