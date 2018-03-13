package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff;

import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.AbstractSubcatchment;

public class SubcatchmentReceiver implements ReceiverRunoff {
    ReceiverType receiverType;
    AbstractSubcatchment receiverObject;
    Double percentage;

    public SubcatchmentReceiver(AbstractSubcatchment area, Double percentage) {
        this.receiverObject = area;
        this.percentage = percentage;
        this.receiverType = ReceiverType.SUBCATCHMENT;
    }

    @Override
    public ReceiverType getReceiverType() {
        return receiverType;
    }

    @Override
    public AbstractSubcatchment getReceiverObject() {
        return receiverObject;
    }

    @Override
    public Double getPercentage() {
        return percentage;
    }
}
