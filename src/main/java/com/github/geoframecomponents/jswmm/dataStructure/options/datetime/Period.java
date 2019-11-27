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
package com.github.geoframecomponents.jswmm.dataStructure.options.datetime;

import java.time.Instant;

public class Period implements Datetimeable {

    private Instant startDate;
    private Instant endDate;

    public Period(Instant startDate, Instant endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public <T> void setDateTime(AvailableDateTypes type, T field) {
        switch (type) {
            case startDate:
                this.startDate = (Instant) field;
                break;
            case endDate:
                this.endDate = (Instant) field;
                break;
            default:
                throw new NullPointerException("Not a defined DateType");
        }
    }

    public <T> T getDateTime(AvailableDateTypes type) {
        switch (type) {
            case startDate:
                return (T) this.startDate;
            case endDate:
                return (T) this.endDate;
            default:
                throw new IllegalArgumentException("Not a defined DateType");
        }
    }
}