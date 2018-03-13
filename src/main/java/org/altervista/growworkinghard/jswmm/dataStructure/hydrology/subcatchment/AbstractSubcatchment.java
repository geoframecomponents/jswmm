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

package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.RaingageSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.AbstractReceiver;
import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

import java.time.Instant;
import java.util.LinkedHashMap;

public abstract class AbstractSubcatchment {

    ReadDataFromFile readDataFromFile;
    AcquiferSetup acquiferSetup;
    SnowPackSetup snowpack;
    ProjectUnits subcatchmentUnits;

    RaingageSetup raingageSetup;

    String subcatchmentName;
    Double subcatchmentArea;

    LinkedHashMap<Instant, Double> runoffInflow;

    public LinkedHashMap<Instant, Double> getRunoffInflow() {
        return runoffInflow;
    }
}
