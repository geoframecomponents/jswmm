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

public class PeriodStep extends Period {

    private Long stepSize;

    public PeriodStep(Instant startDate, Instant endDate, Long stepSize) {
        super(startDate, endDate);
        this.stepSize = stepSize;
    }

    public <T> void setDateTime(AvailableDateTypes type, T field) {
        switch (type) {
            case stepSize:
                this.stepSize = (Long) field;
                break;
            default:
                super.setDateTime(type, field);
        }
    }

    public <T> T getDateTime(AvailableDateTypes type) {
        switch ( type) {
            case stepSize:
                return (T) (Long) this.stepSize;
            default:
                return super.getDateTime(type);
        }
    }
}