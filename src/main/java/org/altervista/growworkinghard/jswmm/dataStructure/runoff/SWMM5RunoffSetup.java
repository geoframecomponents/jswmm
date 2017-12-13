package org.altervista.growworkinghard.jswmm.dataStructure.runoff;

import java.time.Instant;

public class SWMM5RunoffSetup implements RunoffSetup {

    /**
     * Minimum step size to use for ODE integration
     */
    private Double minimumStepSize;

    /**
     * Maximum step size to use for ODE integration
     */
    private Double maximumStepSize;

    /**
     * Absolute tolerance for the error in the ODE solution
     */
    private Double absoluteTolerance;

    /**
     * Relative tolerance for the error in the ODE solution
     */
    private Double relativeTolerance;

    /**
     * Initial time for the current step of data
     */
    private Instant initialTime;

    /**
     * Final time for the current step of data
     */
    private Instant totalTime;

    private Long runoffStepSize;

    public SWMM5RunoffSetup(Double minimumStepSize, Double maximumStepSize, Double absoluteTolerance,
                            Double relativeTolerance, Instant initialTime, Instant totalTime, Long runoffStepSize) {
        this.minimumStepSize = minimumStepSize;
        this.maximumStepSize = maximumStepSize;
        this.absoluteTolerance = absoluteTolerance;
        this.relativeTolerance = relativeTolerance;
        this.initialTime = initialTime;
        this.totalTime = totalTime;
        this.runoffStepSize = runoffStepSize;
    }

    public Double getMinimumStepSize() {
        return minimumStepSize;
    }

    public Double getMaximumStepSize() {
        return maximumStepSize;
    }

    public Double getAbsoluteTolerance() {
        return absoluteTolerance;
    }

    public Double getRelativeTolerance() {
        return relativeTolerance;
    }

    public Instant getInitialTime() {
        return initialTime;
    }

    public Instant getTotalTime() {
        return totalTime;
    }

    public long getRunoffStepSize() {
        return runoffStepSize;
    }
}
