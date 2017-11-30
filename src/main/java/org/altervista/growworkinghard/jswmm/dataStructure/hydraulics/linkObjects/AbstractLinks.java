package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects;

public abstract class AbstractLinks {
    String linkID;
    String upstreamNode;
    String downstreamNode;

    /**
     *
     */
    //AbstractCrossSectionData crossSectionProperties;

    Double linkFlowRate;
    Double linkDepth;
    Double linkVolume;








    /**

   int           type;            // link type code
   int           subIndex;        // index of link's sub-category
   char          rptFlag;         // reporting flag
   double        offset1;         // ht. above start node invert (ft)
   double        offset2;         // ht. above end node invert (ft)


   double        q0;              // initial flow (cfs)
   double        qLimit;          // constraint on max. flow (cfs)

   double        cLossInlet;      // inlet loss coeff.
   double        cLossOutlet;     // outlet loss coeff.
   double        cLossAvg;        // avg. loss coeff.

   double        seepRate;        // seepage rate (ft/sec)
   int           hasFlapGate;     // true if flap gate present

   //-----------------------------

   double        surfArea1;       // upstream surface area (ft2)
   double        surfArea2;       // downstream surface area (ft2)

   double        qFull;           // flow when full (cfs)

   double        setting;         // current control setting
   double        targetSetting;   // target control setting

   double        timeLastSet;     // time when setting was last changed        //(5.1.010)

   double        froude;          // Froude number

   double*       oldQual;         // previous quality state
   double*       newQual;         // current quality state
   double*       totalLoad;       // total quality mass loading

   int           flowClass;       // flow classification

   double        dqdh;            // change in flow w.r.t. head (ft2/sec)
   signed char   direction;       // flow direction flag
   char          bypassed;        // bypass dynwave calc. flag
   char          normalFlow;      // normal flow limited flag
   char          inletControl;    // culvert inlet control flag

     */
}