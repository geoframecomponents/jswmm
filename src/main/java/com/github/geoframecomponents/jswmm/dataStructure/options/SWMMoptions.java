package com.github.geoframecomponents.jswmm.dataStructure.options;

import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.units.AvailableUnits;

import java.time.Instant;

public class SWMMoptions extends AbstractOptions {

    private Instant startDate;
    private Instant endDate;
    private Instant reportStartDate;
    private Instant reportEndDate;
    private Instant sweepStart;
    private Instant sweepEnd;
    private Integer dryDays;

    private AvailableUnits projectUnits;

    @Override
    public <T> void setDateTime(AvailableDateTypes type, T field) {
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
    public <T> T getDateTime(AvailableDateTypes type) {
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

    @Override
    public <T> T getUnits() {
        if (projectUnits != null) {
            switch (projectUnits) {
                case CMS:
                    return (T) AvailableUnits.CMS;
                case CFS:
                    return (T) AvailableUnits.CFS;
                default:
                    throw new IllegalArgumentException("Wrong definition of units");
            }
        }
        else {
            throw new NullPointerException("Units not defined.");
        }
    }

    @Override
    public <T> void setUnits(AvailableUnits units) {

        switch (units) {
            case CMS:
                this.projectUnits = AvailableUnits.CMS;
            case CFS:
                this.projectUnits = AvailableUnits.CFS;
            default:
                throw new IllegalArgumentException("Not a recognized units");
        }

    }
}