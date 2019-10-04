package com.github.geoframecomponents.jswmm.dataStructure;

import com.github.geoframecomponents.jswmm.dataStructure.options.units.ProjectUnits;

public abstract class Unitable {
    ProjectUnits units;

    public Unitable(ProjectUnits units) {
        this.units = units;
    }

    public ProjectUnits getUnits() {
        return units;
    }

    public void setUnits(ProjectUnits units) {
        this.units = units;
    }
}
