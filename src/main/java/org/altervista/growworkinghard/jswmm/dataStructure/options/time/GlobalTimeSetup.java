package org.altervista.growworkinghard.jswmm.dataStructure.options.time;

import java.time.Instant;

public class GlobalTimeSetup implements TimeSetup {

    Instant startDate;
    Instant endDate;
    Instant reportStartDate;
    Instant reportEndDate;
    Instant sweepStart;
    Instant sweepEnd;
    Integer dryDays;

    public GlobalTimeSetup(Instant startDate, Instant endDate, Instant reportStartDate, Instant reportEndDate,
                           Instant sweepStart, Instant sweepEnd, Integer dryDays) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.reportStartDate = reportStartDate;
        this.reportEndDate = reportEndDate;
        this.sweepStart = sweepStart;
        this.sweepEnd = sweepEnd;
        this.dryDays = dryDays;
    }
}
