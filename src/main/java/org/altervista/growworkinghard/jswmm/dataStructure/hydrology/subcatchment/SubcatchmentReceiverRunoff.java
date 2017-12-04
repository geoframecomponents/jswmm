package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

public class SubcatchmentReceiverRunoff {
    public enum ReceiverType {
        NODE,
        SUBCATCHMENT
    }

    String receiverName;
    ReceiverType receiverType;

    public SubcatchmentReceiverRunoff(ReceiverType receiverType, String receiverName) {
        this.receiverName = receiverName;
        this.receiverType = receiverType;
    }
}