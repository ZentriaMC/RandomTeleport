package me.darkeyedragon.randomtp.api.world.location.search;


import me.darkeyedragon.randomtp.api.config.section.subsection.SectionWorldDetail;
import me.darkeyedragon.randomtp.api.world.location.RandomLocation;

import java.util.concurrent.CompletableFuture;

public interface LocationSearcher {

    CompletableFuture<RandomLocation> getRandom(SectionWorldDetail sectionWorldDetail);

    default boolean isSafe(RandomLocation location) {
        return isSafe(location, false);
    }

    boolean isSafe(RandomLocation location, boolean lenient);

    boolean isSafeForPlugins(RandomLocation location);
}
