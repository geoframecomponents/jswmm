/*
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData;

import java.io.*;
import java.time.Instant;
import java.util.LinkedHashMap;

/**
 * Take the rainfall data from file and save it in an LinkedHashMap that has elapsed seconds as key and
 * rainfall value as value.
 * <p>
 * Based on FILE data of SWMM with the following structure:
 * ;Station   Year   Month   Day   Hour   Minutes   Value
 * STA01      2004     6     12     00      00      0.12
 */

public class ReadSWMM5RainfallFile implements ReadDataFromFile {

    File fileName;
    LinkedHashMap<String, LinkedHashMap<Instant, Double>> fileRead;

    public ReadSWMM5RainfallFile(String fileName) throws IOException {
        this.fileName = new File(fileName);
        try {
            this.fileRead = readDataFile(this.fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    LinkedHashMap<String, LinkedHashMap<Instant, Double>> readDataFile(File file)
            throws IOException {

        LinkedHashMap<String, LinkedHashMap<Instant, Double>> dataFromFile = new LinkedHashMap<>();

        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String temporaryLine;
        while ((temporaryLine = bufferRead.readLine()) != null){

            LinkedHashMap<Instant, Double> tempDateValue = new LinkedHashMap<>();
            String[] splittedTempLine = temporaryLine.split("\\s+");

            String currentStation = splittedTempLine[0];
            Instant currentDate = transformToDate(splittedTempLine);
            Double currentValue = Double.parseDouble(splittedTempLine[6]);

            tempDateValue.put(currentDate, currentValue);
            if(dataFromFile.get(currentStation) == null){
                dataFromFile.put(currentStation, tempDateValue);
            }
            else {
                LinkedHashMap<Instant, Double> previousValue = dataFromFile.get(currentStation);
                previousValue.put(currentDate, currentValue);
                dataFromFile.replace(currentStation, previousValue);
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
        String seconds = "00";

        //2007-12-03T10:15:30.00Z
        return Instant.parse(year + "-" + month + "-" + day + "T" + hour + ":" + minutes + ":" + seconds + ".00Z");
    }


    @Override
    public LinkedHashMap<String, LinkedHashMap<Instant, Double>> getData() {
        return fileRead;
    }
}