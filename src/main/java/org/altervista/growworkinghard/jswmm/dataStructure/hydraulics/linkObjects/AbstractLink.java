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

package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.routing.RoutingSetup;

import java.time.Instant;
import java.util.LinkedHashMap;

public abstract class AbstractLink {

    ProjectUnits linkUnits;

    String linkName;
    String upstreamNodeName;
    String downstreamNodeName;

    public enum LinkShape {
        CIRCURAL,
        DUMMY
    }
    LinkShape linkShape;

    Long routingStepSize;
    RoutingSetup routingMethod;
}