package org.runaway.boosters;

import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.Date;

/*
 * Created by _RunAway_ on 30.1.2019
 */

public class LBlocks extends Booster {

    @Override
    public void start(String owner, long time, double multiplier) {
        Gamer gamer = Main.gamers.get(Bukkit.getOfflinePlayer(owner).getUniqueId());
        if (!Utils.getlBlocksTime().containsKey(owner)) {
            Date target = new Date();
            target = DateUtils.addSeconds(target, (int)time);
            Utils.getlBlocksTime().put(owner, Vars.dateFormat.format(target));
            Utils.getlBlocksRealTime().put(owner, time);
            Utils.getlBlocksActivatingTime().put(owner, System.currentTimeMillis());
            Utils.getlBlocksMultiplier().put(owner, multiplier);
            if (Utils.getPlayers().contains(owner)) {
                gamer.sendTitle(ChatColor.GREEN + "Вы активировали", ChatColor.GREEN + "бустер блоков " + multiplier + "x");
            }
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Utils.getlBlocksMultiplier().remove(owner);
                Utils.getlBlocksTime().remove(owner);
                Utils.getlBlocksRealTime().remove(owner);
                Utils.getlBlocksActivatingTime().remove(owner);
                if (Utils.getPlayers().contains(owner)) {
                    gamer.sendMessage(ChatColor.RED + "Ваш локальный бустер блоков закончился!");
                }
            },20 * time);
        }
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public String getOwner() {
        return null;
    }

    @Override
    public double getMultiplier() {
        return 0;
    }

    @Override
    boolean isActive() {
        return false;
    }
}
