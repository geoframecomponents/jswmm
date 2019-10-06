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

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Instant getReportStartDate() {
        return reportStartDate;
    }

    public Instant getReportEndDate() { return reportEndDate; }

    public Instant getSweepStart() {
        return sweepStart;
    }

    public Instant getSweepEnd() {
        return sweepEnd;
    }

    public Integer getDryDays() {
        return dryDays;
    }

    @Override
    public void setProjectTime() {
    }

    @Override
    public Instant getProjectTime(String type) {
        switch (type) {
            case "initial":
                return this.startDate;
            case "final":
                return this.endDate;
            default:
                throw new IllegalArgumentException(type);
        }
    }
}
