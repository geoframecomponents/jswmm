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

public abstract class PeriodStepTolerance extends PeriodStep {

    private double minStep;
    private double maxStep;
    private double absoluteTolerance;
    private double relativeTolerance;

    public PeriodStepTolerance(Instant startDate, Instant endDate, Long stepSize) {
        super(startDate, endDate, stepSize);
    }

    public <T> void setDateTime(AvailableDateTypes type, T field) {
        switch (type) {
            case minStep:
                this.minStep = (Double) field;
                break;
            case maxStep:
                this.maxStep = (Double) field;
                break;
            case absoluteTolerance:
                this.absoluteTolerance = (Double) field;
                break;
            case relativeTolerance:
                this.relativeTolerance = (Double) field;
                break;
            default:
                super.setDateTime(type, field);
        }
    }

    public <T> T getDateTime(AvailableDateTypes type) {
        switch ( type) {
            case minStep:
                return (T) (Double) this.minStep;
            case maxStep:
                return (T) (Double) this.maxStep;
            case absoluteTolerance:
                return (T) (Double) this.absoluteTolerance;
            case relativeTolerance:
                return (T) (Double) this.relativeTolerance;
            default:
                return super.getDateTime(type);
        }
    }
}