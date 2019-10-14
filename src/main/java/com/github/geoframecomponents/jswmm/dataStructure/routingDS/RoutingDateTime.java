package com.github.geoframecomponents.jswmm.dataStructure.routingDS;

import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.AvailableDateTypes;
import com.github.geoframecomponents.jswmm.dataStructure.options.datetime.PeriodStepTolerance;

import java.time.Instant;

public class RoutingDateTime extends PeriodStepTolerance {

    public RoutingDateTime(Instant startDate, Instant endDate, double stepSize, double absTol) {
        super(startDate, endDate, stepSize);
        setDateTime(AvailableDateTypes.absoluteTolerance, absTol);
    }

}