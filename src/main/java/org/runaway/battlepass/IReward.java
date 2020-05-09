package org.runaway.battlepass;

import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.enums.EMessage;
import org.runaway.utils.Utils;

public abstract class IReward {

    private int level;
    private Object[] details;
    private boolean isFree;
    private int value;

    /**
     * Starting on loading server
     */
    protected abstract void init();

    /**
     *
     * @param gamer
     *      has a Player which get the reward
     */
    protected abstract void getReward(Gamer gamer);

    boolean isFree() {
        return this.isFree;
    }

    void setFree(boolean free) {
        this.isFree = free;
    }

    protected abstract String getName();

    protected abstract String getDescription();

    protected int getValue() {
        return this.value;
    }

    protected void setValue(int value) {
        this.value = value;
    }

    void setDetails(Object... objects) {
        this.details = objects;
    }

    protected Object[] getDetails() {
        return this.details;
    }

    public void get(Gamer gamer) {
        if (gamer.hasBattlePass() || this.isFree()) {
            gamer.getPlayer().sendMessage(Utils.colored(EMessage.BPREWARDPAID.getMessage().replace("%reward%", getName())));
            this.getReward(gamer);
        } else {
            gamer.getPlayer().sendMessage(Utils.colored(EMessage.BPREWARDFREE.getMessage().replace("%reward%", getName())));
        }
    }

    protected abstract Material getType();

    /**
     * @return an Argument string for config header
     */
    public abstract String getArgumentsString();

    int getLengthArgumentString() {
        return getArgumentsString().split(" ").length;
    }

    public RewardIcon getIcon() {
        return new RewardIcon.Builder(this).build();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
