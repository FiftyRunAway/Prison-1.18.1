package org.runaway.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class AsyncRepeatTask implements Cancellable {
    public static int tasks;
    private static JavaPlugin javaPlugin;
    private BukkitTask task;

    public static void setJavaPlugin(JavaPlugin javaPlugin) {
        AsyncRepeatTask.javaPlugin = javaPlugin;
    }

    public AsyncRepeatTask(JavaPlugin javaPlugin) {
        AsyncRepeatTask.javaPlugin = javaPlugin;
    }

    public AsyncRepeatTask(Runnable run, int tick) {
        new AsyncRepeatTask(run, tick, 0);
    }

    public AsyncRepeatTask(Runnable run, int tick, int start) {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, run, start, tick);
        tasks++;
    }

    @Override
    public void stop() {
        if (this.task == null) {
            return;
        }
        this.task.cancel();
        this.task = null;
        tasks--;
    }
}