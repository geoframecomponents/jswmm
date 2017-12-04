package org.altervista.growworkinghard.jswmm.runoff;

import oms3.annotations.*;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.LinkedHashMap;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Runoff {

    @In
    LinkedHashMap<Instant, Double> adaptedRainfallData;

    @In
    LinkedHashMap<Instant, Double> adaptedInfiltrationData;

    //@In
    //@Out
    //SWMMobject dataStructure;

    Instant initialTime;
    Instant totalTime;
    long runoffStepSize;

    //long initialTime = initialTime.toEpochMilli();
    //long totalTime = totalTime.toEpochMilli();
    //long runoffStepSize = runoffStepSize.getEpochSecond();

    Double imperviousPercentage;
    Double imperviousDepressionStorage;
    LinkedHashMap<Instant, Double> imperviousWOstorageDepth;
    LinkedHashMap<Instant, Double> imperviousWstorageDepth;
    LinkedHashMap<Instant, Double> perviousDepth;

    //Subarea perviousSubarea;
    //Subarea imperviousWstorageSubarea;
    //Subarea imperviousWOstorageSubarea;

    String perviousRouteTo = "OUTLET";
    String imperviousWstorageRouteTo = "OUTLET";
    String imperviousWOstorageRouteTo = "OUTLET";

    LinkedHashMap<Instant, Double> perviousRainfallData;
    LinkedHashMap<Instant, Double> imperviousWOstorageRainfallData;
    LinkedHashMap<Instant, Double> imperviousWstorageRainfallData;

    LinkedHashMap<Instant, Double> perviousEvaporationData;
    LinkedHashMap<Instant, Double> imperviousWOstorageEvaporationData;
    LinkedHashMap<Instant, Double> imperviousWstorageEvaporationData;

    LinkedHashMap<Instant, Double> perviousInfiltrationData;
    LinkedHashMap<Instant, Double> imperviousWOstorageInfiltrationData;
    LinkedHashMap<Instant, Double> imperviousWstorageInfiltrationData;

    LinkedHashMap<Instant, Double> flowRatePervious; //TODO evaluate it at each step!
    LinkedHashMap<Instant, Double> flowRateImperviousWOstorage; //TODO evaluate it at each step!
    LinkedHashMap<Instant, Double> flowRateImperviousWstorage; //TODO evaluate it at each step!

    Double imperviousToPerviousPercentageRouted;
    Double perviousToImperviousPercentageRouted;

    Double perviousArea;
    Double imperviousWOstorageArea;
    Double imperviousWstorageArea;

    Double perviousStorage;
    Double imperviousWOstorageStorage;
    Double imperviousWstorageStorage;

    Double perviousExcessRainfall;
    Double imperviousWOstorageExcessRainfall;
    Double imperviousWstorageExcessRainfall;

    Double perviousDepthFactor;
    Double imperviousWOstorageDepthFactor;
    Double imperviousWstorageDepthFactor;

    AbstractRunoffMethod odeSolver;

    @Description("Minimum step for evaluation of the ODE")
    @In
    private Double minimumStepSize = 1.0e-8;

    @Description("Maximum step for evaluation of the ODE")
    @In
    private Double maximumStepSize = 1.0e+3;

    @Description("Absolute tolerance for evaluation of the ODE")
    @In
    private Double absoluteTolerance = 1.0e-10;

    @Description("Relative tolerance for evaluation of the ODE")
    @In
    private Double relativeTolerance = 1.0e-10;

    private void runoffMethod(){

        /**
         * a.
         * If snow melt is being simulated, use the procedures described in Chapter 6 to
         * adjust the precipitation rate i to reflect any snow accumulation (which decreases i) or snow melt (which increases i).
         *
         * b.
         * Set the available moisture volume d_a to ii∆tt + dd where d is the current ponded depth and limit the evaporation rate e
         * to be no greater than d/ ∆ t.
         *
         * c.
         * If the subarea is pervious, then determine the infiltration rate f using the methods described in Chapter 4 and
         * if groundwater is being simulated consider the possible reduction in f that can occur due to fully saturated conditions (see Chapter 5).
         * Otherwise set f = 0.
         *
         * d. If losses exceed the available moisture volume (i.e.,(ee + ff)∆tt ≥ dd aa ) then d = 0 and the runoff rate q is 0.
         * Otherwise, compute the rainfall excess i x as: ii XX = ii − ee − ff.
         *
         * e. If the rainfall excess is not enough to fill the depression storage depth d s over the time step (i.e.,dd + ii XX ∆tt ≤ dd SS )
         * then update d to dd + ii XX ∆tt and se
         */

        Instant currentTime = initialTime;
        while(currentTime.isBefore(totalTime)) {

            Instant nextTime = currentTime.plus(runoffStepSize, (TemporalUnit) SECONDS);

            //check snownelt - snowaccumulation TODO build a new component

            // evaluate the moisture volume


            Double imperviousWstorageMoistureVolume = imperviousWstorageRainfallData.get(currentTime)*runoffStepSize +
                    imperviousWstorageDepth.get(currentTime);

            imperviousWstorageEvaporationData.put(currentTime,
                    Math.max(imperviousWstorageEvaporationData.get(currentTime), imperviousWstorageDepth.get(currentTime)/runoffStepSize));

            if(imperviousWstorageEvaporationData.get(currentTime)*runoffStepSize >= imperviousWstorageMoistureVolume) {
                imperviousWstorageDepth.put(nextTime, 0.0);
                flowRateImperviousWstorage.put(nextTime, 0.0);
                //imperviousWOstorageDepth.put(nextTime, 0.0);
                //flowRateImperviousWOstorage.put(nextTime, 0.0);
            }
            else {
                Double imperviousWstorageExcessRainfall = imperviousWstorageRainfallData.get(currentTime) -
                        imperviousWstorageEvaporationData.get(currentTime) - imperviousWstorageInfiltrationData.get(currentTime);

                if(imperviousWstorageExcessRainfall <= imperviousWstorageStorage) {
                    imperviousWstorageDepth.put(nextTime, imperviousWstorageDepth.get(currentTime) +
                            imperviousWstorageRainfallData.get(currentTime)*runoffStepSize);
                    flowRateImperviousWstorage.put(nextTime, 0.0);
                }

                //if(imperviousWstorageExcessRainfall > perviousStorage) {
                //    upgradeSubareasDepth(currentTime, nextTime);
                //}
            }

            Double imperviousWOstorageMoistureVolume = imperviousWstorageRainfallData.get(currentTime)*runoffStepSize +
                    imperviousWstorageDepth.get(currentTime);

            imperviousWstorageEvaporationData.put(currentTime,
                    Math.max(imperviousWstorageEvaporationData.get(currentTime), imperviousWstorageDepth.get(currentTime)/runoffStepSize));

            if(imperviousWstorageEvaporationData.get(currentTime)*runoffStepSize >= imperviousWstorageMoistureVolume) {
                imperviousWstorageDepth.put(nextTime, 0.0);
                flowRateImperviousWstorage.put(nextTime, 0.0);
                //imperviousWOstorageDepth.put(nextTime, 0.0);
                //flowRateImperviousWOstorage.put(nextTime, 0.0);
            }
            else {
                Double imperviousWstorageExcessRainfall = imperviousWstorageRainfallData.get(currentTime) -
                        imperviousWstorageEvaporationData.get(currentTime) - imperviousWstorageInfiltrationData.get(currentTime);

                if(imperviousWstorageExcessRainfall <= imperviousWstorageStorage) {
                    imperviousWstorageDepth.put(nextTime, imperviousWstorageDepth.get(currentTime) +
                            imperviousWstorageRainfallData.get(currentTime)*runoffStepSize);
                    flowRateImperviousWstorage.put(nextTime, 0.0);
                }

                //if(imperviousWstorageExcessRainfall > perviousStorage) {
                //    upgradeSubareasDepth(currentTime, nextTime);
                //}
            }


            currentTime.plus(runoffStepSize, (TemporalUnit) SECONDS);
        }

        //for each area evaluate the runoff and return the modified data
    }

    private void upgradeFlowRateRunoff(Subarea subarea) {
        subarea.
    }

    private void upgradeSubareasDepth(Instant currentTime, Instant nextTime) {

        Double nextDepth;
        if(imperviousPercentage == 1){
            if(imperviousDepressionStorage != 0.0) {
                nextDepth = evaluateNextDepth(imperviousWstorageRainfallData.get(currentTime),
                        imperviousWstorageEvaporationData.get(currentTime), imperviousWstorageInfiltrationData.get(currentTime),
                        imperviousWstorageDepthFactor, currentTime, nextTime, imperviousWstorageDepth.get(currentTime));

                perviousDepth.put(nextTime, nextDepth);
            }
            nextDepth = evaluateNextDepth(imperviousWOstorageRainfallData.get(currentTime),
                    imperviousWOstorageEvaporationData.get(currentTime), imperviousWOstorageInfiltrationData.get(currentTime),
                    imperviousWOstorageDepthFactor, currentTime, nextTime, imperviousWOstorageDepth.get(currentTime));

            imperviousWOstorageDepth.put(nextTime, nextDepth);
        }
        else if(imperviousPercentage == 0) {

            nextDepth = evaluateNextDepth(perviousRainfallData.get(currentTime),
                    perviousEvaporationData.get(currentTime), perviousInfiltrationData.get(currentTime),
                    perviousDepthFactor, currentTime, nextTime, perviousDepth.get(currentTime));

            perviousDepth.put(nextTime, nextDepth);
        }
        else {
            nextDepth = evaluateNextDepth(imperviousWOstorageRainfallData.get(currentTime),
                    imperviousWOstorageEvaporationData.get(currentTime), imperviousWOstorageInfiltrationData.get(currentTime),
                    imperviousWOstorageDepthFactor, currentTime, nextTime, imperviousWOstorageDepth.get(currentTime));

            imperviousWOstorageDepth.put(nextTime, nextDepth);

            if(perviousRouteTo == "OUTLET") {
                if(imperviousWstorageRouteTo == "PERVIOUS") {

                    nextDepth = evaluateNextDepth(imperviousWstorageRainfallData.get(currentTime),
                            imperviousWstorageEvaporationData.get(currentTime), imperviousWstorageInfiltrationData.get(currentTime),
                            imperviousWstorageDepthFactor, currentTime, nextTime, imperviousWstorageDepth.get(currentTime));

                    imperviousWstorageDepth.put(nextTime, nextDepth);

                    upgradeRainfallData(perviousRainfallData, flowRateImperviousWOstorage, flowRateImperviousWstorage,
                            imperviousToPerviousPercentageRouted, imperviousWstorageArea, imperviousWOstorageArea,
                            perviousArea, currentTime);

                    nextDepth = evaluateNextDepth(perviousRainfallData.get(currentTime),
                            perviousEvaporationData.get(currentTime), perviousInfiltrationData.get(currentTime),
                            perviousDepthFactor, currentTime, nextTime, perviousDepth.get(currentTime));

                    perviousDepth.put(nextTime, nextDepth);

                }
                else {
                    nextDepth = evaluateNextDepth(imperviousWstorageRainfallData.get(currentTime),
                            imperviousWstorageEvaporationData.get(currentTime), imperviousWstorageInfiltrationData.get(currentTime),
                            imperviousWstorageDepthFactor, currentTime, nextTime, imperviousWstorageDepth.get(currentTime));

                    imperviousWstorageDepth.put(nextTime, nextDepth);

                    nextDepth = evaluateNextDepth(perviousRainfallData.get(currentTime),
                            perviousEvaporationData.get(currentTime), perviousInfiltrationData.get(currentTime),
                            perviousDepthFactor, currentTime, nextTime, perviousDepth.get(currentTime));

                    perviousDepth.put(nextTime, nextDepth);
                }
            }
            else {
                nextDepth = evaluateNextDepth(perviousRainfallData.get(currentTime),
                        perviousEvaporationData.get(currentTime), perviousInfiltrationData.get(currentTime),
                        perviousDepthFactor, currentTime, nextTime, perviousDepth.get(currentTime));

                perviousDepth.put(nextTime, nextDepth);

                upgradeRainfallData(imperviousWstorageRainfallData, flowRatePervious, perviousToImperviousPercentageRouted,
                        perviousArea, imperviousWstorageArea, currentTime);

                nextDepth = evaluateNextDepth(imperviousWstorageRainfallData.get(currentTime),
                        imperviousWstorageEvaporationData.get(currentTime), imperviousWstorageInfiltrationData.get(currentTime),
                        imperviousWstorageDepthFactor, currentTime, nextTime, imperviousWstorageDepth.get(currentTime));

                perviousDepth.put(nextTime, nextDepth);
            }
        }
    }

    private LinkedHashMap<Instant, Double> upgradeRainfallData(LinkedHashMap<Instant, Double> rainfallDataPervious,
                                                               LinkedHashMap<Instant, Double> flowRateImperviousWOstorage,
                                                               LinkedHashMap<Instant, Double> flowRateImperviousWstorage,
                                                               Double imperviousToPerviousPercentageRouted,
                                                               Double imperviousWstorageArea, Double imperviousWOstorageArea,
                                                               Double perviousArea, Instant currentTime) {

        Double rainfallDataImperviousWstorage = flowRateImperviousWstorage.get(currentTime)*
                imperviousWstorageArea*imperviousToPerviousPercentageRouted;
        Double rainfallDataImperviousWOstorage = flowRateImperviousWOstorage.get(currentTime)*
                imperviousWOstorageArea*imperviousToPerviousPercentageRouted;

        rainfallDataPervious.put(currentTime, rainfallDataPervious.get(currentTime) + (rainfallDataImperviousWstorage +
                rainfallDataImperviousWOstorage)/perviousArea);

        return rainfallDataPervious;
    }

    private LinkedHashMap<Instant, Double> upgradeRainfallData(LinkedHashMap<Instant, Double> rainfallDataImpervious,
                                                               LinkedHashMap<Instant, Double> flowRatePervious,
                                                               Double perviousToImperviousPercentageRouted,
                                                               Double perviousArea, Double imperviousWstorageArea,
                                                               Instant currentTime) {

        Double rainfallDataPervious = flowRatePervious.get(currentTime)*perviousArea*perviousToImperviousPercentageRouted;

        rainfallDataImpervious.put(currentTime, rainfallDataImpervious.get(currentTime) +
                (rainfallDataPervious)/imperviousWstorageArea);

        return rainfallDataImpervious;
    }

    private Double evaluateNextDepth(Double rainfallData, Double evaporationData, Double infiltrationData,
                                    Double depthFactor, Instant initialTimeInstant, Instant finalTimeInstant, Double initialValueDouble) {

        odeSolver = new DormandPrince54(rainfallData - evaporationData - infiltrationData, depthFactor,
                minimumStepSize, maximumStepSize, absoluteTolerance, relativeTolerance);

        Double initialTime = ((double) initialTimeInstant.getEpochSecond());
        Double finalTime = ((double) finalTimeInstant.getEpochSecond());

        double[] initialValue = null;
        initialValue[0] = initialValueDouble;

        odeSolver.integrate(initialTime, initialValue, finalTime, initialValue);

        return initialValue[0];
    }

}
    /*




    @Description("Structured input data")
    @In
    @Out
    String inputData;
    //SWMMobject inputData;

    @Description("Minimum step for evaluation of the ODE")
    @In
    private Double minimumStepSize = 1.0e-8;

    @Description("Maximum step for evaluation of the ODE")
    @In
    private Double maximumStepSize = 1.0e+3;

    @Description("Absolute tolerance for evaluation of the ODE")
    @In
    private Double absoluteTolerance = 1.0e-10;

    @Description("Relative tolerance for evaluation of the ODE")
    @In
    private Double relativeTolerance = 1.0e-10;

    @Description("Precipitation input")
    @In
    private Double precipitation;

    @Description("Constant depth factor")
    @In
    private Double depthFactor;

    @Description("Initial time")
    @In
    private Double initialTime;

    @Description("Final time")
    @In
    private Double finalTime;

    @Description("Initial value")
    @In
    private Double[] initialValue = { 0.0 };

    */

    /*TODO is necessary??
    @Description("Output step size")
    @In
    private Double outputStepSize = 0.0;
    */


    /*
    @Description("Output values")
    private Double[] outputValues;


    @Initialize
    void Runoff(){
        if ( inputData != null ) {
            //Manage SWMMobject to fill fields of the class
            //minimumStepSize =
            //maximumStepSize =
            //absoluteTolerance =
            //relativeTolerance =
            //depthFactor =
            //initialTime = selected element of the adapted vector
            //finalTime = selected element of the vector
            //outputStepSize =
        }
    }

    AbstractRunoffMethod odeSolver = new DormandPrince54(precipitation, depthFactor,
            minimumStepSize, maximumStepSize, absoluteTolerance, relativeTolerance);

    @Execute
    public void run(){

        //infiltration method(i, e)
        //runoff method

        //groundwater method(i)
    }

    @Finalize
    private void updateSWMMobject(){
        if ( inputData != null ) {
            //Manage SWMMobject to fill fields of the class
            //minimumStepSize =
            //maximumStepSize =
            //absoluteTolerance =
            //relativeTolerance =
            //precipitation =
            //depthFactor =
            //initialTime =
            //finalTime =
            //outputStepSize =
        }
    }

    private void runoffMethod(){
        //reorder A1-A2-A3
        for(long currentTime = initialTime; currentTime < totalTime; currentTime += runoffStepSize) {

            if(imperviousPercentage == 1){
                if(depressionStorageImpervious != 0.0) {
                    imperviousWstorage.depth = evaluateNextDepth(SubareaSetup imperviousWstorage);
                }
                imperviousWOstorage.depth = evaluateNextDepth(SubareaSetup imperviousWOstorage);
            }
            else if(imperviousPercentage == 0) {
                pervious.depth = evaluateNextDepth(SubareaSetup pervious);
            }
            else {
                imperviousWOstorage.depth = evaluateNextDepth(SubareaSetup imperviousWOstorage);
                if(pervious.routeTo == OUTLET) {
                    if(imperviousWstorage.routeTo == PERVIOUS) {
                        imperviousWstorage.depth = evaluateNextDepth(SubareaSetup imperviousWstorage);
                        updateRainfallData(SubareaSetup imperviousWstorage, SubareaSetup imperviousWOstorage, currentTime);
                        pervious.depth = evaluateNextDepth(SubareaSetup pervious);
                    }
                    else {
                        imperviousWstorage.depth = evaluateNextDepth(SubareaSetup imperviousWstorage);
                        pervious.depth = evaluateNextDepth(SubareaSetup pervious);
                    }
                }
                else {
                    pervious.depth = evaluateNextDepth(SubareaSetup pervious);
                    updateRainfallData(SubareaSetup pervious, currentTime);
                    imperviousWstorage.depth = evaluateNextDepth(SubareaSetup imperviousWstorage);
                }
            }
            upgradeInfiltrationData(subcatchmentName);
        }

        //for each area evaluate the runoff and return the modified data
    }

    private LinkedHashMap<Instant, Double> upgradeRainfallData(LinkedHashMap<> rainfallData,
                                                               Double imperviousWstoragePercentageRouted,
                                                               Double imperviousWOstoragePercentageRouted,
                                                               Instant currentTime) {

        rainfallFromImperviousWstorage = rainfallData.get(currentTime)*imperviousWstorage.area;
        rainfallFromImperviousWOstorage = rainfallData.get(currentTime)*imperviousWOstorage.area;

        return rainfallData.get(currentTime) + (rainfallFromImperviousWstorage + rainfallFromImperviousWOstorage)/pervious.area;
    }

    private LinkedHashMap<Instant, Double> upgradeRainfallData(LinkedHashMap<> rainfallData,
                                                               Double perviousPercentageRouted,
                                                               Instant currentTime) {

        rainfallPervious = rainfallData.get(currentTime)*pervious.area;

        return rainfallData.get(currentTime) + rainfallPervious + imperviousWstorage.area;
    }

    private Double evaluateNextStep(Double rainfallData, Double evaporationData, Double infiltrationData,
                                    Double depthFactor, Instant initialTime, Instant finalTime, Double[] initialValue) {

        odeSolver.integrate(initialTime, initialValue, finalTime, initialValue);

        return initialValue[0];
    }
}
*/