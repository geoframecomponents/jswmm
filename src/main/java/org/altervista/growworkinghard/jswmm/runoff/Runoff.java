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
import org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.nodeObject.Junction;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Area;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.ReceiverRunoff.ReceiverRunoff;
import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment.Subarea;
import org.altervista.growworkinghard.jswmm.dataStructure.options.time.TimeSetup;
import org.altervista.growworkinghard.jswmm.dataStructure.runoff.RunoffSetup;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Runoff {

    @In
    public LinkedHashMap<Instant, Double> adaptedRainfallData;

    private LinkedHashMap<Instant, Double> evaporationData = null;

    /**
     * Time setup of the simulation
     */
    private Instant initialTime;

    private Instant totalTime;

    /**
     * Area setup
     */

    private Area area;

    @In
    public String areaName = null;

    private Junction node;

    @In
    public String nodeName = null;

    private List<Subarea> subareas;

    private Double slopeArea;

    private Double characteristicWidth;

    /**
     * Integration method setup
     */
    private FirstOrderIntegrator firstOrderIntegrator;

    private Long runoffStepSize;

    /**
     * Data structure
     */
    @In
    @Out
    public SWMMobject dataStructure;

    private RunoffSetup runoffSetup;

    private List<ReceiverRunoff> receivers;

    @Initialize
    public void initialize() {
    }

    @Execute
    public void run() {

        if (dataStructure != null && areaName != null) {

            //this.dataStructure = dataStructure;
            //TODO evaporation!!
            this.runoffSetup = dataStructure.getRunoffSetup();
            TimeSetup timeSetup = dataStructure.getTimeSetup();
            this.area = dataStructure.getAreas().get(areaName);
            //TODO IF node is not Juntions????
            //this.node = dataStructure.getJunctions().get(nodeName);

            //this.areaName = runoffSetup.getAreaName();
            this.initialTime = timeSetup.getStartDate();
            this.totalTime = timeSetup.getEndDate();
            this.runoffStepSize = runoffSetup.getRunoffStepSize();

            this.subareas = area.getSubareas();
            this.slopeArea = area.getAreaSlope();
            this.characteristicWidth = area.getCharacteristicWidth();
            //this.adaptedRainfallData = tempRainfall;
        } else {
            throw new NullPointerException("");//TODO
	}

        Instant currentTime = initialTime;
        while (currentTime.isBefore(totalTime)) {

            //check snownelt - snowaccumulation TODO build a new component
            upgradeStepValues(currentTime);

            currentTime = currentTime.plusSeconds(runoffStepSize);
        }
        area.evaluateTotalFlowRate(); //TODO to be verified
        dataStructure.setJunctionFlowRate(nodeName, areaName);
        //node.addRunoffFlowRate(area.getTotalAreaFlowRate());
        //System.out.println(area.getTotalAreaFlowRate().get(Instant.parse("2018-01-01T00:02:00Z")));
    }

    private void upgradeStepValues(Instant currentTime) {
        LinkedHashMap<Instant, Double> ad = new LinkedHashMap<>(adaptedRainfallData);
        for (Subarea subarea : subareas) {
            System.out.println("Before " + areaName);
            subarea.setDepthFactor(slopeArea, characteristicWidth);
            System.out.println("Depth factor done " + areaName);
            subarea.evaluateFlowRate(ad.get(currentTime), 0.0, currentTime, //TODO evaporation!!
                    runoffSetup, slopeArea, characteristicWidth);
            System.out.println("Flow rate done " + areaName);
        }
    }

    @Finalize
    public void upgradeNodeFlowRate() {
    }

    private List<Double> testingValues(String fileName) {
        String line;

        List<Double> testingValues = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                testingValues.add(Double.parseDouble(line));
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" +
                fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '"
                + fileName + "'");
        }

        return testingValues;
    }

    public void test(String fileChecks) {
        LinkedHashMap<Instant, Double> evaluated = area.getTotalAreaFlowRate();
        List<Double> defined = testingValues(fileChecks);

        int i = 0;
        for(Map.Entry<Instant, Double> data : evaluated.entrySet()) {
            //TODO check a method to do it better - not always is ordered
            assertEquals(data.getValue(), defined.get(i), 0.85);
            //System.out.println(data.getValue());
            //System.out.println(defined.get(i));
            i = i + 1;
        }
    }
}
