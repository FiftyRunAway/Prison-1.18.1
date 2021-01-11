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

    public int getMaxLevel() {
        return getLevels().size();
    }

    public Location getLocation(Job job) {
        return Utils.unserializeLocation(EConfig.MINES.getConfig().getString("jobs." + job.getClass().getSimpleName() + ".location"));
    }

    public Item getButton(Gamer gamer) {
        ArrayList<String> reqs = new ArrayList<>();
        boolean access = (int)gamer.getStatistics(EStat.LEVEL) >= getLevel();
        reqs.add("&7- " + (access ? ChatColor.GREEN : ChatColor.RED) + "Минимальный уровень • " + getLevel());
        return new Item.Builder(getMaterial()).name("&7Работа: &r" + getName()).lore(
                new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7Требования к доступу:")
                        .addList(reqs)
                        .build()).build();
    }

    public static int getStatistics(Gamer gamer, JobRequriement requriement) {
        if (!EConfig.JOBS_DATA.getConfig().contains(gamer.getGamer() + "." + requriement.getConfig())) return 0;
        return EConfig.JOBS_DATA.getConfig().getInt(gamer.getGamer() + "." + requriement.getConfig());
    }

    public static int getStatistics(Gamer gamer, String name) {
        if (!EConfig.JOBS_DATA.getConfig().contains(gamer.getGamer() + "." + name)) return 0;
        return EConfig.JOBS_DATA.getConfig().getInt(gamer.getGamer() + "." + name);
    }

    public static int getLevel(Gamer gamer, Job job) {
        return getStatistics(gamer, job.getClass().getSimpleName().toLowerCase());
    }

    public static void addStatistics(Gamer gamer, JobRequriement requriement) {
        if (getStatistics(gamer, requriement) > 0) {
            EConfig.JOBS_DATA.getConfig().set(gamer.getGamer() + "." + requriement.getConfig(), getStatistics(gamer, requriement) + 1);
        } else {
            EConfig.JOBS_DATA.getConfig().set(gamer.getGamer() + "." + requriement.getConfig(), 1);
        }
        EConfig.JOBS_DATA.saveConfig();
    }

    public static void addStatistics(Gamer gamer, String name) {
        if (getStatistics(gamer, name) > 0) {
            EConfig.JOBS_DATA.getConfig().set(gamer.getGamer() + "." + name, getStatistics(gamer, name) + 1);
        } else {
            EConfig.JOBS_DATA.getConfig().set(gamer.getGamer() + "." + name, 1);
        }
        EConfig.JOBS_DATA.saveConfig();
    }

    public static void removeStatistics(Gamer gamer, JobRequriement requriement) {
        EConfig.JOBS_DATA.getConfig().set(gamer.getGamer() + "." + requriement.getConfig(), null);
        EConfig.JOBS_DATA.saveConfig();
    }

    public static boolean hasStatistics(Gamer gamer, JobReq req) {
        return getStatistics(gamer, req.getRequriement()) >= req.getValue();
    }

    public static void take(Gamer gamer, JobRequriement job) {
        switch (job) {
            case LEGENDARY_FISH: {
                removeStatistics(gamer, job);
                break;
            }
        }
    }
}
