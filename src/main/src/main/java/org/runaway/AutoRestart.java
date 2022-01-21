package org.runaway;

import org.bukkit.Bukkit;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

/*
 * Created by _RunAway_ on 21.4.2019
 */

public class AutoRestart {

    public static long getTime() {
        return timeToRestart;
    }

    private static long timeToRestart;

    private static void timeUpdater() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Prison.getInstance(), () -> {
            if (timeToRestart > 0) {
                timeToRestart--;
            } else {
                Utils.DisableKick();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }, 0L, 1200L);
    }

    public static void loadAutoRestarter() {
        try {
            timeToRestart = 1440;
            timeUpdater();
            Prison.getInstance().setAutoRestart();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading auto restarter!");
            Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }
}
