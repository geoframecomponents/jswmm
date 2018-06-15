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

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.RaingageSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.ReceiverRunoff;
import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.RunoffSetup;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Area extends AbstractSubcatchment {

    RaingageSetup raingageSetup;
    List<ReceiverRunoff> receivers;

    //Double imperviousPercentage; //TODO evaluate from subareas
    //Double percentageImperviousWOstorage; //TODO evaluate from subareas

    Double characteristicWidth;
    Double areaSlope;
    //Double curbLength;

    HashMap<Integer, List<Subarea>> subareas;
    HashMap<Integer, LinkedHashMap<Instant, Double>> totalAreaFlowRate;

    public Area(Double subcatchmentArea, RaingageSetup raingageSetup, Double characteristicWidth, Double areaSlope,
                HashMap<Integer, List<Subarea>> subareas) {
        this.subcatchmentArea = subcatchmentArea;
        this.raingageSetup = raingageSetup;
        this.characteristicWidth = characteristicWidth;
        this.areaSlope = areaSlope;
        this.subareas = subareas;
        this.totalAreaFlowRate = new LinkedHashMap<>();
    }

    public LinkedHashMap<Instant, Double> evaluateTotalFlowRate(Integer id) {
        //check if totalarea contain the rainfallTimeId
        if (!totalAreaFlowRate.containsKey(id)) {
            totalAreaFlowRate.put(id, new LinkedHashMap<>());
        }
        //sum the volume of each subarea as product of the flowrate and the subarea's area
        for(Subarea subarea : subareas.get(id)) {


            LinkedHashMap<Instant, Double> subareaFlowRate = subarea.getFlowRate().get(id);
            for (Instant time : subareaFlowRate.keySet()) {
                Double oldFLowRate = totalAreaFlowRate.get(id).get(time);
                double value;
                if (oldFLowRate == null) {
                    value = subareaFlowRate.get(time) * subarea.subareaArea;
                } else {
                    value = oldFLowRate + subareaFlowRate.get(time) * subarea.subareaArea;
                }
                LinkedHashMap<Instant, Double> upgradedLHM = totalAreaFlowRate.get(id);
                upgradedLHM.put(time, value);
                totalAreaFlowRate.put(id, upgradedLHM);
            }
        }
        return totalAreaFlowRate.get(id);
    }

    public List<ReceiverRunoff> getReceivers() {
        return receivers;
    }

    public HashMap<Integer, List<Subarea>> getSubareas() {
        return subareas;
    }

    public void evaluateRunoffFlowRate(HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData,
                                       RunoffSetup runoffSetup, Instant currentTime) {

        for (Integer identifier : adaptedRainfallData.keySet()) {
            for (Subarea subarea : subareas.get(identifier)) {
                subarea.setDepthFactor(areaSlope, characteristicWidth);
                subarea.evaluateFlowRate(identifier, adaptedRainfallData.get(identifier).get(currentTime), 0.0,
                        currentTime, runoffSetup, areaSlope, characteristicWidth); //TODO evaporation!!
            }
        }
    }
}
