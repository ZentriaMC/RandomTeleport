package me.darkeyedragon.randomtp.common.config;

import me.darkeyedragon.randomtp.api.config.Dimension;
import me.darkeyedragon.randomtp.api.config.DimensionData;
import me.darkeyedragon.randomtp.api.config.RandomBlacklist;

import java.util.HashMap;
import java.util.Map;

public class Blacklist implements RandomBlacklist {

    private final Map<Dimension, DimensionData> dimensions;

    public Blacklist(Map<Dimension, DimensionData> dimensions) {
        this.dimensions = dimensions;
    }

    public Blacklist() {
        this(new HashMap<>());
    }

    public Map<Dimension, DimensionData> getDimensions() {
        return dimensions;
    }

    public DimensionData getDimensionData(Dimension dimension) {
        return dimensions.get(dimension);
    }

    public void addDimensionData(Dimension dimension, DimensionData dimensionData) {
        dimensions.put(dimension, dimensionData);
    }
}
