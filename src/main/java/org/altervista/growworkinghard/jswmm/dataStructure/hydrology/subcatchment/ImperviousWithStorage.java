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

import org.altervista.growworkinghard.jswmm.dataStructure.options.units.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.runoffDS.RunoffSetup;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static org.altervista.growworkinghard.jswmm.dataStructure.options.units.UnitsSWMM.CMS;

public class ImperviousWithStorage extends Subarea {

    Double totalImperviousArea;

    public ImperviousWithStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                 Double depressionStorageImpervious, Double roughnessCoefficient,
                                 ProjectUnits projectUnits) {
        this(imperviousWStorageArea, imperviousWOStorageArea, depressionStorageImpervious,
                roughnessCoefficient, null, null, projectUnits);
    }

    public ImperviousWithStorage(Double imperviousWStorageArea, Double imperviousWOStorageArea,
                                 Double depressionStorageImpervious, Double roughnessCoefficient,
                                 Double percentageRouted, List<Subarea> connections, ProjectUnits projectUnits) {

        this.subareaArea = imperviousWStorageArea;
        this.totalImperviousArea = imperviousWStorageArea + imperviousWOStorageArea;
        this.depressionStorage = depressionStorageImpervious;
        this.roughnessCoefficient = roughnessCoefficient;
        this.percentageRouted = percentageRouted;
        this.subareaConnections = connections;

        this.totalDepth = new HashMap<>();
        this.runoffDepth = new HashMap<>();
        this.flowRate = new HashMap<>();
        this.excessRainfall = new HashMap<>();

        this.projectUnits = projectUnits;
    }

    @Override
    public void setDepthFactor(Double subareaSlope, Double characteristicWidth) {
        double depthFactor = 1.0;

        if (subareaConnections != null) {
            for (Subarea connections : subareaConnections) {
                connections.setDepthFactor(subareaSlope, characteristicWidth);
                depthFactor = Math.pow(subareaSlope, 0.5) *
                        characteristicWidth / (roughnessCoefficient * totalImperviousArea);
            }
        }
        else {
            depthFactor = (Math.pow(subareaSlope, 0.5) * characteristicWidth) / (roughnessCoefficient * totalImperviousArea);
        }

        if ( projectUnits.getProjectUnits() == CMS ) {
            this.depthFactor = 3.6E-3 * depthFactor; //return the depth in [ mm/h ]
        }
    }

    @Override
    Double getWeightedFlowRate(Integer identifier, Instant currentTime) {
        return flowRate.get(identifier).get(currentTime) * subareaArea * percentageRouted;
    }

    @Override
    void evaluateNextStep(Integer id, Instant currentTime, RunoffSetup runoffSetup, Double rainfall,
                          Double evaporation, Double subareaSlope, Double characteristicWidth) {

        Long runoffStepSize = runoffSetup.getRunoffStepSize();
        Instant nextTime = currentTime.plusSeconds(runoffStepSize);

        Double moistureVolume = rainfall * runoffStepSize + totalDepth.get(id).get(currentTime);

        if(evaporation != 0.0) {
            evaporation = Math.max(evaporation, totalDepth.get(id).get(currentTime)/runoffStepSize);
        }

        setExcessRainfall(id, rainfall - evaporation);

        if(evaporation * runoffStepSize >= moistureVolume) {
            setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime));
            setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime));
            setFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime));
        }
        else {
            Double exRainHeigth = excessRainfall.get(id) * runoffStepSize;
            if ( exRainHeigth == 0.0 ) {
                if ( depressionStorage - totalDepth.get(id).get(currentTime) >= 0.0 ) {
                    setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + exRainHeigth);
                    setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime));
                    setFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime));
                }
                else {
                    setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + exRainHeigth);
                    setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime) + exRainHeigth);
                    setFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime) +
                            evaluateNextFlowRate(subareaSlope, characteristicWidth,
                                    runoffDepth.get(id).get(nextTime)));
                }
            }
            else {
                if( depressionStorage - totalDepth.get(id).get(currentTime) >= 0.0 ) {
                    setTotalDepth(id, nextTime, totalDepth.get(id).get(currentTime) + exRainHeigth);
                    setRunoffDepth(id, nextTime, runoffDepth.get(id).get(currentTime));
                    setFlowRate(id, nextTime, getFlowRate().get(id).get(currentTime));
                }
                else {
                    runoffODEsolver(id, currentTime, nextTime, getExcessRainfall(id), runoffSetup);
                    setFlowRate( id, nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth,
                            runoffDepth.get(id).get(nextTime)) );
                }
            }
        }

        /*if(evaporation * runoffStepSize >= moistureVolume) {

            System.out.println("IF1 ");

            setTotalDepth(identifier, nextTime, totalDepth.get(identifier).get(currentTime) + 0.0);
            setRunoffDepth(identifier, nextTime, runoffDepth.get(identifier).get(currentTime) + 0.0);
            setFlowRate(identifier, nextTime, getFlowRate().get(identifier).get(currentTime) + 0.0);
        }
        else {
            if(getExcessRainfall(identifier) * runoffStepSize <= depressionStorage - totalDepth.get(identifier).get(currentTime)) {

                System.out.println("IF2 ");

                setTotalDepth(identifier, nextTime, totalDepth.get(identifier).get(currentTime) +
                        getExcessRainfall(identifier) * runoffStepSize);
                setRunoffDepth(identifier, nextTime, runoffDepth.get(identifier).get(currentTime) + 0.0);
                setFlowRate(identifier, nextTime, getFlowRate().get(identifier).get(currentTime) + 0.0);
            }
            else {

                System.out.println("ELSE ");

                System.out.println("identifier " + identifier);
                System.out.println("currentTime " + currentTime);
                System.out.println("nextTime " + nextTime);
                System.out.println("getExcessRainfall(identifier) " + getExcessRainfall(identifier));

                runoffODEsolver(identifier, currentTime, nextTime, getExcessRainfall(identifier), runoffSetup);

                System.out.println("AFTER ODE ");


                setFlowRate( identifier, nextTime, evaluateNextFlowRate(subareaSlope, characteristicWidth,
                        runoffDepth.get(identifier).get(nextTime)) );
            }
        }*/
    }

    @Override
    Double evaluateNextFlowRate(Double subareaSlope, Double characteristicWidth, Double currentDepth) {
        return Math.pow(subareaSlope, 0.5) * characteristicWidth *
                Math.pow(currentDepth, 5.0/3.0) / (totalImperviousArea * roughnessCoefficient);
    }
}