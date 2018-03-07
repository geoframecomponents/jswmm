package routing;

import org.junit.Before;
import org.junit.Test;

public class RoutingMain {

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