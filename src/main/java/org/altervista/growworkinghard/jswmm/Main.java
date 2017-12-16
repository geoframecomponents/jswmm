package org.altervista.growworkinghard.jswmm;

import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.runoff.PreRunoff;
import org.altervista.growworkinghard.jswmm.runoff.Runoff;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SWMMobject data = new SWMMobject();
        PreRunoff preRunoff = new PreRunoff();
        preRunoff.initialize(data);
        preRunoff.run();
        Runoff runoff = new Runoff();

        runoff.initialize(preRunoff.getAdaptedRainfallData(), data);
        runoff.run();
        runoff.test();
    }
}
