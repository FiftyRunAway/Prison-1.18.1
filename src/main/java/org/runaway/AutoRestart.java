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
        return TIME_TO_RESTART;
    }

    private static long TIME_TO_RESTART;

    private void timeUpdater() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Prison.getInstance(), () -> {
            if (TIME_TO_RESTART > 0) {
                TIME_TO_RESTART--;
            } else {
                Utils.DisableKick();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }, 0L, 1200L);
    }

    void loadAutoRestarter() {
        try {
            TIME_TO_RESTART = 1440;
            timeUpdater();
            Prison.isAutoRestart = true;
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading auto restarter!");
            Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }
}
