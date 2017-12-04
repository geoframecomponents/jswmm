package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.subcatchment;

import org.altervista.growworkinghard.jswmm.dataStructure.ProjectUnits;
import org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData.ReadDataFromFile;

public class Subarea extends AbstractSubcatchments {

    SubareaSetup subareaPervious;
    SubareaSetup subareaImperviousWOstorage;
    SubareaSetup subareaImperviousWstorage;

    String raingageName;
    SubcatchmentReceiverRunoff subcatchmentReceiverRunoff;

    Double percentageImperviousWOstorage;

    Double imperviousPercentage;
    Double characteristicWidth;
    Double subareaSlope;
    Double curbLength;

    public Subarea(ReadDataFromFile readDataFromFile, AcquiferSetup acquiferSetup, SnowPackSetup snowpack,
                   ProjectUnits subcatchmentUnits, String subcatchmentName, Double subcatchmentArea, String raingageName,
                   SubcatchmentReceiverRunoff subcatchmentReceiverRunoff, SubareaReceiver.SubareaReceiverRunoff subareaReceiver,
                   Double percentageReceiver, Double imperviousPercentage, Double characteristicWidth, Double subareaSlope,
                   Double curbLength) {

        this.readDataFromFile = readDataFromFile;
        this.raingageName = raingageName;
        this.subcatchmentReceiverRunoff = subcatchmentReceiverRunoff;
        this.imperviousPercentage = imperviousPercentage;
        this.characteristicWidth = characteristicWidth;
        this.subareaSlope = subareaSlope;
        this.curbLength = curbLength;

        Double perviousArea = subcatchmentArea*(100-imperviousPercentage);
        this.subareaPervious = new Pervious(perviousArea, subareaReceiver, percentageReceiver);
    }

    public Subarea(ReadDataFromFile readDataFromFile, AcquiferSetup acquiferSetup, SnowPackSetup snowpack,
                   ProjectUnits subcatchmentUnits, String subcatchmentName, Double subcatchmentArea, String raingageName,
                   SubcatchmentReceiverRunoff subcatchmentReceiverRunoff, Double imperviousPercentage,
                   SubareaReceiver.SubareaReceiverRunoff subareaPerviousReceiver, Double percentagePerviousReceiver,
                   SubareaReceiver.SubareaReceiverRunoff subareaImperviousWstorageReceiver, Double percentageImperviousWstorageReceiver,
                   SubareaReceiver.SubareaReceiverRunoff subareaImperviousWOstorageReceiver, Double percentageImperviousWOstorageReceiver,
                   Double percentageImperviousWOstorage, Double characteristicWidth, Double subareaSlope,
                   Double curbLength) {

        this(readDataFromFile, acquiferSetup, snowpack, subcatchmentUnits, subcatchmentName, subcatchmentArea, raingageName,
                subcatchmentReceiverRunoff, subareaPerviousReceiver, percentagePerviousReceiver, imperviousPercentage, characteristicWidth,
                subareaSlope, curbLength);

        this.percentageImperviousWOstorage = percentageImperviousWOstorage;

        Double imperviousWOstorageArea = subcatchmentArea*(imperviousPercentage)*percentageImperviousWOstorage;
        this.subareaImperviousWOstorage = new ImperviousWithoutStorage(imperviousWOstorageArea, subareaImperviousWOstorageReceiver,
                percentageImperviousWOstorageReceiver);

        Double imperviousWstorageArea = subcatchmentArea - imperviousWOstorageArea;
        this.subareaImperviousWstorage = new ImperviousWithStorage(imperviousWstorageArea, subareaImperviousWstorageReceiver,
                percentageImperviousWstorageReceiver);
    }
}
