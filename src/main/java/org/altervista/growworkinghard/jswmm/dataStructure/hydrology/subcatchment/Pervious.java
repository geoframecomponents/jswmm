package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;


import org.altervista.growworkinghard.jswmm.runoff.*;
import java.time.Instant;
import java.util.LinkedHashMap;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Pervious implements SubareaSetup {

    Double perviousArea;
    Double perviousStorage;
    Double perviousDepthFactor;

    LinkedHashMap<Instant, Double> perviousRainfallData;
    LinkedHashMap<Instant, Double> perviousEvaporationData;
    LinkedHashMap<Instant, Double> perviousInfiltrationData;
    LinkedHashMap<Instant, Double> perviousDepth;
    LinkedHashMap<Instant, Double> perviousFlowRate;

    Double perviousExcessRainfall;

    SubareaReceiver subareaReceiverRunoff;

    public Pervious(Double perviousArea, SubareaReceiver.SubareaReceiverRunoff subareaReceiverRunoff,
                    Double percentageReceiverRunoff) {

        this.perviousArea = perviousArea;
        this.subareaReceiverRunoff = new SubareaReceiver(subareaReceiverRunoff, percentageReceiverRunoff);
    }

    @Override
    public void evaluateSubareaDepth() {

    }

    @Override
    public Double evaluateAlpha() {
        return null;
    }

    @Override
    public void upgradeFlowRateRunoff() {

    }

    @Override
    public Double getSubareaArea() {
        return perviousArea;
    }
}
