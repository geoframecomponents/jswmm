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

import java.io.File;
import java.time.Instant;
import java.util.LinkedHashMap;

class VolumeRainfall implements DataCollector {

    @Override
    public void setDatasetName(String stationName) {
        throw new NullPointerException("Nothing implemented yet.");
    }

    @Override
    public void setDatasetStepSize(Long rainfallStepSize) {
        throw new NullPointerException("Nothing implemented yet.");
    }

    @Override
    public Long getDatasetStepSize() {
        throw new NullPointerException("Nothing implemented yet.");
    }

    @Override
    public File getDataSourceName() {
        throw new NullPointerException("Nothing implemented yet.");
    }

    @Override
    public String getDatasetName() {
        throw new NullPointerException("Nothing implemented yet.");
    }

    @Override
    public LinkedHashMap<String, LinkedHashMap<Instant, Double>> getDatasetData() {
        throw new NullPointerException("Nothing implemented yet.");
    }
}
