import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

import java.util.List;

public class BackEulerSolver {
    @In double linkSectionArea;
    @In double linkLength;
    @In double upstreamHeadNode;
    @In double downstreamHeadNode;
    @In double upstreamElevationNode;
    @In double downstreamElevationNode;
    @In double gravityForce;
    @In double linkRoughnessCoefficient;
    @In double elaspsedStepTime;
    @In double storageNodeArea;
    @In @Out List<double[]> dischargeOverSteps;
    @Out List<double[]> upstreamHead;

    //private double getLinkMidDepth(double upstreamElevationNode, double upstreamHeadNode,
    //                               double downstreamElevationNode, double downstreamHeadNode){
        //return linkMidDepth;
    //}

    private void getSectionEntities(){

    }

    //private double getLinkSurfaceArea(){
    //    return sumOfSurfaceArea;
    //}

    //private double getInertiaTerm(){
    //    return inertialTerm;
    //}

    //private double getPressureTerm(){
    //    return pressureTerm;
    //}

    //private double getFrictionTerm(){
    //    return frictionTerm;
    //}

    //private void upgradeLinkDischarge(){
    //
    //}

    private void upgradeHeadNode() {

    }

    @Execute
    public void upgrade(){
    //    upgradeLinkDischarge();
    //    upgradeHeadNode();
    }
}
