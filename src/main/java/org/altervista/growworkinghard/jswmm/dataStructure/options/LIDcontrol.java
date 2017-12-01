package org.altervista.growworkinghard.jswmm.dataStructure.options;

public class LIDcontrol {
    String controlLIDname;
    String subcatchment;
    String reportFileName;

    public LIDcontrol(String controlLIDname, String subcatchment, String reportFileName) {
        this.controlLIDname = controlLIDname;
        this.subcatchment = subcatchment;
        this.reportFileName = reportFileName;
    }
}