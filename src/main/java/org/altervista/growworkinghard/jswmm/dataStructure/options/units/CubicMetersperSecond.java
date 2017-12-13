package org.altervista.growworkinghard.jswmm.dataStructure.options.units;

public class CubicMetersperSecond implements ProjectUnits {
    //TODO implement a method to check if is equal for the full SWMMobj

    private UnitsSWMM projectUnits;

    public CubicMetersperSecond() {
        this.projectUnits = UnitsSWMM.CMS;
    }

    @Override
    public void setProjectUnits() {
        this.projectUnits = UnitsSWMM.CMS;
    }

    @Override
    public UnitsSWMM getProjectUnits() {
        return projectUnits;
    }
}
