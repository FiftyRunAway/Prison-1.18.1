package org.runaway.boosters;

import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.Date;

/*
 * Created by _RunAway_ on 1.2.2019
 */

public class LMoney extends Booster {

    @Override
    public void start(String owner, long time, double multiplier) {
        Gamer gamer = GamerManager.getGamer(Bukkit.getOfflinePlayer(owner).getUniqueId());
        if (!Utils.getlMoneyTime().containsKey(owner)) {
            Date target = new Date();
            target = DateUtils.addSeconds(target, (int)time);
            Utils.getlMoneyTime().put(owner, Vars.dateFormat.format(target));
            Utils.getlMoneyMultiplier().put(owner, multiplier);
            Utils.getlMoneyRealTime().put(owner, time);
            Utils.getlMoneyActivatingTime().put(owner, System.currentTimeMillis());
            if (Utils.getPlayers().contains(owner)) {
                gamer.sendTitle(ChatColor.GREEN + "Вы активировали", ChatColor.GREEN + "бустер денег " + multiplier + "x");
            }
            Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> {
                Utils.getlMoneyMultiplier().remove(owner);
                Utils.getlMoneyTime().remove(owner);
                Utils.getlMoneyRealTime().remove(owner);
                Utils.getlMoneyActivatingTime().remove(owner);
                if (Utils.getPlayers().contains(owner)) {
                    GamerManager.getGamer(owner).sendMessage(ChatColor.RED + "Ваш локальный бустер денег закончился!");
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
    public boolean isActive() {
        return false;
    }
}
