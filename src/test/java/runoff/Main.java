package runoff;

import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.runoff.PreRunoff;
import org.altervista.growworkinghard.jswmm.runoff.Runoff;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

public class Main {

    SWMMobject data;
    PreRunoff preRunoff_1;
    PreRunoff preRunoff_2;
    Runoff runoff_1;
    Runoff runoff_2;

    @Before
    public void initialize() throws IOException {
        data = new SWMMobject();

        preRunoff_1 = new PreRunoff();
        preRunoff_2 = new PreRunoff();

        preRunoff_1.dataStructure = data;
        preRunoff_2.dataStructure = data;

        preRunoff_1.areaName = "RG1";
        preRunoff_2.areaName = "RG2";

        preRunoff_1.initialize();
        preRunoff_2.initialize();

        preRunoff_1.run();
        preRunoff_2.run();
    }

    @Test
    public void execute() throws IOException {
        runoff_1 = new Runoff();
        runoff_2 = new Runoff();

        runoff_1.dataStructure = data;
        runoff_1.areaName = "Sub1";
        runoff_1.nodeName = "N1";
        runoff_1.adaptedRainfallData = preRunoff_1.getAdaptedRainfallData();

        runoff_2.dataStructure = data;
        runoff_2.areaName = "Sub2";
        runoff_2.nodeName = "N2";
        runoff_2.adaptedRainfallData = preRunoff_2.getAdaptedRainfallData();

        runoff_1.initialize();
        runoff_2.initialize();

        runoff_1.run();
        runoff_2.run();

        runoff_1.test();
        //runoff_2.test();

        System.out.println(data.getAreas().get("Sub1").getTotalAreaFlowRate().get(Instant.parse("2000-01-01T05:00:00Z")));
        System.out.println(data.getAreas().get("Sub2").getTotalAreaFlowRate().get(Instant.parse("2000-01-01T05:00:00Z")));

    }
}