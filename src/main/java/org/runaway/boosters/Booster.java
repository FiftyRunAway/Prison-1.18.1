package org.runaway.boosters;

/*
 * Created by _RunAway_ on 30.1.2019
 */

abstract public class Booster {

    abstract void start(String owner, long time, double multiplier);

    abstract long getTime();

    abstract String getOwner();

    abstract double getMultiplier();

    abstract boolean isActive();
}
