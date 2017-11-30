package org.altervista.growworkinghard.jswmm.dataStructure.runoff;

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
    Double initialTime;

    /**
     * Final time for the current step of data
     */
    Double finalTime;

}
