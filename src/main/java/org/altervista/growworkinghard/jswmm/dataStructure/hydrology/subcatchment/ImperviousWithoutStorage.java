package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

public class ImperviousWithoutStorage implements SubareaSetup {

    Double imperviousWOstorageArea;
    SubareaReceiver subareaReceiverRunoff;

    public ImperviousWithoutStorage(Double imperviousWOstorageArea,
                                    SubareaReceiver.SubareaReceiverRunoff subareaReceiverRunoff,
                                    Double percentageReceiverRunoff) {

        this.imperviousWOstorageArea = imperviousWOstorageArea;
        this.subareaReceiverRunoff = new SubareaReceiver(subareaReceiverRunoff, percentageReceiverRunoff);
    }

    @Override
    public Double evaluateAlpha() {
        return null;
    }
}
