package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import java.time.Instant;
import java.util.LinkedHashMap;

public interface SubareaSetup {


    void evaluateSubareaDepth();
    Double evaluateAlpha();
    void upgradeFlowRateRunoff();
    Double getSubareaArea();

}
