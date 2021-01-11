package org.runaway.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SyncRepeatTask implements Cancellable {
    public static int tasks;
    private static JavaPlugin javaPlugin;
    private BukkitTask task;

    public static void setJavaPlugin(JavaPlugin javaPlugin) {
        SyncRepeatTask.javaPlugin = javaPlugin;
    }

    public SyncRepeatTask(JavaPlugin javaPlugin) {
        SyncRepeatTask.javaPlugin = javaPlugin;
    }

    public SyncRepeatTask(Runnable run, int tick, int start) {
        try {
            this.task = Bukkit.getScheduler().runTaskTimer(javaPlugin, run, start, tick);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("SR TaskError: " + e.getMessage());
            this.stop();
        }
        tasks++;
        //Bukkit.getConsoleSender().sendMessage("sync tasks: " + tasks);
    }

    public SyncRepeatTask(Runnable run, int tick) {
        this(run, tick, 0);
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
