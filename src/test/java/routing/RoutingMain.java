package routing;

import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.routing.Routing;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class RoutingMain {

    SWMMobject data;
    Routing routing;
    @Before
    public void initialize() throws IOException {
        data = new SWMMobject();
    }

    @Test
    public void execute() throws IOException {
        routing = new Routing();
        routing.initialize();
        routing.run();
        //routing.test();
    }
}
