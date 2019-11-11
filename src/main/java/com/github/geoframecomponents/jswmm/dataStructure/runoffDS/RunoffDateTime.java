package com.github.geoframecomponents.jswmm.dataStructure.runoffDS;

import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.PeriodStepTolerance;

import java.time.Instant;

@Deprecated
public class RunoffDateTime extends PeriodStepTolerance {

    public RunoffDateTime(Instant startDate, Instant endDate, double stepSize) {
        super(startDate, endDate, stepSize);
    }
}