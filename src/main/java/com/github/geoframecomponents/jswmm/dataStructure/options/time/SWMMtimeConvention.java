/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.geoframecomponents.jswmm.dataStructure.options.time;

import java.time.Instant;

public class SWMMtimeConvention implements ProjectTime {

    private Instant startDate;
    private Instant endDate;
    private Instant reportStartDate;
    private Instant reportEndDate;
    private Instant sweepStart;
    private Instant sweepEnd;
    private Integer dryDays;

    public SWMMtimeConvention(Instant startDate, Instant endDate, Instant reportStartDate, Instant reportEndDate,
                              Instant sweepStart, Instant sweepEnd, Integer dryDays) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.reportStartDate = reportStartDate;
        this.reportEndDate = reportEndDate;
        this.sweepStart = sweepStart;
        this.sweepEnd = sweepEnd;
        this.dryDays = dryDays;
    }

    public <T> void setProjectTime(DateTypes type, T field) {
        switch (type) {
            case startSimDate:
                this.startDate = (Instant) field;
            case endSimDate:
                this.endDate = (Instant) field;
            case rptStartDate:
                this.reportStartDate = (Instant) field;
            case rptEndDate:
                this.reportEndDate = (Instant) field;
            case sweepStartDate:
                this.sweepStart = (Instant) field;
            case sweepEndDate:
                this.sweepEnd = (Instant) field;
            case nmbOfDryDays:
                assert field instanceof Integer;
                this.dryDays = (Integer) field;
            default:
                throw new NullPointerException("Not a defined DataType");
        }
    }

    @Override
    public <T> T getProjectTime(DateTypes type) {
        switch (type) {
            case startSimDate:
                return (T) this.startDate;
            case endSimDate:
                return (T) this.endDate;
            case rptStartDate:
                return (T) this.reportStartDate;
            case rptEndDate:
                return (T) this.reportEndDate;
            case sweepStartDate:
                return (T) this.sweepStart;
            case sweepEndDate:
                return (T) this.sweepEnd;
            case nmbOfDryDays:
                return (T) this.dryDays;
            default:
                throw new IllegalArgumentException("Not a defined DataType");
        }
    }
}