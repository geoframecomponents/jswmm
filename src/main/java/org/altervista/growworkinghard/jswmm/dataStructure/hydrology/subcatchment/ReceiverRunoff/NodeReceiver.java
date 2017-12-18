package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff;

import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject.AbstractNode;

public class NodeReceiver implements ReceiverRunoff {
    ReceiverType receiverType;
    AbstractNode receiverObject;
    Double percentage;

    public NodeReceiver(AbstractNode node, Double percentage) {
        this.receiverObject = node;
        this.percentage = percentage;
        this.receiverType = ReceiverType.NODE;
    }

    @Override
    public ReceiverType getReceiverType() {
        return receiverType;
    }

    @Override
    public AbstractNode getReceiverObject() {
        return receiverObject;
    }

    @Override
    public Double getPercentage() {
        return percentage;
    }
}

