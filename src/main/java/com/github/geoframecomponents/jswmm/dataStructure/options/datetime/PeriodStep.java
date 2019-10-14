package com.github.geoframecomponents.jswmm.dataStructure.options.datetime;

import java.time.Instant;

public class PeriodStep extends Period {

    private double stepSize;

    public PeriodStep(Instant startDate, Instant endDate, double stepSize) {
        super(startDate, endDate);
        this.stepSize = stepSize;
    }

    public <T> void setDateTime(AvailableDateTypes type, T field) {
        switch (type) {
            case stepSize:
                this.stepSize = (double) field;
            default:
                super.setDateTime(type, field);
        }
    }

    public <T> T getDateTime(AvailableDateTypes type) {
        switch ( type) {
            case stepSize:
                return (T) (Double) this.stepSize;
            default:
                return super.getDateTime(type);
        }
    }
}