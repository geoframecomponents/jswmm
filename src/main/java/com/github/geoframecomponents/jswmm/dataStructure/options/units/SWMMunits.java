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

package com.github.geoframecomponents.jswmm.dataStructure.options.units;

/**
 * Implementation of SWMM unit measurements convention CMS
 */

public class CubicMetersSecond implements ProjectUnits {

    private UnitsSWMM projectUnits;

    public CubicMetersSecond() {
        this.projectUnits = UnitsSWMM.CMS;
    }

    @Override
    public void setProjectUnits() {
        this.projectUnits = UnitsSWMM.CMS;
    }

    @Override
    public UnitsSWMM getProjectUnits() {
        if (projectUnits != null) {
            return projectUnits;
        }
        else{
            throw new NullPointerException("System units not defined");
        }
    }
}
