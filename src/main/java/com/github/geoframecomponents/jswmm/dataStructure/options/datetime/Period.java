package com.github.geoframecomponents.jswmm.dataStructure.options.datetime;

import java.time.Instant;

public class Period implements Datetimeable {

    private Instant startDate;
    private Instant endDate;

    public Period(Instant startDate, Instant endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public <T> void setDateTime(AvailableDateTypes type, T field) {
        switch (type) {
            case startDate:
                this.startDate = (Instant) field;
                break;
            case endDate:
                this.endDate = (Instant) field;
                break;
            default:
                throw new NullPointerException("Not a defined DateType");
        }
    }

    public <T> T getDateTime(AvailableDateTypes type) {
        switch (type) {
            case startDate:
                return (T) this.startDate;
            case endDate:
                return (T) this.endDate;
            default:
                throw new IllegalArgumentException("Not a defined DateType");
        }
    }
}