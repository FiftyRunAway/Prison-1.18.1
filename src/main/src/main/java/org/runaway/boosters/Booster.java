package org.runaway.boosters;

/*
 * Created by _RunAway_ on 30.1.2019
 */

abstract public class Booster {

    public abstract void start(String owner, long time, double multiplier);

    public abstract long getTime();

    public abstract String getOwner();

    public abstract double getMultiplier();

    abstract public boolean isActive();
}
