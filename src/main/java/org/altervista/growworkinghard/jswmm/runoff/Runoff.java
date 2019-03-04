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

package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Area;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Subarea;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.RunoffSetup;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Runoff {

    @In
    public HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData; // [mm/hour]

    private LinkedHashMap<Instant, Double> evaporationData = null; // [mm/hour]

    /**
     * Time setup of the simulation
     */
    private Instant initialTime;

    private Instant totalTime;

    /**
     * Simulation node fields
     */
    @In
    public String areaName = null;

    @In
    public String nodeName = null;

    /**
     * Area characteristics
     */
    private Area area;

    private List<Subarea> subareas;

    private Double slopeArea;

    private Double characteristicWidth;

    /**
     * Integration method setup
     */
    private Long runoffStepSize;

    private RunoffSetup runoffSetup;

    /**
     * Data structure
     */
    @In
    public SWMMobject dataStructure;

    @OutNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> runoffFlowRate = new HashMap<>();

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        //System.out.println("Processing area " + areaName);
        if (dataStructure == null) {
            System.out.println("Data structure null");
        }
        if (dataStructure != null && areaName != null) {

            //TODO evaporation!!
            this.runoffSetup = dataStructure.getRunoffSetup();
            this.runoffStepSize = runoffSetup.getRunoffStepSize();
            TimeSetup timeSetup = dataStructure.getTimeSetup();
            this.area = dataStructure.getAreas(areaName);

            this.initialTime = timeSetup.getStartDate();
            this.totalTime = timeSetup.getEndDate();
        }
        else {
            throw new NullPointerException("Nothing implemented yet");
        }

        /*for (Map.Entry<Integer, LinkedHashMap<Instant, Double>> entry : adaptedRainfallData.entrySet()) {
            LinkedHashMap<Instant, Double> val = entry.getValue();
            for (Instant time : val.keySet() ) {
                System.out.print(time);
                System.out.println(val.get(time) * 3600.0);
            }
        }*/

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            //check snownelt - snowaccumulation TODO build a new component
            area.evaluateRunoffFlowRate(adaptedRainfallData, runoffSetup, currentTime);
            currentTime = currentTime.plusSeconds(runoffStepSize);
        }

        for (Integer identifier : adaptedRainfallData.keySet()) {
            runoffFlowRate.put(identifier, area.evaluateTotalFlowRate(identifier)); //[m^3/s]
        }

        /*for (Map.Entry<Integer, LinkedHashMap<Instant, Double>> entry : runoffFlowRate.entrySet()) {
            LinkedHashMap<Instant, Double> val = entry.getValue();
            for (Instant time : val.keySet() ) {
                //System.out.print("Time " + time);
                System.out.println(val.get(time));
            }
        }*/
    }


    /*public void test(String fileChecks) {
        LinkedHashMap<Instant, Double> evaluated = area.getTotalAreaFlowRate();
        List<Double> defined = dataStructure.readFileList(fileChecks);

        int i = 0;
        for(Map.Entry<Instant, Double> data : evaluated.entrySet()) {
            //TODO check a method to do it better - not always is ordered
            assertEquals(data.getValue(), defined.get(i), 0.85);
            //System.out.println(data.getValue());
            //System.out.println(defined.get(i));
            i = i + 1;
        }
    }*/
}
