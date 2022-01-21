package org.runaway.boosters;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.runaway.Prison;
import org.runaway.utils.Utils;

/*
 * Created by _RunAway_ on 30.1.2019
 */

public class GBlocks extends Booster {

    private long time, fulltime;
    private String owner;
    private double multiplier;
    private boolean active;
    private int timer;

    @Override
    public void start(String owner, long time, double multiplier) {
        Utils.getPlayers().forEach(s -> Bukkit.getPlayer(s).sendTitle(ChatColor.WHITE + "Активирован ускоритель", ChatColor.YELLOW + "блоков " + multiplier + "x", 20, 20, 20));
        active = true;
        this.time = time;
        this.owner = owner;
        this.multiplier = multiplier;
        Prison.BlocksBar.setVisible(true);
        this.fulltime = time;
        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Prison.getInstance(), () -> {
            if (getTime() == 0) {
                Utils.getPlayers().forEach(s -> Bukkit.getPlayer(s).sendTitle(ChatColor.RED + "Ускоритель блоков " + multiplier + "x", ChatColor.RED + "закончился", 20, 20, 20));
                active = false;
                Prison.BlocksBar.setVisible(false);
                Bukkit.getScheduler().cancelTask(timer);
                Prison.gBlocks = new GBlocks();
                Prison.THXersBlocks.clear();
            } else {
                this.time--;
                Prison.BlocksBar.setProgress(getTime() / (float) fulltime);
                Prison.BlocksBar.setTitle(ChatColor.translateAlternateColorCodes('&',
                        "&fАктивен ускоритель &eблоков " + getMultiplier() + "x. &fПоблагодарите &e" + getOwner() + " &f- &e/tip&f. Осталось &e" + Utils.formatTime(getTime())));
            }
        }, 0, 20);
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getOwner() {
        return owner;
    }
}
