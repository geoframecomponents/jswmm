package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

public class SubareaReceiver {

    public enum SubareaReceiverRunoff {
        OUTLET,
        PERVIOUS,
        IMPERVIOUS
    }
    SubareaReceiverRunoff subareaReceiverRunoff;

    Double percentageSubareaReceiver;

    public SubareaReceiver(SubareaReceiverRunoff subareaReceiverRunoff, Double percentageSubareaReceiver) {
        this.subareaReceiverRunoff = subareaReceiverRunoff;
        this.percentageSubareaReceiver = percentageSubareaReceiver;
    }
}
