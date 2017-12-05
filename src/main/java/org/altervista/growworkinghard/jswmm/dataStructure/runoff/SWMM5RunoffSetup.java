package org.altervista.growworkinghard.jswmm.dataStructure.runoff;

import java.time.Instant;

public class SWMM5RunoffSetup implements RunoffSetup {

    /**
     * Minimum step size to use for ODE integration
     */
    Double minimumStepSize;

    /**
     * Maximum step size to use for ODE integration
     */
    Double maximumStepSize;

    /**
     * Absolute tolerance for the error in the ODE solution
     */
    Double absoluteTolerance;

    /**
     * Relative tolerance for the error in the ODE solution
     */
    Double relativeTolerance;

    /**
     * Initial time for the current step of data
     */
    Instant initialTime;

    /**
     * Final time for the current step of data
     */
    Instant finalTime;


    long runoffStepSize;

    public SWMM5RunoffSetup(Double minimumStepSize, Double maximumStepSize, Double absoluteTolerance,
                            Double relativeTolerance, Instant initialTime, Instant finalTime, long runoffStepSize) {
        this.minimumStepSize = minimumStepSize;
        this.maximumStepSize = maximumStepSize;
        this.absoluteTolerance = absoluteTolerance;
        this.relativeTolerance = relativeTolerance;
        this.initialTime = initialTime;
        this.finalTime = finalTime;
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

    public Instant getFinalTime() {
        return finalTime;
    }

    public long getRunoffStepSize() {
        return runoffStepSize;
    }
}
