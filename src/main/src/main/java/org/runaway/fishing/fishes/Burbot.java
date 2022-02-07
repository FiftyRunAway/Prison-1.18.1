package org.runaway.fishing.fishes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.fishing.EFishType;
import org.runaway.fishing.Fish;

public class Burbot extends Fish {

    @Override
    public String getName() {
        return "Налим";
    }

    @Override
    public EFishType getType() {
        return EFishType.EPIC;
    }

    @Override
    public ItemStack getMaterial() {
        return new ItemStack(Material.SALMON);
    }

    @Override
    protected double getMaxWeight() {
        return 7000;
    }

    @Override
    protected double getChance() {
        return 0.5;
    }

    @Override
    protected short getIconData() {
        return 0;
    }

    @Override
    public double getPrice() {
        return 0.000046;
    }
}
