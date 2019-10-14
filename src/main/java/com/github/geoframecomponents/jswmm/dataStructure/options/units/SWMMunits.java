package com.github.geoframecomponents.jswmm.dataStructure.options.units;

public class SWMMunits implements Unitable {

    private AvailableUnits projectUnits;

    public SWMMunits(AvailableUnits projectUnits) {
        setUnits((AvailableUnits) projectUnits);
    }

    public SWMMunits(String projectUnits) {
        setUnits((String) projectUnits);
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
            case CFS:
                this.projectUnits = AvailableUnits.CFS;
            default:
                throw new IllegalArgumentException("Not a recognized units");
        }
    }

    public void setUnits(String units) {
        switch (units) {
            case "CMS":
                this.projectUnits = AvailableUnits.CMS;
            case "CFS":
                this.projectUnits = AvailableUnits.CFS;
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