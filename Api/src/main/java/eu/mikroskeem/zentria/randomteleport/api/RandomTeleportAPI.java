package eu.mikroskeem.zentria.randomteleport.api;

import me.darkeyedragon.randomtp.api.world.RandomWorld;
import me.darkeyedragon.randomtp.api.world.location.RandomLocation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.CompletableFuture;

public interface RandomTeleportAPI {
    @Nullable
    RandomWorld getWorld(String name);

    CompletableFuture<RandomLocation> getValidRandomLocation(RandomWorld world);

    CompletableFuture<Boolean> checkRandomLocationSafety(RandomLocation randomLocation);
}
