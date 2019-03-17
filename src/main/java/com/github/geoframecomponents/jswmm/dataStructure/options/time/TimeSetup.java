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

public interface TimeSetup {

    public Instant getStartDate();

    public Instant getEndDate();

    public Instant getReportStartDate();

    public Instant getReportEndDate();

    public Instant getSweepStart();

    public Instant getSweepEnd();

    public Integer getDryDays();
}
