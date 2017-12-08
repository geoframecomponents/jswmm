package org.altervista.growworkinghard.jswmm.dataStructure.options.time;

import java.time.Instant;

public interface TimeSetup {

    public Instant getStartDate();

    public Instant getEndDate();

    public Instant getReportStartDate();

    public Instant getReportEndDate();

    public Instant getSweepStart();

    public Instant getSweepEnd();

    public Integer getDryDays();
}
