package org.runaway.achievements;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Main;
import org.runaway.enums.BoosterType;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

public class BoosterReward implements Reward {

    private String reward;
    private BoosterType type;
    private double mult;
    private long time;
    private boolean global;

    @Override
    public Reward setReward(Object object) {
        this.reward = String.valueOf(object);
        return this;
    }

    Reward setReward(BoosterType type, double multiplier, long time, boolean global) {
        this.type = type;
        this.mult = multiplier;
        this.time = time;
        this.global = global;
        return this;
    }

    @Override
    public Object getReward() {
        return reward;
    }

    @Override
    public Class getTypes() {
        return ItemStack.class;
    }

    @Override
    public void giveReward(Player player) {
        GamerManager.getGamer(player).addBooster(type, mult, time, global);
    }

    @Override
    public String rewardTitle() {
        return "&2" + (global ? "Глобальный" : "Локальный") + " ускоритель " + (type == BoosterType.MONEY ? "денег" : "блоков") + " " + mult + "x на " + Utils.formatTime(time);
    }
}
