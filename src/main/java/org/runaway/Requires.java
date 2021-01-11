package org.runaway;

import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;

/*
 * Created by _RunAway_ on 11.2.2019
 */

public class Requires {

    private Gamer gamer;

    public Requires(Gamer gamer) {
        this.gamer = gamer;
        //this.mode = EMode.valueOf(gamer.getStatistics(EStat.MODE).toString().toUpperCase());
        //this.percent = mode.getPercent() / 100;
    }

    public double blocksNextLevel() {
        return getBlocksLevel(gamer.getIntStatistics(EStat.LEVEL) + 1);
    }

    private double getBlocksLevel(int level) {
        return Math.round(EConfig.CONFIG.getConfig().getDouble("levels." + level + ".blocks")/* * percent*/);
    }

    public double costNextLevel() {
        return getCostLevel(gamer.getIntStatistics(EStat.LEVEL) + 1);
    }

    private double getCostLevel(int level) {
        return Math.round(EConfig.CONFIG.getConfig().getDouble("levels." + level + ".price")/* * percent*/);
    }
}
