package eu.mikroskeem.zentria.randomteleport;

import me.darkeyedragon.randomtp.SpigotImpl;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public final class ServerExecutor implements Executor {
    private ServerExecutor() {}
    public static final ServerExecutor INSTANCE = new ServerExecutor();

    @Override
    public void execute(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(SpigotImpl.getPlugin(SpigotImpl.class), runnable);
    }
}
