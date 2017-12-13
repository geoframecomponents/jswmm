package org.altervista.growworkinghard.jswmm.dataStructure.hydraulics.linkObjects.crossSections;

public class Circular implements CrossSectionType {

    Double diameter;

    //Double depthFull;
    //Double areaFull;
    //Double hydraulicRadiousFull;
    //Double sectionFactorFull;

    public Circular(Double diameter) {
        this.diameter = diameter;
    }

    @Override
    public Double getDepthFull() {
        return 0.938*diameter;
    }

    @Override
    public Double getAreaFull() {
        return 0.7854*Math.pow(getDepthFull(), 2);
    }

    @Override
    public Double getHydraulicRadiusFull() {
        return 0.25*getDepthFull();
    }

    @Override
    public Double getSectionFactorFull() {
        return getAreaFull()*Math.pow(getHydraulicRadiusFull(), 2.0/3.0);
    }
}
