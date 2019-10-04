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

package com.github.geoframecomponents.jswmm.runoff;

import com.github.geoframecomponents.jswmm.dataStructure.SWMMobject;
import com.github.geoframecomponents.jswmm.dataStructure.hydrology.subcatchment.Area;
import oms3.annotations.*;
import com.github.geoframecomponents.jswmm.dataStructure.options.time.ProjectTime;
import com.github.geoframecomponents.jswmm.dataStructure.runoffDS.AbstractRunoffSolver;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class Runoff {

    @In
    public HashMap<Integer, LinkedHashMap<Instant, Double>> adaptedRainfallData; // [mm/hour]

    private LinkedHashMap<Instant, Double> adaptedEvaporationData = null; // [mm/hour]

    /**
     * Start date/time of the runoff simulation
     */
    private Instant initialTime;

    /**
     * End date/time of the runoff simulation
     */
    private Instant totalTime;

    /**
     * Name of the area of current simulation
     */
    @In
    public String areaName = null;

    /**
     * Name of the node where area drains in the current simulation
     */
    @In
    public String nodeName = null;

    /**
     * Area properties
     */
    private Area area;
    //private List<Subarea> subareas;
    //private Double slopeArea;
    //private Double characteristicWidth;

    /**
     * Integration method setup
     */
    private Long runoffStepSize;

    private AbstractRunoffSolver runoffSolver;

    /**
     * Data structure
     */
    @In
    public SWMMobject dataStructure;

    /**
     * HM of the flowrate over time that drains into the node,
     * ID of the rainfall curve is the key and the resulting runoff are the values
     */
    @OutNode
    public HashMap<Integer, LinkedHashMap<Instant, Double>> runoffFlowRate = new HashMap<>();

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        //System.out.println("Processing area " + areaName);
        if (dataStructure == null) {
            System.out.println("Data structure is null");
        }
        if (dataStructure != null && areaName != null) {

            //TODO add evaporation
            this.runoffSolver = dataStructure.getRunoffSolver();
            this.runoffStepSize = runoffSolver.getRunoffStepSize();
            ProjectTime projectTime = dataStructure.getProjectTime();
            this.area = dataStructure.getAreas(areaName);

            this.initialTime = projectTime.getStartDate();
            this.totalTime = projectTime.getEndDate();
        }
        else {
            throw new NullPointerException("Runoff over" + areaName + "fails setup.");
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
            area.evaluateRunoffFlowRate(adaptedRainfallData, runoffSolver, currentTime);
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
