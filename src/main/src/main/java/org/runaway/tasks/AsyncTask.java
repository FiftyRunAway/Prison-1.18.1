package org.runaway.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class AsyncTask implements Task {

    private static JavaPlugin javaPlugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private BukkitTask bukkitTask;

    public static void setJavaPlugin(JavaPlugin javaPlugin) {
        AsyncTask.javaPlugin = javaPlugin;
    }

    public AsyncTask(Runnable run) {
        run(run);
    }

    public AsyncTask(Runnable run, int ticks) {
        run(run, ticks);
    }

    public void run(Runnable run) {
        this.bukkitTask = scheduler.runTaskAsynchronously(javaPlugin, run);
    }

    public void run(Runnable run, int delay) {
        this.bukkitTask = scheduler.runTaskLaterAsynchronously(javaPlugin, run, delay);
    }

    @Override
    public void stop() {
        this.bukkitTask.cancel();
    }
}
