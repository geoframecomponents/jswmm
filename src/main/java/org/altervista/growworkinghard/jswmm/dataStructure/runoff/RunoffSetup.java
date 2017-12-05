package org.altervista.growworkinghard.jswmm.dataStructure.runoff;

import java.time.Instant;

public interface RunoffSetup {

    public Double getMinimumStepSize();

    public Double getMaximumStepSize();

    public Double getAbsoluteTolerance();

    public Double getRelativeTolerance();

    public Instant getInitialTime();

    public Instant getFinalTime();

    public long getRunoffStepSize();
}
