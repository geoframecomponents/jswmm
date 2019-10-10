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

import static com.github.geoframecomponents.jswmm.dataStructure.options.units.AvailableUnits.*;

enum AvailableUnits {
    CMS,
    CFS;
}

public class SWMMunits implements ProjectUnits {

    private AvailableUnits projectUnits;

    public SWMMunits(String units) {
        setProjectUnits(units);
    }

    @Override
    public void setProjectUnits(String type) {
        switch (type) {
            case "CMS":
                this.projectUnits = CMS;
            case "CFS":
                this.projectUnits = CFS;
            default:
                throw new IllegalArgumentException(type);
        }
    }

    @Override
    public String getProjectUnits() {
        if (projectUnits != null) {
            switch (projectUnits) {
                case CMS:
                    return "CMS";
                case CFS:
                    return "CFS";
                default:
                    throw new IllegalArgumentException("Wrong definition of units");
            }
        }
        else {
            throw new NullPointerException("Units not defined.");
        }
    }
}