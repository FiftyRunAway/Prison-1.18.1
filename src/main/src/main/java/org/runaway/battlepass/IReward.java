package org.runaway.battlepass;

import org.bukkit.Material;
import org.runaway.Gamer;

public abstract class IReward {

    private int level;
    private Object[] details;
    private boolean isFree;
    private Object value;
    private boolean stringValue;

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

    public boolean isFree() {
        return this.isFree;
    }

    void setFree(boolean free) {
        this.isFree = free;
    }

    public boolean isStringValue() {
        return this.stringValue;
    }

    protected void setStringValue(boolean sv) {
        this.stringValue = sv;
    }

    public abstract String getName();

    protected abstract String getDescription();

    public int getValue() {
        return Integer.parseInt(this.value.toString());
    }

    protected void setValue(Object value) {
        this.value = value;
    }

    protected void setDetails(Object... objects) {
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

    public int getIntValue(int argument) {
        return Integer.parseInt(getStringValue(argument));
    }

    public String getStringValue(int argument) {
        return this.getDetails()[argument].toString();
    }
}
