package com.github.geoframecomponents.jswmm.dataStructure.options.datetime;

import java.time.Instant;

public abstract class PeriodStepTolerance extends PeriodStep {

    private double minStep;
    private double maxStep;
    private double absoluteTolerance;
    private double relativeTolerance;

    public PeriodStepTolerance(Instant startDate, Instant endDate, double stepSize) {
        super(startDate, endDate, stepSize);
    }

    public <T> void setDateTime(AvailableDateTypes type, T field) {
        switch (type) {
            case minStep:
                this.minStep = (Double) field;
                break;
            case maxStep:
                this.maxStep = (Double) field;
                break;
            case absoluteTolerance:
                this.absoluteTolerance = (Double) field;
                break;
            case relativeTolerance:
                this.relativeTolerance = (Double) field;
                break;
            default:
                super.setDateTime(type, field);
        }
    }

    public <T> T getDateTime(AvailableDateTypes type) {
        switch ( type) {
            case minStep:
                return (T) (Double) this.minStep;
            case maxStep:
                return (T) (Double) this.maxStep;
            case absoluteTolerance:
                return (T) (Double) this.absoluteTolerance;
            case relativeTolerance:
                return (T) (Double) this.relativeTolerance;
            default:
                return super.getDateTime(type);
        }
    }
}