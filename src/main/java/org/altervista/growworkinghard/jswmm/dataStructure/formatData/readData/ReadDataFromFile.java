package org.altervista.growworkinghard.jswmm.dataStructure.formatData.readData;

import java.time.Instant;
import java.util.LinkedHashMap;

public interface ReadDataFromFile {
    LinkedHashMap<String, LinkedHashMap<Instant, Double>> getData();
}


