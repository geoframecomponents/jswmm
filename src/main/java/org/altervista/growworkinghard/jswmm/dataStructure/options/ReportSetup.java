package org.altervista.growworkinghard.jswmm.dataStructure.options;

import java.util.List;

public class ReportSetup {

    boolean reportInput;
    boolean reportContinuity;
    boolean reportFlowStats;
    boolean reportControls;
    List<String> reportSubcatchments;
    List<String> reportNodes;
    List<String> reportLinks;
    List<LIDcontrol> reportLID;

    public ReportSetup(boolean reportInput, boolean reportContinuity, boolean reportFlowStats, boolean reportControls,
                       List<String> reportSubcatchments, List<String> reportNodes,
                       List<String> reportLinks, List<LIDcontrol> reportLID) {

        this.reportInput = reportInput;
        this.reportContinuity = reportContinuity;
        this.reportFlowStats = reportFlowStats;
        this.reportControls = reportControls;
        this.reportSubcatchments = reportSubcatchments;
        this.reportNodes = reportNodes;
        this.reportLinks = reportLinks;
        this.reportLID = reportLID;
    }
}
