package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff;

import java.time.Instant;
import java.util.LinkedHashMap;

public abstract class AbstractReceiver {
    public abstract LinkedHashMap<Instant, Double> getRunoffInflow();
}
