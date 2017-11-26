package org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData;

import org.altervista.growworkinghard.jswmm.dataStructure.hydrology.rainData.AbstractRaingage;

public class Raingage extends AbstractRaingage {

    String raingageID;
    String dataSourceFile;
    String dataTimeseries;



    /*
    char*         ID;              // raingage name
    int           dataSource;      // data from time series or file
    int           tSeries;         // rainfall data time series index
    char          fname[MAXFNAME+1]; // name of rainfall data file

    char          staID[MAXMSG+1]; // station number

    DateTime      startFileDate;   // starting date of data read from file
    DateTime      endFileDate;     // ending date of data read from file

    int           rainType;        // intensity, volume, cumulative
    int           rainInterval;    // recording time interval (seconds)
    int           rainUnits;       // rain depth units (US or SI)

    double        snowFactor;      // snow catch deficiency correction
    //-----------------------------
    long          startFilePos;    // starting byte position in Rain file
    long          endFilePos;      // ending byte position in Rain file
    long          currentFilePos;  // current byte position in Rain file

    double        rainAccum;       // cumulative rainfall

    double        unitsFactor;     // units conversion factor (to inches or mm)

    DateTime      startDate;       // start date of current rainfall
    DateTime      endDate;         // end date of current rainfall
    DateTime      nextDate;        // next date with recorded rainfall

    double        rainfall;        // current rainfall (in/hr or mm/hr)
    double        nextRainfall;    // next rainfall (in/hr or mm/hr)

    double        reportRainfall;  // rainfall value used for reported results

    int           coGage;          // index of gage with same rain AbstractTimeseriesData

    int           isUsed;          // TRUE if gage used by any subcatchment
    int           isCurrent;       // TRUE if gage's rainfall is current */

}
