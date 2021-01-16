package org.runaway.jobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Job {

    public abstract int getLevel();
    public abstract String getName();
    public abstract String getDescrition();
    public abstract Material getMaterial();
    public abstract ArrayList<JobReq[]> getLevels();
    public abstract String getConfigName();

    public int getMaxLevel() {
        return getLevels().size();
    }

    public Location getLocation(Job job) {
        return Utils.unserializeLocation(EConfig.MINES.getConfig().getString("jobs." + job.getConfigName() + ".location"));
    }

    public Item getButton(Gamer gamer) {
        ArrayList<String> reqs = new ArrayList<>();
        boolean access = gamer.getIntStatistics(EStat.LEVEL) >= getLevel();
        reqs.add("&7• " + (access ? ChatColor.GREEN : ChatColor.RED) + "Минимальный уровень • " + getLevel());
        return new Item.Builder(getMaterial()).name("&7Работа: &r" + getName()).lore(
                new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7Требования к доступу:")
                        .addList(reqs)
                        .build()).build();
    }

    public static int getStatistics(Gamer gamer, JobRequriement requriement) {
        if (requriement == JobRequriement.MONEY) return (int)Math.round(gamer.getDoubleStatistics(EStat.MONEY));
        return getStatistics(gamer, requriement.getConfig());
    }

    public static int getStatistics(Gamer gamer, String name) {
        return gamer.getJobValues(name);
    }

    public static int getLevel(Gamer gamer, Job job) {
        return getStatistics(gamer, job.getClass().getSimpleName().toLowerCase());
    }

    public static void addStatistics(Gamer gamer, JobRequriement requriement) {
        addStatistics(gamer, requriement.getConfig(), 1);
    }

    public static void addStatistics(Gamer gamer, JobRequriement requriement, int value) {
        addStatistics(gamer, requriement.getConfig(), value);
    }

    public static void addStatistics(Gamer gamer, String name) {
        addStatistics(gamer, name, 1);
    }

    public static void addStatistics(Gamer gamer, String name, int value) {
        gamer.getJobValues().put(name, gamer.getJobValues(name) + value);
    }

    public static void removeStatistics(Gamer gamer, JobReq requriement) {
        gamer.getJobValues().put(requriement.getRequriement().getName(),
                getStatistics(gamer, requriement.getRequriement()) - requriement.getValue());
    }

    public static boolean hasStatistics(Gamer gamer, JobReq req) {
        if (req.getRequriement() == JobRequriement.MONEY) return gamer.getDoubleStatistics(EStat.MONEY) >= req.getValue();
        return getStatistics(gamer, req.getRequriement()) >= req.getValue();
    }

    public static void take(Gamer gamer, JobReq job) {
        switch (job.getRequriement()) {
            case MONEY: {
                gamer.withdrawMoney(job.getValue());
                break;
            }
            default: {
                removeStatistics(gamer, job);
            }
        }
    }
}
