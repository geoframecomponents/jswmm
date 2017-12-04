package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

public class ImperviousWithStorage implements SubareaSetup {

    Double imperviousWstorageArea;
    SubareaReceiver subareaReceiverRunoff;

    public ImperviousWithStorage(Double imperviousWOstorageArea,
                                    SubareaReceiver.SubareaReceiverRunoff subareaReceiverRunoff,
                                    Double percentageReceiverRunoff) {

        this.imperviousWstorageArea = imperviousWOstorageArea;
        this.subareaReceiverRunoff = new SubareaReceiver(subareaReceiverRunoff, percentageReceiverRunoff);
    }

    @Override
    public Double evaluateAlpha() {
        return null;
    }
}
