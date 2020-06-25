package org.runaway.battlepass;

import org.bukkit.Material;
import org.runaway.Gamer;

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
        this.getReward(gamer);
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

    public int getValue(int argument) {
        return Integer.parseInt(this.getDetails()[argument].toString());
    }
}
