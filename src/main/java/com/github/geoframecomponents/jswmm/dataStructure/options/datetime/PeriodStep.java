package com.github.geoframecomponents.jswmm.dataStructure.options.datetime;

import java.time.Instant;

public class PeriodStep extends Period {

    private Long stepSize;

    public PeriodStep(Instant startDate, Instant endDate, Long stepSize) {
        super(startDate, endDate);
        this.stepSize = stepSize;
    }

    public <T> void setDateTime(AvailableDateTypes type, T field) {
        switch (type) {
            case stepSize:
                this.stepSize = (Long) field;
                break;
            default:
                super.setDateTime(type, field);
        }
    }

    public <T> T getDateTime(AvailableDateTypes type) {
        switch ( type) {
            case stepSize:
                return (T) (Long) this.stepSize;
            default:
                return super.getDateTime(type);
        }
    }
}