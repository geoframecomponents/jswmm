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

package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject;

import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.writeData.WriteDataToFile;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.AbstractReceiver;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;

import java.time.Instant;
import java.util.LinkedHashMap;

public abstract class AbstractNode {

    ReadDataFromFile readDataFromFile;
    WriteDataToFile writeDataToFile;
    ExternalInflow dryWeatherInflow;
    ExternalInflow rainfallDependentInfiltrationInflow;

    ProjectUnits nodeUnits;

    String nodeName;
    Double nodeElevation;
    LinkedHashMap<Instant, Double> runoffInflow;
    LinkedHashMap<Instant, Double> routingInflow;

    LinkedHashMap<Instant, Double> nodeFlowRate;
    LinkedHashMap<Instant, Double> nodeDepth;

    public LinkedHashMap<Instant, Double> getRunoffInflow() {
        return runoffInflow;
    }

    public abstract void addRoutingFlowRate(LinkedHashMap<Instant, Double> newFlowRate);

    public abstract LinkedHashMap<Instant, Double> getNodeFlowRate();
}


