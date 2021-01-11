package org.runaway.fishing.fishes;

import org.runaway.fishing.EFishType;
import org.runaway.fishing.Fish;

public class Perch extends Fish {

    @Override
    public String getName() {
        return "Окунь";
    }

    @Override
    public EFishType getType() {
        return EFishType.ORDINARY;
    }

    @Override
    protected double getMaxWeight() {
        return 600;
    }

    @Override
    protected double getChance() {
        return 0.6;
    }

    @Override
    protected short getIconData() {
        return 1;
    }

    @Override
    public double getPrice() {
        return 0.000040;
    }
}
