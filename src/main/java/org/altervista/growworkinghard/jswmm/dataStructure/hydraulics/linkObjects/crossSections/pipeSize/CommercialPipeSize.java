package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections.pipeSize;

public abstract class CommercialPipeSize {

    abstract public double[] getCommercialDiameter(double designedDiameter);

    // Factory method
    public static CommercialPipeSize commercialPipe(String company) {
        switch(company.toLowerCase()) {
            case "Oppo":
                return new Oppo_pvc();

            default:
                throw new IllegalArgumentException();
        }
    }
}
