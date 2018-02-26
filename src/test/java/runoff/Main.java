package runoff;

import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.runoff.PreRunoff;
import org.altervista.growworkinghard.jswmm.runoff.Runoff;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class Main {

    SWMMobject data;
    PreRunoff preRunoff;
    Runoff runoff;

    @Before
    public void initialize() throws IOException {
        data = new SWMMobject();
        preRunoff = new PreRunoff();
        preRunoff.initialize(data);
        preRunoff.run();
    }

    @Test
    public void execute() throws IOException {
        runoff = new Runoff();
        runoff.initialize(preRunoff.getAdaptedRainfallData(), data);
        runoff.run();
        runoff.test();
    }
}
