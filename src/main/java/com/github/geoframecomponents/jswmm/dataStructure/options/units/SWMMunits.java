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
package com.github.geoframecomponents.jswmm.dataStructure.options.units;

public class SWMMunits implements Unitable {

    private AvailableUnits projectUnits;

    public SWMMunits(AvailableUnits projectUnits) {
        setUnits(projectUnits);
    }

    public SWMMunits(String projectUnits) {
        setUnits(projectUnits);
    }

    @Override
    public <T> void setUnits(T units) {
        if (units instanceof AvailableUnits) {
            setUnits((AvailableUnits) units);
        }
        else if (units instanceof String) {
            setUnits((String) units);
        }
        else {
            throw new IllegalArgumentException("Not a recognized units");
        }

    }

    public void setUnits(AvailableUnits units) {
        switch (units) {
            case CMS:
                this.projectUnits = AvailableUnits.CMS;
                break;
            case CFS:
                this.projectUnits = AvailableUnits.CFS;
                break;
            default:
                throw new IllegalArgumentException("Not a recognized units");
        }
    }

    public void setUnits(String units) {
        switch (units) {
            case "CMS":
                this.projectUnits = AvailableUnits.CMS;
                break;
            case "CFS":
                this.projectUnits = AvailableUnits.CFS;
                break;
            default:
                throw new IllegalArgumentException("Not a recognized units");
        }
    }

    @Override
    public AvailableUnits getUnits() {
        if (projectUnits != null) {
            switch (projectUnits) {
                case CMS:
                    return AvailableUnits.CMS;
                case CFS:
                    return AvailableUnits.CFS;
                default:
                    throw new IllegalArgumentException("Wrong definition of units");
            }
        } else {
            throw new NullPointerException("Units not defined.");
        }
    }

}