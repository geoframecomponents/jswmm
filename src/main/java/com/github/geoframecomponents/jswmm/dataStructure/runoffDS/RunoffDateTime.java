package com.github.geoframecomponents.jswmm.dataStructure.runoffDS;

import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.PeriodStepTolerance;

import java.time.Instant;

public class RunoffDateTime extends PeriodStepTolerance {

    public RunoffDateTime(Instant startDate, Instant endDate, double stepSize,
                          double minStep, double maxStep, double absTol, double relTol) {
        super(startDate, endDate, stepSize);
        this.setDateTime(AvailableDateTypes.minStep, minStep);
        this.setDateTime(AvailableDateTypes.maxStep, maxStep);
        this.setDateTime(AvailableDateTypes.absoluteTolerance, absTol);
        this.setDateTime(AvailableDateTypes.relativeTolerance, relTol);
    }
}