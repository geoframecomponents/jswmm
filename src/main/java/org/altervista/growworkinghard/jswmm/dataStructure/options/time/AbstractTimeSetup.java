package org.altervista.growworkinghard.jswmm.dataStructure.options.time;

import org.altervista.growworkinghard.jswmm.dataStructure.options.time.StepSizeType;

import java.time.Instant;

public class AbstractTimeSetup {

    StepSizeType runoffStepSize;
    StepSizeType routingStepSize;

    Instant startDate;
    Instant endDate;
    Instant reportStartDate;
    Instant reportEndDate;
    Instant sweepStart;
    Instant sweepEnd;
    Integer dryDays;
}
