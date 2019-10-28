/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.geoframecomponents.jswmm.dataStructure.hydraulics.nodeObject;

import org.altervista.growworkinghard.jswmm.inpparser.objects.DividerINP;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Divider extends AbstractNode {

    public Divider(String name, String INPfile) throws ConfigurationException {
        super(name);
        interfaceINP = new DividerINP(INPfile);
    }

    @Override
    public void sumFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> newFlowRate) {
        throw new NullPointerException("Nothing implemented yet");
    }

    @Override
    public HashMap<Integer, LinkedHashMap<Instant, Double>> getFlowRate() {
        throw new NullPointerException("Nothing implemented yet");
    }
}
