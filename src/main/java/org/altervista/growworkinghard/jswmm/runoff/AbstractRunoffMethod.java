package org.altervista.growworkinghard.jswmm.runoff;

abstract class AbstractRunoffMethod {
    abstract double[] integrate(Double initialTime,double[] inputValues,
                                Double finalTime, double[] outputValues);
}