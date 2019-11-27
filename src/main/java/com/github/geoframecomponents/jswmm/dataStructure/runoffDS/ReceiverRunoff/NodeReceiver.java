/*
 * JSWMM: Reimplementation of EPA SWMM in Java
 * Copyright (C) 2019 Daniele Dalla Torre (ftt01)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.geoframecomponents.jswmm.dataStructure.runoffDS.ReceiverRunoff;

import com.github.geoframecomponents.jswmm.dataStructure.hydraulics.nodeObject.AbstractNode;
import com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment.AbstractSubcatchment;

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
        throw new NullPointerException("Nothing implemented yet");

    }

    @Override
    public AbstractSubcatchment getReceiverObject() {
        throw new NullPointerException("Nothing implemented yet");

    }

    @Override
    public Double getPercentage() {
        throw new NullPointerException("Nothing implemented yet");

    }
}

