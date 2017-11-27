package org.altervista.growworkinghard.jswmm.dataStructure.formatData;

import java.io.*;
import java.time.Instant;
import java.util.LinkedHashMap;

public class fileData extends AbstractFilesData{

    LinkedHashMap<String, LinkedHashMap<Instant, Double>> readDataFile(File file)
            throws IOException {

        LinkedHashMap<String, LinkedHashMap<Instant, Double>> dataFromFile = null;

        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String temporaryLine;
        while ((temporaryLine = bufferRead.readLine()) != null){

            LinkedHashMap<Instant, Double> tempDateValue = null;
            String[] splittedTempLine = temporaryLine.split("\\s+");

            String currentStation = splittedTempLine[0];
            Instant currentDate = transformToDate(splittedTempLine);
            Double currentValue = Double.parseDouble(splittedTempLine[6]);

            tempDateValue.put(currentDate, currentValue);
            if(dataFromFile.get(currentStation).put(currentDate, currentValue) == null){
                dataFromFile.put(currentStation, tempDateValue);
            }

        }
        bufferRead.close();

        return dataFromFile;
    }

    private Instant transformToDate(String[] splittedTempRainLine) {
        String year = splittedTempRainLine[1];
        String month = splittedTempRainLine[2];
        String day = splittedTempRainLine[3];
        String hour = splittedTempRainLine[4];
        String minutes = splittedTempRainLine[5];

        return Instant.parse(year + "-" + month + "-" + day + "T" + hour + ":" + minutes + "Z");
    }


}
