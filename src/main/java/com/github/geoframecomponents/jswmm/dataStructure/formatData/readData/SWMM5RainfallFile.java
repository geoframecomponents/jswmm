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

package com.github.geoframecomponents.jswmm.dataStructure.formatData.readData;

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

public class SWMM5RainfallFile implements DataCollector {

    public File dataSourceName;
    public String stationName;
    public Long rainfallStepSize;

    public LinkedHashMap<String, LinkedHashMap<Instant, Double>> fileRead;

    public SWMM5RainfallFile(String dataSourceName, String objectName, Long stepSizeData) throws IOException {
        this.dataSourceName = new File(dataSourceName);
        try {
            this.fileRead = readDataFile(this.dataSourceName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stationName = objectName;
        this.rainfallStepSize = stepSizeData;
    }

    @Override
    public Long getDatasetStepSize() {
        return rainfallStepSize;
    }

    @Override
    public File getDataSourceName() {
        return dataSourceName;
    }

    @Override
    public String getDatasetName() {
        return stationName;
    }

    @Override
    public LinkedHashMap<String, LinkedHashMap<Instant, Double>> getDatasetData() {
        return fileRead;
    }

    @Override
    public void setDatasetName(String stationName) {
        this.stationName = stationName;
    }

    @Override
    public void setDatasetStepSize(Long rainfallStepSize) {
        this.rainfallStepSize = rainfallStepSize;
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
}