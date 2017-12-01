package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

public class Subarea extends AbstractSubcatchments {

    String raingageName;
    String receiverRunoffName;

    SubareaSetup subareaPervious = new Pervious(); //TODO what is necessary to pass??
    SubareaSetup subareaImperviousWOstorage = new ImperviousWithoutStorage(); //TODO what is necessary to pass??
    SubareaSetup subareaImperviousWstorage = new ImperviousWithStorage(); //TODO what is necessary to pass??

    Double imperviousPercentage;
    Double characteristicWidth;
    Double subareaSlope;
    Double curbLength;

    public Subarea(ReadDataFromFile readDataFromFile, AcquiferSetup acquiferSetup, SnowPackSetup snowpack,
                   ProjectUnits subcatchmentUnits, String subcatchmentName, Double subcatchmentArea,
                   String raingageName, String receiverRunoffName, Double imperviousPercentage,
                   Double characteristicWidth, Double subareaSlope, Double curbLength) {

        super(readDataFromFile, acquiferSetup, snowpack, subcatchmentUnits, subcatchmentName, subcatchmentArea);

        this.raingageName = raingageName;
        this.receiverRunoffName = receiverRunoffName;
        this.imperviousPercentage = imperviousPercentage;
        this.characteristicWidth = characteristicWidth;
        this.subareaSlope = subareaSlope;
        this.curbLength = curbLength;

        //TODO create divideAreas();
    }
}
